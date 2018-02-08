package com.activiti.enums;

public enum ProcessVariable {

    //流程节点所有ID，多个逗号隔开
    PROCESSNODE("processNode:");

    public String value;

    ProcessVariable(String value){
        this.value = value;
    }
}
