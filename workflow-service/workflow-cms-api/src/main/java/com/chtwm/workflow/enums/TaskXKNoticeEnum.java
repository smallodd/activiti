package com.chtwm.workflow.enums;

/**
 * @author fanyuexing
 * @date 2019/11/11 16:06
 * 销客任务通知实体类 xkNoticeState（销客通知状态）对应的枚举
 */
public enum TaskXKNoticeEnum {

    UNSEND(0,"未通知"),
    SUCCESS(1,"通知成功"),
    FAILURE(2,"通知失败");

    public int code;
    public String value;

    TaskXKNoticeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
