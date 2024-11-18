package com.philip.friendsbackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 使用者註冊請求
 *
 * @author philip
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -1179409064334697104L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
