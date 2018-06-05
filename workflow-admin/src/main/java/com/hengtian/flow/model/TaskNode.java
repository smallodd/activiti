package com.hengtian.flow.model;

/**
 * 任务节点信息
 * @author houjinrong@chtwm.com
 * date 2018/6/4 15:14
 */
public class TaskNode {

    /**
     * 任务节点KEY
     */
    private String taskDefinationKey;
    /**
     * 任务节点名称
     */
    private String taskDefinationName;
    /**
     * 节点审批人
     */
    private String assignee;

    public String getTaskDefinationKey() {
        return taskDefinationKey;
    }

    public void setTaskDefinationKey(String taskDefinationKey) {
        this.taskDefinationKey = taskDefinationKey;
    }

    public String getTaskDefinationName() {
        return taskDefinationName;
    }

    public void setTaskDefinationName(String taskDefinationName) {
        this.taskDefinationName = taskDefinationName;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
