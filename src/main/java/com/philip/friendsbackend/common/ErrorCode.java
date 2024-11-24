package com.philip.friendsbackend.common;

/**
 * 錯誤碼
 *
 * @author Philip
 */
public enum ErrorCode {
    ;

    private final int code;

    private final String message;

    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }
}
