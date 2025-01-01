package com.philip.friendsbackend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.philip.friendsbackend.model.domain.Team;
import com.philip.friendsbackend.model.domain.User;

public interface TeamService extends IService<Team> {

    /**
     * 創建隊伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);
}
