package com.philip.friendsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.philip.friendsbackend.model.domain.User;

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
}
