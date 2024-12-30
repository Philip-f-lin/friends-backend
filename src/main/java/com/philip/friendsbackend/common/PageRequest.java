package com.philip.friendsbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分頁請求參數
 *
 * @author Philip
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 3133366720012048852L;

    /**
     * 一頁有幾筆資料
     */
    protected int pageSize = 10;

    /**
     * 目前第幾頁
     */
    protected int pageNum = 1;
}
