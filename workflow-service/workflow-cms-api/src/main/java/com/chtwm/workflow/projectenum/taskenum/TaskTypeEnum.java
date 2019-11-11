package com.chtwm.workflow.projectenum.taskenum;

/**
 * @author fanyuexing
 * @date 2019/11/11 15:57
 * 销客任务通知实体类 type对应的枚举
 */
public enum TaskTypeEnum {
    TYPE_EXAMINE(0,"审批"),
    TASK_TURN(1,"转办"),
    TASK_ASK(2,"问询"),
    TASK_REPLY(3,"回复");

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

    TaskTypeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
