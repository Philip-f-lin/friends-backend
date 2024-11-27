package com.philip.friendsbackend.exception;

import com.philip.friendsbackend.common.ErrorCode;

/**
 * 自定義異常類
 *
 * @author Philip
 */
public class BusinessException extends RuntimeException{
    private final int code;

    private final String description;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
