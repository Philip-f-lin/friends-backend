package com.philip.friendsbackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 使用者登入請求
 *
 * @author philip
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 7187788043978284519L;

    private String userAccount;

    private String userPassword;
}
