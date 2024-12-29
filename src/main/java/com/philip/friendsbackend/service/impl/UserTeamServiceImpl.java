package com.philip.friendsbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philip.friendsbackend.model.domain.UserTeam;
import com.philip.friendsbackend.service.UserTeamService;
import com.philip.friendsbackend.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author linzhuofei
* @description 针对表【user_team(使用者隊伍關係表)】的数据库操作Service实现
* @createDate 2024-12-27 15:28:22
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




