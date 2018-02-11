package com.activiti.enums;

public enum TaskType {
    //审核人
    ASSIGNEE("assignee"),
    //会签
    COUNTERSIGN("counterSign"),
    //候选人
    CANDIDATEUSER("candidateUser"),
    //候选组
    CANDIDATEGROUP("candidateGroup");

    public String value;

    TaskType(String value){
        this.value = value;
    }
}
