package com.chtwm.workflow.projectenum.taskenum;

/**
 * @author fanyuexing
 * @date 2019/11/11 16:06
 * 销客任务通知实体类 xkNoticeState（销客通知状态）对应的枚举
 */
public enum TaskXKNoticeEnum {

    XK_NOTICE_UNSEND(0,"未通知"),
    XK_NOTICE_SUCCESS(1,"通知成功"),
    XK_NOTICE_FAILURE(2,"通知失败");

    public int code;
    public String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    TaskXKNoticeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
