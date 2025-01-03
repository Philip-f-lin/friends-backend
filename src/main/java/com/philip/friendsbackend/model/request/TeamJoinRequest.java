package com.philip.friendsbackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 加入隊伍請求
 *
 * @author Philip
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 4613528125739313750L;
    /**
     * id
     */
    private Long teamId;

    /**
     * 隊伍密碼
     */
    private String password;
}
