package com.philip.friendsbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的 API 返回結果封裝類。
 * @param <T>
 * @author Phlip
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }
}
