package com.hengtian.common.enums;

/**
 * 流程实力状态枚举类
 * @author houjinrong@chtwm.com
 * date 2018/5/23 11:01
 */
public enum ProcessStatusEnum {

    UNFINISHED(0, "未完成"),
    FINISHED_Y(1, "通过"),
    FINISHED_N(2, "未通过");

    public int status;

    public String desc;

    ProcessStatusEnum(int status, String desc){
        this.status = status;
        this.desc = desc;
    }
}
