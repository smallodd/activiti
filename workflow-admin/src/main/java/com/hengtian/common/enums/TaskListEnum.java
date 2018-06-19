package com.hengtian.common.enums;

/**
 * 请求返回code和提示信息
 * @author houjinrong@chtwm.com
 * date 2018/4/19 13:20
 */
public enum TaskListEnum {

    CLAIM("claim", "认领任务列表"),
    ACTIVE("active","待处理任务列表"),
    OPEN("open","代办任务列表"),
    CLOSE("close","完成任务列表");

    public String type;

    public String msg;

    TaskListEnum(String type, String msg){
        this.type = type;
        this.msg = msg;
    }
}
