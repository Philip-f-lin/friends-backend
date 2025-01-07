package com.philip.friendsbackend.model.enums;

/**
 * 隊伍狀態枚舉
 */
public enum TeamStatusEnum {

    PUBLIC(0, "公開"),
    SECRET(1, "私人");

    public static TeamStatusEnum getEnumByValue(Integer value){
        if (value == null){
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values) {
            if (teamStatusEnum.getValue() == value){
                return teamStatusEnum;
            }
        }
        return null;
    }


    private int value;

    private String text;

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
