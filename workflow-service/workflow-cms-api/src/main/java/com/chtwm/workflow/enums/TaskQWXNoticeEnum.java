package com.chtwm.workflow.enums;

/**
 * 企业微信通知状态
 * @author fanyuexing
 * @date 2020/4/23 9:20
 */
public enum TaskQWXNoticeEnum {

    UNSEND(0,"未通知"),
    SUCCESS(1,"通知成功"),
    FAILURE(2,"通知失败"),
    NOT_INFORM(5,"不再重试通知")
    ;

    public Integer code;
    public String value;

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    TaskQWXNoticeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

}
