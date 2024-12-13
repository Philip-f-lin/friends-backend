package com.philip.friendsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.philip.friendsbackend.common.ErrorCode;
import com.philip.friendsbackend.exception.BusinessException;
import com.philip.friendsbackend.mapper.UserMapper;
import com.philip.friendsbackend.model.domain.User;
import com.philip.friendsbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    // 鹽
    private static final String SALT = "philip";

    // 使用者登入狀態(session)
    public static final String USER_LOGIN_STATE = "userLoginSate";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 檢驗輸入的帳號密碼
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "輸入的帳號密碼為空白");
        }
        if(userAccount.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "使用者帳號需大於等於 8 位");
        }
        if(userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "使用者密碼需大於等於 8 位");
        }
        // 帳號不能包含特殊符號
        String validPattern = "[^a-zA-Z0-9]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帳號不能包含特殊符號");
        }
        // 密碼與驗證密碼不相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密碼與驗證密碼不相同");
        }
        // 帳號不能重複
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帳號不能重複");
        }
        // 2. 使用 MD5 加密
        String hashedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 創建 user 物件，並存入數據
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(hashedPassword);
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SAVE_ERROR, "使用者資料保存時出現未知錯誤");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 檢驗輸入的帳號密碼
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "輸入的帳號密碼為空白");
        }
        if(userAccount.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "使用者帳號需大於等於 8 位");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "使用者密碼需大於等於 8 位");
        }
        // 帳號不能包含特殊符號
        String validPattern = "[^a-zA-Z0-9]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            return null;
        }
        // 使用 MD5 加密
        String hashedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 判斷使用者是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", hashedPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 使用者不存在
        if (user == null){
            log.info("Login failed: the user account or password is incorrect.");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "使用者不存在");
        }
        // 去除使用者敏感資訊
        User safetyUser = getSafetyUser(user);
        // 記錄使用者登入狀態(session)
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 去除使用者敏感資訊
     * @param user
     * @return
     */
    @Override
    public User getSafetyUser(User user) {
        if (user == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setTags(user.getTags());
        return safetyUser;
    }

    /**
     * 根據標籤搜索使用者
     *
     * @param tagNameList 使用者擁有的標籤
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1. 查詢所有使用者
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 2. 在內存中判斷是否包含要求的標籤
        return userList.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr)) {
                return false;
            }
            // 用 fromJson 將字串，反序列化成對象
            Set<String> tempTagNameSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            // 如果要將字串加密成字串
            //String gsonJsonStr = gson.toJson(tempTagNameList);
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 使用者登出
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




