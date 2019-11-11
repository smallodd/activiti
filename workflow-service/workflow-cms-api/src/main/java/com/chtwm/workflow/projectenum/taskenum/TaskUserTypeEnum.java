package com.chtwm.workflow.projectenum.taskenum;

/**
 * @author fanyuexing
 * @date 2019/11/11 15:53
 * 销客任务通知实体类 userType对应的枚举
 */
public enum TaskUserTypeEnum {
    User_TYPE_DEPARTMENT(1,"部门"),
    User_TYPE_ROLE(2,"角色"),
    User_TYPE_STAFF(3,"员工"),
    User_TYPE_GROUP(4,"组");

    public int code;
    public String value;

    TaskUserTypeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
