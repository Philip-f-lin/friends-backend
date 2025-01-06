package com.philip.friendsbackend.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 隊伍
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 使用者 id ( 同時為隊伍隊長 id )
     */
    private Long userId;

    /**
     * 0 - 公開，1 - 加密(私人)
     */
    private Integer status;

    /**
     * 密碼
     */
    private String password;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}