package com.chtwm.workflow.enums;

/**
 * @author fanyuexing
 * @date 2019/11/11 15:57
 * 销客任务通知实体类 type对应的枚举
 */
public enum TaskTypeEnum {
    APPROVE(0,"审批"),
    TURN(1,"转办"),
    ASK(2,"问询"),
    REPLY(3,"回复");

    public Integer code;
    public String value;

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    TaskTypeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    /**
     * 传入code 返回code对应的value
     * @param code
     * @return
     */
    public static TaskTypeEnum of (Integer code){
        if (code == null){
            return null;
        }
        for (TaskTypeEnum taskTypeEnum : TaskTypeEnum.values()) {
            if (taskTypeEnum.getCode().equals(code)){
                return taskTypeEnum;
            }
        }
        return null;
    }

}
