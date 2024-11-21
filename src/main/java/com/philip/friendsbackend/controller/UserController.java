package com.philip.friendsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.philip.friendsbackend.model.domain.User;
import com.philip.friendsbackend.model.request.UserLoginRequest;
import com.philip.friendsbackend.model.request.UserRegisterRequest;
import com.philip.friendsbackend.service.UserService;
import com.philip.friendsbackend.utils.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author philip
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request);
    }

    @PostMapping("/logout")
    public Integer userLogout(HttpServletRequest request){
        if (request == null) {
            return null;
        }
       return userService.userLogout(request);
    }

    @GetMapping("/current")
    public User getCurrentUser(){
        // 如果使用者資訊有變化，使用 UserHolder.getUser() 會拿到舊的資訊
        User currentUser = UserHolder.getUser();
        long userId = currentUser.getId();
        // 因此在查詢一次資料庫拿到最新使用者資訊
        User user = userService.getById(userId);
        // 去除使用者敏感資訊
        return userService.getsafetyUser(user);
    }

    @GetMapping("/search")
    public List<User> searchUsers(String username){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        return userList.stream()
                .map(user -> userService.getsafetyUser(user))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/delete")
    public boolean deleteUser(@RequestBody long id){
        if (id <= 0){
            return false;
        }
        return userService.removeById(id);
    }
}
