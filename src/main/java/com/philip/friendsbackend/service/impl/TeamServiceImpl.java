package com.philip.friendsbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philip.friendsbackend.mapper.TeamMapper;
import com.philip.friendsbackend.model.domain.Team;
import com.philip.friendsbackend.service.TeamService;
import org.springframework.stereotype.Service;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

}




