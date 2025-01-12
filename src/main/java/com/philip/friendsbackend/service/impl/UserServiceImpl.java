package com.philip.friendsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.philip.friendsbackend.common.ErrorCode;
import com.philip.friendsbackend.exception.BusinessException;
import com.philip.friendsbackend.mapper.UserMapper;
import com.philip.friendsbackend.model.domain.User;
import com.philip.friendsbackend.model.vo.UserVO;
import com.philip.friendsbackend.service.UserService;
import com.philip.friendsbackend.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.apache.commons.math3.util.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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

    private UserVO convertToUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
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

    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 只允許更改自己的使用者資訊
        if (userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return (User) userObj;
    }

    @Override
    public Page<User> getRecommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = this.getLoginUser(request);
        String redisKey = String.format("philip:user:recommend:%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有緩存，直接讀緩存
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if(userPage != null){
            return userPage;
        }
        // 無緩存，查資料庫
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id", loginUser.getId()); // 排除自己
        queryWrapper.orderByDesc("create_time"); // 根據建立時間降序排序
        userPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        // 為什麼用try catch，因為即使失敗，還是可以把資料庫的資料返還給前端
        // 在這邊捕獲異常，而不是交給全局異常處理器去拋
        // 寫緩存
        try {
            valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return userPage;
    }

    @Override
    public List<UserVO> getMatchUsers(long num, User loginUser) {
        // 獲取所有具有標籤的使用者
        List<User> allUsersWithTags = this.list(
                new QueryWrapper<User>()
                        .select("id", "tags")
                        .isNotNull("tags")
                        .apply("JSON_LENGTH(tags) > 0")
        );

        // 解析當前使用者的標籤
        List<String> loginUserTags = parseTags(loginUser.getTags());
        if (loginUserTags == null || loginUserTags.isEmpty()) {
            return Collections.emptyList();
        }

        // 計算相似度
        List<Pair<Long, User>> similarityList = allUsersWithTags.stream()
                .filter(user -> isValidMatch(user, loginUser)) // 過濾掉無標籤或是自己
                .map(user -> {
                    List<String> userTags = parseTags(user.getTags());
                    long distance = AlgorithmUtils.minDistance(loginUserTags, userTags);
                    return new Pair<>(distance, user);
                })
                .sorted(Comparator.comparing(Pair::getKey)) // 根據相似度排序
                .limit(num)
                .collect(Collectors.toList());

        // 獲取推薦使用者的 ID
        List<Long> topUserIds = similarityList.stream()
                .map(pair -> pair.getValue().getId())
                .collect(Collectors.toList());

        // 獲取安全的使用者資料
        Map<Long, UserVO> safeUserMap = this.list(
                        new QueryWrapper<User>().in("id", topUserIds)
                ).stream()
                .map(this::convertToUserVO)
                .collect(Collectors.toMap(UserVO::getId, user -> user));

        // 根據排序返回推薦使用者列表
        return topUserIds.stream()
                .map(safeUserMap::get)
                .collect(Collectors.toList());
    }

    /**
     * 解析 JSON 格式的標籤
     */
    private List<String> parseTags(String tagsJson) {
        if (StringUtils.isBlank(tagsJson)) {
            return Collections.emptyList();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(tagsJson, listType);
    }

    /**
     * 判斷是否為有效的推薦匹配對象
     */
    private boolean isValidMatch(User user, User loginUser) {
        return !StringUtils.isBlank(user.getTags()) && !user.getId().equals(loginUser.getId());
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




