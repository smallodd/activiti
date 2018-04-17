package com.activiti.enums;

public enum TaskVariable {

    //任务类型
    TASKTYPE("taskType"),
    //任务节点审核人数详情 例：{"userCountTotal":3,"userCountNeed"2:,"userCountNow":1,"userCountRefuse":1}
    USERCOUNT("userCount"),
    //任务节点审核人数
    USERCOUNTTOTAL("userCountTotal"),
    //任务节点需要审核人数
    USERCOUNTNEED("userCountNeed"),
    //任务节点已审核人数
    USERCOUNTNOW("userCountNow"),
    //任务节点已审核人数(拒绝)
    USERCOUNTREFUSE("userCountRefuse"),
    //任务节点所有审核人
    TASKUSER("taskUser"),
    //任务流程最后审核人
    LASTTASKUSER("lastTaskUser");

    public String value;

    TaskVariable(String value){
        this.value = value;
    }
}
