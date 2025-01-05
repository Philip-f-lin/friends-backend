package com.philip.friendsbackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 離開隊伍請求
 *
 * @author philip
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = 4613528125739313750L;
    /**
     * id
     */
    private Long teamId;
}
