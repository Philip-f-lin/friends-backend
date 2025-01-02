package com.philip.friendsbackend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.philip.friendsbackend.model.domain.Team;
import com.philip.friendsbackend.model.domain.User;
import com.philip.friendsbackend.model.dto.TeamQuery;
import com.philip.friendsbackend.model.request.TeamUpdateRequest;
import com.philip.friendsbackend.model.vo.TeamUserVO;

import java.util.List;

public interface TeamService extends IService<Team> {

    /**
     * 創建隊伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 查詢隊伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery);

    /**
     * 更新隊伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);
}
