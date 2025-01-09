package com.philip.friendsbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philip.friendsbackend.model.domain.UserTeam;
import com.philip.friendsbackend.service.UserTeamService;
import com.philip.friendsbackend.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




