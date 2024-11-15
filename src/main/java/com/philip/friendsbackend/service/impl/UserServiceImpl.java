package com.philip.friendsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.philip.friendsbackend.mapper.UserMapper;
import com.philip.friendsbackend.model.domain.User;
import com.philip.friendsbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 檢驗輸入的帳號密碼
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            return -1;
        }

        if(userAccount.length() < 8){
            return -1;
        }

        if(userPassword.length() < 8 || checkPassword.length() < 8){
            return -1;
        }

        // 帳號不能包含特殊符號
        String validPattern = "[^a-zA-Z0-9]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            return -1;
        }

        // 密碼與驗證密碼不相同
        if(!userPassword.equals(checkPassword)){
            return -1;
        }

        // 帳號不能重複
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            return -1;
        }

        // 2. 使用 MD5 哈希加密
        final String SALT = "philip";
        StringBuilder hashedPassword = new StringBuilder();
        DigestUtils.appendMd5DigestAsHex((SALT + userPassword).getBytes(), hashedPassword);

        // 3. 創建 user 物件，並存入數據
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(hashedPassword.toString());
        boolean saveResult = this.save(user);
        if(!saveResult){
            return -1;
        }

        return user.getId();
    }
}




