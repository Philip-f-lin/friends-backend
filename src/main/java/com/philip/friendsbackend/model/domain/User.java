package com.philip.friendsbackend.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 使用者
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private long id;

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
     * 密碼
     */
    private String userPassword;

    /**
     * 電話
     */
    private String phone;

    /**
     * 電子郵件
     */
    private String email;

    /**
     * 狀態 0正常
     */
    private Integer userStatus;

    /**
     * 創建時間
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 是否刪除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 標籤 Json 列表
     */
    private String tags;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}