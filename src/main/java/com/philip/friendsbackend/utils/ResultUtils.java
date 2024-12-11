package com.philip.friendsbackend.utils;

import com.philip.friendsbackend.common.BaseResponse;
import com.philip.friendsbackend.common.ErrorCode;

/**
 * 用於封裝統一 API 返回結果的工具類。
 * 提供快捷方法生成成功或失敗的返回對象。
 *
 * @author Philip
 */
public class ResultUtils {

    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, data, "成功");
    }

    /**
     * 失敗
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    /**
     *
     * @param errorCode
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(int errorCode, String message, String description){
        return new BaseResponse<>(errorCode, null, message, description);
    }

    /**
     * 失敗
     * @param errorCode
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message, String description){
        return new BaseResponse<>(errorCode.getCode(), null, message, description);
    }

}
