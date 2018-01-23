package com.hengtian.common.enums;

public enum TaskVariable {

    //任务类型
    TASKTYPE("taskType"),
    //任务节点审核人数
    USERCOUNTTOTAL("userCountTotal"),
    //任务节点需要审核人数
    USERCOUNTNEED("userCountNeed"),
    //任务节点已审核人数
    USERCOUNTNOW("userCountNow");

    public String value;

    TaskVariable(String value){
        this.value = value;
    }
}
