package com.philip.friendsbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.philip.friendsbackend.common.BaseResponse;
import com.philip.friendsbackend.common.ErrorCode;
import com.philip.friendsbackend.exception.BusinessException;
import com.philip.friendsbackend.model.domain.User;
import com.philip.friendsbackend.model.request.UserLoginRequest;
import com.philip.friendsbackend.model.request.UserRegisterRequest;
import com.philip.friendsbackend.service.UserService;
import com.philip.friendsbackend.utils.ResultUtils;
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null) {
            return null;
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(){
        // 如果使用者資訊有變化，使用 UserHolder.getUser() 會拿到舊的資訊
        User currentUser = UserHolder.getUser();
        long userId = currentUser.getId();
        // 因此在查詢一次資料庫拿到最新使用者資訊
        User user = userService.getById(userId);
        // 去除使用者敏感資訊
        User safetyUser = userService.getsafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream()
                .map(user -> userService.getsafetyUser(user))
                .collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id){
        if (id <= 0){
            return null;
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }
}
