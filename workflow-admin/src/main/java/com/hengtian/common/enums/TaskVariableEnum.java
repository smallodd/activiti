package com.hengtian.common.enums;

public enum TaskVariableEnum {

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
    LASTTASKUSER("lastTaskUser"),




    //已经审批过的总数量
    APPROVE_COUNT("approveCount"),
    //设置的任务节点审批人总数量
    APPROVE_COUNT_TOTAL("approveCountTotal"),
    //任务节点需要审批人数量
    APPROVE_COUNT_NEED("approveCountNeed"),
    //任务节点已审批通过的数量
    APPROVE_COUNT_NOW("approveCountNow"),
    //任务节点已审批拒绝的数量
    APPROVE_COUNT_REFUSE("approveCountRefuse");

    public String value;

    TaskVariableEnum(String value){
        this.value = value;
    }
}
