package com.chtwm.workflow.enums;

/**
 * @author fanyuexing
 * @date 2019/11/11 15:53
 * 销客任务通知实体类 userType对应的枚举
 */
public enum TaskUserTypeEnum {
    DEPARTMENT(1,"部门"),
    ROLE(2,"角色"),
    EMP(3,"员工"),
    GROUP(4,"组");

    public Integer code;
    public String value;

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    TaskUserTypeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}