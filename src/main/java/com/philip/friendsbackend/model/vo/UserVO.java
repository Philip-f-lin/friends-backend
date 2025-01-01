package com.philip.friendsbackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 使用者資訊包裝類
 *
 * @author philip
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = -4099953825702917248L;

    /**
     * id
     */
    private Long id;

    /**
     * 使用者名稱
     */
    private String username;

    /**
     * 帳號
     */
    private String userAccount;

    /**
     * 大頭照
     */
    private String avatarUrl;

    /**
     * 性別
     */
    private Integer gender;

    /**
     * 電話
     */
    private String phone;

    /**
     * 電子郵件
     */
    private String email;

    /**
     * 標籤列表 json
     */
    private String tags;

    /**
     * 狀態 0 正常
     */
    private Integer userStatus;

    /**
     * 創建時間
     */
    private Date createTime;

    /**
     * 更新時間
     */
    private Date updateTime;
}