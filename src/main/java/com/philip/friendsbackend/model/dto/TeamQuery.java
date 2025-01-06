package com.philip.friendsbackend.model.dto;

import com.philip.friendsbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 隊伍查詢封裝類
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * id List
     */
    private List<Long> idList;

    /**
     * 搜尋關鍵字(同時對隊伍名稱和簡介搜索)
     */
    private String searchText;

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
}
