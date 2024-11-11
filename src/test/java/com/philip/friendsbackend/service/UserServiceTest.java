package com.philip.friendsbackend.service;

import com.philip.friendsbackend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Date;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("Test");
        user.setUserAccount("test123");
        user.setAvatarUrl("123");
        user.setGender(0);
        user.setUserPassword("12345678");
        user.setPhone("0912345678");
        user.setEmail("test@gmail.com");
        user.setTags("test");


        userService.save(user);
    }
}