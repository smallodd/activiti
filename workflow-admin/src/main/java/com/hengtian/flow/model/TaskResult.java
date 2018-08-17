package com.hengtian.flow.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hengtian.flow.vo.AssigneeVo;
import com.hengtian.flow.vo.TaskNodeVo;

import java.util.Date;
import java.util.List;

/**
 * 任务信息-返回值
 */
public class TaskResult {

    private static final long serialVersionUID = 1L;

    /**
     * 任务节点ID
     */
    private String taskId;
    /**
     * 任务节点名称
     */
    private String taskName;
    /**
     * 任务节点状态(1.待签收   2.待受理[办理,转办,委派,跳转])
     */
    private String taskState;
    /**
     * 当前处理人
     */
    private String assignee;

    private String assigneeName;
    /**
     * 前一步处理人
     */
    private String assigneeBefore;

    private String assigneeBeforeName;
    /**
     * 后一步处理人
     */
    private String assigneeNext;

    private String assigneeNextName;
    /**
     * 委托人
     */
    private String assigneeDelegate;
    /**
     * 流程创建人编号
     */
    private String creatorCode;
    /**
     * 流程创建人名称
     */
    private String creatorName;
    /**
     * 流程创建人所属部门编号
     */
    private String creatorDeptCode;
    /**
     * 流程创建人所属部门名称
     */
    private String creatorDeptName;
    /**
     * 流程编号
     */
    private String processInstanceId;
    /**
     * 流程标题
     */
    private String processInstanceTitle;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 流程名称
     */
    private String processDefinitionName;
    /**
     * 流程状态
     */
    private String processInstanceState;
    /**
     * 业务主键
     */
    private String businessKey;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    /**
     * 是否是意见征询的任务 0-待审批任务；1-意见征询任务
     */
    private int asked;
    /**
     * 意见征询主键
     */
    private String askId;
    /**
     * 被意见征询的节点
     */
    private String  askTaskKey;

    public String getAskTaskKey() {
        return askTaskKey;
    }

    public void setAskTaskKey(String askTaskKey) {
        this.askTaskKey = askTaskKey;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getAssigneeBeforeName() {
        return assigneeBeforeName;
    }

    public void setAssigneeBeforeName(String assigneeBeforeName) {
        this.assigneeBeforeName = assigneeBeforeName;
    }

    public String getAssigneeNextName() {
        return assigneeNextName;
    }

    public void setAssigneeNextName(String assigneeNextName) {
        this.assigneeNextName = assigneeNextName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeBefore() {
        return assigneeBefore;
    }

    public void setAssigneeBefore(String assigneeBefore) {
        this.assigneeBefore = assigneeBefore;
    }

    public String getAssigneeNext() {
        return assigneeNext;
    }

    public void setAssigneeNext(String assigneeNext) {
        this.assigneeNext = assigneeNext;
    }

    public String getAssigneeDelegate() {
        return assigneeDelegate;
    }

    public void setAssigneeDelegate(String assigneeDelegate) {
        this.assigneeDelegate = assigneeDelegate;
    }

    public String getCreatorCode() {
        return creatorCode;
    }

    public void setCreatorCode(String creatorCode) {
        this.creatorCode = creatorCode;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorDeptCode() {
        return creatorDeptCode;
    }

    public void setCreatorDeptCode(String creatorDeptCode) {
        this.creatorDeptCode = creatorDeptCode;
    }

    public String getCreatorDeptName() {
        return creatorDeptName;
    }

    public void setCreatorDeptName(String creatorDeptName) {
        this.creatorDeptName = creatorDeptName;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceTitle() {
        return processInstanceTitle;
    }

    public void setProcessInstanceTitle(String processInstanceTitle) {
        this.processInstanceTitle = processInstanceTitle;
    }

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
    }

    public String getProcessInstanceState() {
        return processInstanceState;
    }

    public void setProcessInstanceState(String processInstanceState) {
        this.processInstanceState = processInstanceState;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public int getAsked() {
        return asked;
    }

    public void setAsked(int asked) {
        this.asked = asked;
    }

    public String getAskId() {
        return askId;
    }

    public void setAskId(String askId) {
        this.askId = askId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
}
