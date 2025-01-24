package com.philip.friendsbackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 隊伍和使用者資訊包裝類
 *
 * @author philip
 */
@Data
public class TeamUserVO implements Serializable {

    private static final long serialVersionUID = 8785394007192173075L;

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
     * 最大人數
     */
    private Integer maxNum;

    /**
     * 使用者 id
     */
    private Long userId;

    /**
     * 0 - 公開，1 - 加密(私人)
     */
    private Integer status;

    /**
     * 創建時間
     */
    private Date createTime;

    /**
     * 更新時間
     */
    private Date updateTime;

    /**
     * 創建隊伍使用者資訊
     */
    private UserVO createUser;

    /**
     * 已加入的使用者數量
     */
    private Integer hasJoinNum;

    /**
     * 是否已加入隊伍
     */
    private boolean hasJoin = false;
}
