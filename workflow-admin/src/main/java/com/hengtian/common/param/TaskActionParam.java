package com.hengtian.common.param;

/**
 * 任务管理操作接口参数
 * @author houjinrong@chtwm.com
 * date 2018/4/18 9:42
 */
public class TaskActionParam {

    /**
     * 操作类型
     *
     */
    private String actionType;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 当前任务ID
     */
    private String taskId;

    /**
     * 当前任务ID
     */
    private String userId;

    /**
     * 目标任务ID
     */
    private String targetTaskId;

    /**
     * 目标用户ID
     */
    private String targetUserId;

    /**
     * 目标任务节点KEY
     */
    private String targetTaskDefKey;

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetTaskId() {
        return targetTaskId;
    }

    public void setTargetTaskId(String targetTaskId) {
        this.targetTaskId = targetTaskId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetTaskDefKey() {
        return targetTaskDefKey;
    }

    public void setTargetTaskDefKey(String targetTaskDefKey) {
        this.targetTaskDefKey = targetTaskDefKey;
    }
}
