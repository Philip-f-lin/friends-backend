package com.philip.friendsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philip.friendsbackend.common.ErrorCode;
import com.philip.friendsbackend.exception.BusinessException;
import com.philip.friendsbackend.mapper.TeamMapper;
import com.philip.friendsbackend.model.domain.Team;
import com.philip.friendsbackend.model.domain.User;
import com.philip.friendsbackend.model.domain.UserTeam;
import com.philip.friendsbackend.model.dto.TeamQuery;
import com.philip.friendsbackend.model.enums.TeamStatusEnum;
import com.philip.friendsbackend.model.request.TeamUpdateRequest;
import com.philip.friendsbackend.model.vo.TeamUserVO;
import com.philip.friendsbackend.model.vo.UserVO;
import com.philip.friendsbackend.service.TeamService;
import com.philip.friendsbackend.service.UserService;
import com.philip.friendsbackend.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        // 1. 請求參數是否為 null
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 2. 是否登入
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final long userId = loginUser.getId();
        // 3. 檢驗資訊
        //    a. 隊伍人數 >= 1 且 <= 10
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "隊伍人數有誤");
        }
        //    b. 隊伍名稱 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "隊伍名稱有誤");
        }
        //    c. 簡介 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "隊伍簡介長度有誤");
        }
        //    d. status 是否公開，默認為 0 公開
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "隊伍狀態有誤");
        }
        //    e. 如果 status 為加密狀態，一定要有密碼，且密碼 <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "設置密碼有誤");
            }
        }
        // 使用分布式鎖，避免重複建立多個隊伍
        RLock lock = redissonClient.getLock("philip:teamserviceimpl:addteam:lock" + userId);
        try {
            if (lock.tryLock(0, 30000L, TimeUnit.MILLISECONDS)) {
                QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId);
                long hasTeamNum = this.count(queryWrapper);
                //    f. 檢驗使用者最多只能建立 5 個隊伍
                if (hasTeamNum >= 5) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "使用者最多只能建立 5 個隊伍");
                }
                //    g. 插入隊伍資訊到隊伍表
                team.setId(null);
                team.setUserId(userId);
                boolean result = this.save(team);
                Long teamId = team.getId();
                if (!result || teamId == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "創建隊伍失敗");
                }
                //    h. 插入使用者與隊伍的關係到使用者隊伍關係表
                UserTeam userTeam = new UserTeam();
                userTeam.setUserId(userId);
                userTeam.setTeamId(teamId);
                userTeam.setJoinTime(new Date());
                result = userTeamService.save(userTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "創建隊伍失敗");
                }
                return teamId;
            } else {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "系統繁忙，請稍後重試");
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "獲取分布式鎖失敗");
        } finally {
            // 確保只有當前線程持有鎖時才解鎖
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unlock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery) {
        // 合併查詢條件
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            // 根據最大人數查詢
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("max_num", maxNum);
            }
            Long userId = teamQuery.getUserId();
            // 根據使用者 id 查詢
            if (userId != null && userId > 0) {
                queryWrapper.eq("user_id", userId);
            }
            // 根據狀態查詢
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 關聯查詢創建者資訊
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 去除使用者敏感資訊
            UserVO userVO = new UserVO();
            if (user != null) {
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "隊伍不存在");
        }
        // 只有創建隊伍的人可以更新
        if (!oldTeam.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房間須設置密碼");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }
}




