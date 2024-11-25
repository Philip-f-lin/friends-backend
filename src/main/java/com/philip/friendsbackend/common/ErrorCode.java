package com.philip.friendsbackend.common;

/**
 * 錯誤碼
 *
 * @author Philip
 */
public enum ErrorCode {
    SUCCESS(0, "成功", ""),
    PARAMS_ERROR(40000, "請求參數錯誤", ""),
    NULL_ERROR(40001, "請求資料為 null", ""),
    NOT_LOGIN(40100, "尚未登入", ""),
    NO_AUTH(40101, "沒有權限", "");

    /**
     * 錯誤碼
     */
    private final int code;

    /**
     * 訊息
     */
    private final String message;

    /**
     * 描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
