package com.hengtian.flow.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chenzhangyan  on 2018/4/25.
 */
public class AskTaskVo implements Serializable {

    /**
     * 主键
     */
    private String id;
    /**
     * 流程实例ID
     */
    private String procInstId;
    private String executionId;
    /**
     * 流程实例名称
     */
    private String procInstName;
    /**
     * 意见征询所在任务节点ID
     */
    private String currentTaskId;
    /**
     * 意见征询所在任务节点key
     */
    private String currentTaskKey;
    /**
     * 意见征询所在任务节点名称
     */
    private String currentTaskName;
    /**
     * 被意见征询的任务节点key
     */
    private String askTaskKey;
    /**
     * 被意见征询的任务节点名称
     */
    private String askTaskName;
    /**
     * 被意见征询人的id
     */
    private String askUserId;
    /**
     * 意见征询人id
     */
    private String createId;
    /**
     * 更新人id
     */
    private String updateId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 意见征询是否结束
     */
    private Integer isAskEnd;
    /**
     * 意见征询详情
     */
    private String askComment;
    /**
     * 回复详情
     */
    private String answerComment;

    private String askedUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getProcInstName() {
        return procInstName;
    }

    public void setProcInstName(String procInstName) {
        this.procInstName = procInstName;
    }

    public String getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(String currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    public String getCurrentTaskKey() {
        return currentTaskKey;
    }

    public void setCurrentTaskKey(String currentTaskKey) {
        this.currentTaskKey = currentTaskKey;
    }

    public String getCurrentTaskName() {
        return currentTaskName;
    }

    public void setCurrentTaskName(String currentTaskName) {
        this.currentTaskName = currentTaskName;
    }

    public String getAskTaskKey() {
        return askTaskKey;
    }

    public void setAskTaskKey(String askTaskKey) {
        this.askTaskKey = askTaskKey;
    }

    public String getAskTaskName() {
        return askTaskName;
    }

    public void setAskTaskName(String askTaskName) {
        this.askTaskName = askTaskName;
    }

    public String getAskUserId() {
        return askUserId;
    }

    public void setAskUserId(String askUserId) {
        this.askUserId = askUserId;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsAskEnd() {
        return isAskEnd;
    }

    public void setIsAskEnd(Integer isAskEnd) {
        this.isAskEnd = isAskEnd;
    }

    public String getAskComment() {
        return askComment;
    }

    public void setAskComment(String askComment) {
        this.askComment = askComment;
    }

    public String getAnswerComment() {
        return answerComment;
    }

    public void setAnswerComment(String answerComment) {
        this.answerComment = answerComment;
    }

    public String getAskedUserId() {
        return askedUserId;
    }

    public void setAskedUserId(String askedUserId) {
        this.askedUserId = askedUserId;
    }

    @Override
    public String toString() {
        return "AskTaskVo{" +
                "id='" + id + '\'' +
                ", procInstId='" + procInstId + '\'' +
                ", executionId='" + executionId + '\'' +
                ", procInstName='" + procInstName + '\'' +
                ", currentTaskId='" + currentTaskId + '\'' +
                ", currentTaskKey='" + currentTaskKey + '\'' +
                ", currentTaskName='" + currentTaskName + '\'' +
                ", askTaskKey='" + askTaskKey + '\'' +
                ", askTaskName='" + askTaskName + '\'' +
                ", askUserId='" + askUserId + '\'' +
                ", createId='" + createId + '\'' +
                ", updateId='" + updateId + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isAskEnd=" + isAskEnd +
                ", askComment='" + askComment + '\'' +
                ", answerComment='" + answerComment + '\'' +
                '}';
    }
}
