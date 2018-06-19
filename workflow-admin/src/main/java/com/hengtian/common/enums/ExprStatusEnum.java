package com.hengtian.common.enums;

public enum ExprStatusEnum {

    UNUSABLE(0, "停用"),
    USABLE(1, "启用");

    public int status;

    public String desc;

    ExprStatusEnum(int status, String desc){
        this.status = status;
        this.desc = desc;
    }

}
