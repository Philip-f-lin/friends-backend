package com.philip.friendsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.philip.friendsbackend.model.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @author philip
 */
public interface UserService extends IService<User> {

    /**
     * 使用者註冊
     * @param userAccount 使用者帳號
     * @param userPassword 使用者密碼
     * @param checkPassword 檢驗密碼
     * @return 使用者 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 使用者登入
     *
     * @param userAccount  使用者帳號
     * @param userPassword 使用者密碼
     * @param request
     * @return 去除敏感訊息後使用者訊息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);
}
