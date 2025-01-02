package com.philip.friendsbackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 隊伍更新請求
 *
 * @author philip
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -1759021272543128929L;

    /**
     * id
     */
    private Long id;

    /**
     * 隊伍名稱
     */
    private String name;

    /**
     * 簡介
     */
    private String description;

    /**
     * 0 - 公開，1 - 非公開，2 - 加密
     */
    private Integer status;

    /**
     * 隊伍密碼
     */
    private String password;
}
