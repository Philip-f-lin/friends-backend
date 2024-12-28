package com.philip.friendsbackend.service;

import com.philip.friendsbackend.model.domain.Team;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class TeamServiceTest {

    @Resource
    private TeamService teamService;

    @Test
    void teamTest(){
        Team team = new Team();
        team.setId(1L);
        team.setName("teamTest");
        team.setDescription("good");
        team.setMaxNum(5);
        team.setUserId(1L);
        team.setStatus(0);
        team.setPassword("12345678");
        team.setIsDelete(0);
        teamService.save(team);

    }
}