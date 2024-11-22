package com.philip.friendsbackend.utils;

import com.philip.friendsbackend.common.BaseResponse;

/**
 * 用於封裝統一 API 返回結果的工具類。
 * 提供快捷方法生成成功或失敗的返回對象。
 *
 * @author Philip
 */
public class ResultUtils {

    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, data, "ok");
    }
}
