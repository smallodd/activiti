package com.hengtian.common.enums;

public enum ExprEnum {

    LEADER("leader", "上级节点审批人直接领导"),
    LEADER_CREATOR("leader_creator", "申请人领导"),
    CREATOR("creator", "申请人");

    public String expr;

    public String desc;

    ExprEnum(String expr, String desc){
        this.expr = expr;
        this.desc = desc;
    }
}
