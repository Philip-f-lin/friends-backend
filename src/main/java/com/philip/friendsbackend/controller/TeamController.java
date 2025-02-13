package com.philip.friendsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.philip.friendsbackend.common.BaseResponse;
import com.philip.friendsbackend.common.ErrorCode;
import com.philip.friendsbackend.exception.BusinessException;
import com.philip.friendsbackend.model.domain.Team;
import com.philip.friendsbackend.model.domain.User;
import com.philip.friendsbackend.model.domain.UserTeam;
import com.philip.friendsbackend.model.dto.TeamQuery;
import com.philip.friendsbackend.model.request.*;
import com.philip.friendsbackend.model.vo.TeamUserVO;
import com.philip.friendsbackend.service.TeamService;
import com.philip.friendsbackend.service.UserService;
import com.philip.friendsbackend.service.UserTeamService;
import com.philip.friendsbackend.utils.ResultUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request){
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失敗");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery);
        // 獲取隊伍的 ID 列表
        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 判斷當前使用者是否已加入隊伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            // 如果使用者已登入，查詢其已加入的隊伍
            User loginUser = userService.getLoginUser(request);
            userTeamQueryWrapper.eq("user_id", loginUser.getId());
            userTeamQueryWrapper.in("team_id", teamIdList);
            // 獲取使用者已加入的隊伍關係數據
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            // 已加入的隊伍 ID 集合
            Set<Long> hasJoinTeamSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            // 標記每個隊伍是否已加入
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        }catch (Exception e){}
        // 查詢已加入隊伍的人數
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("team_id", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        // 隊伍 ID，加入這個隊伍的使用者列表
        teamList.forEach(team -> {
            team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size());
        });
        return ResultUtils.success(teamList);
    }

    @GetMapping("/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest, HttpServletRequest request){
        if (teamDeleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(teamDeleteRequest.getTeamId(), loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解散失敗");
        }
        return ResultUtils.success(true);
    }

    /**
     * 取得我創建的隊伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery);
        if (teamList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 獲取隊伍 ID 列表
        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 查詢這些隊伍的加入人數
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("team_id", teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);
        // 統計每個隊伍的加入人數
        Map<Long, Long> teamJoinCountMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId, Collectors.counting()));
        // 設置 hasJoinNum，標記當前使用者為隊長（創建者）
        teamList.forEach(team -> {
            team.setHasJoinNum(teamJoinCountMap.getOrDefault(team.getId(), 0L).intValue());
            team.setHasJoin(true); // 既然是創建的隊伍，肯定是加入狀態
        });
        return ResultUtils.success(teamList);
    }

    /**
     * 取得我加入的隊伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 獲取當前登入用戶
        User loginUser = userService.getLoginUser(request);
        // 查詢當前用戶加入的隊伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 獲取當前用戶加入的隊伍 ID 列表
        Set<Long> idSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
        if (idSet.isEmpty()) {
            return ResultUtils.success(Collections.emptyList());
        }
        // 設置查詢條件
        teamQuery.setIdList(new ArrayList<>(idSet));
        // 查詢隊伍列表
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery);
        // 查詢已加入隊伍的人數
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("team_id", idSet);
        List<UserTeam> allUserTeams = userTeamService.list(userTeamJoinQueryWrapper);
        // 按隊伍 ID 分組計算人數
        Map<Long, Long> teamJoinCountMap = allUserTeams.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId, Collectors.counting()));
        // 設置 `hasJoin` 和 `hasJoinNum`
        teamList.forEach(team -> {
            team.setHasJoin(true); // 這個方法只查詢當前用戶已加入的隊伍，所以必然是 true
            team.setHasJoinNum(teamJoinCountMap.getOrDefault(team.getId(), 0L).intValue());
        });
        return ResultUtils.success(teamList);
    }
}
