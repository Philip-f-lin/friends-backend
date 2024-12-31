package com.philip.friendsbackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 隊伍新增請求
 *
 * @author philip
 */
@Data
public class TeamAddRequest implements Serializable {

    private static final long serialVersionUID = 5973148432982317112L;
    /**
     * 隊伍名稱
     */
    private String name;

    /**
     * 簡介
     */
    private String description;

    /**
     * 最大人數
     */
    private Integer maxNum;

    /**
     * 使用者 id
     */
    private Long userId;

    /**
     * 0 - 公開，1 - 非公開，2 - 加密
     */
    private Integer status;

    /**
     * 密碼
     */
    private String password;
}
