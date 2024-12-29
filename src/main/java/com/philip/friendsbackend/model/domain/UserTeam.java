package com.philip.friendsbackend.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 使用者隊伍關係表
 * @TableName user_team
 */
@TableName(value ="user_team")
@Data
public class UserTeam implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 使用者 id
     */
    private Long userId;

    /**
     * 隊伍 id
     */
    private Long teamId;

    /**
     * 加入時間
     */
    private Date joinTime;

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