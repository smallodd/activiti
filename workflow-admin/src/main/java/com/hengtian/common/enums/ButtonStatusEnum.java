package com.hengtian.common.enums;

public enum ButtonStatusEnum {

    UNUSABLE(0, "停用"),
    USABLE(1, "启用");

    public int status;

    public String desc;

    ButtonStatusEnum(int status, String desc){
        this.status = status;
        this.desc = desc;
    }

}
