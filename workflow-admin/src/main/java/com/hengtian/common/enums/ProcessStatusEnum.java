package com.hengtian.common.enums;

public enum ProcessStatusEnum {

    FINISHED(0, "未完成"),
    UNFINISHED(1, "完成");

    public int status;

    public String desc;

    ProcessStatusEnum(int status, String desc){
        this.status = status;
        this.desc = desc;
    }
}
