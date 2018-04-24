package com.hengtian.enquire.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 问询任务
 *
 * @author chenzhangyan  on 2018/4/20.
 */
@TableName("t_enquire_task")
public class EnquireTask implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private long id;

    /**
     * 流程实例ID
     */
    @TableField(value = "proc_inst_id")
    private String procInstId;

    /**
     * 问询所在任务节点ID
     */
    @TableField(value = "current_task_id")
    private String currentTaskId;

    /**
     * 问询所在任务节点key
     */
    @TableField(value = "current_task_key")
    private String currentTaskKey;

    /**
     * 被问询的任务节点key
     */
    @TableField(value = "ask_task_key")
    private String askTaskKey;

    /**
     * 问询是否结束
     */
    @TableField(value = "is_ask_end")
    private int isAskEnd;

    /**
     * 被问询人的id
     */
    @TableField(value = "ask_user_id")
    private String askUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 问询人id
     */
    @TableField(value = "create_id")
    private String createId;

    /**
     * 更新人id
     */
    @TableField(value = "update_id")
    private String updateId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
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

    public String getAskTaskKey() {
        return askTaskKey;
    }

    public void setAskTaskKey(String askTaskKey) {
        this.askTaskKey = askTaskKey;
    }

    public int getIsAskEnd() {
        return isAskEnd;
    }

    public void setIsAskEnd(int isAskEnd) {
        this.isAskEnd = isAskEnd;
    }

    public String getAskUserId() {
        return askUserId;
    }

    public void setAskUserId(String askUserId) {
        this.askUserId = askUserId;
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

    @Override
    public String toString() {
        return "EnquireTask{" +
                "id=" + id +
                ", procInstId='" + procInstId + '\'' +
                ", currentTaskId='" + currentTaskId + '\'' +
                ", currentTaskKey='" + currentTaskKey + '\'' +
                ", askTaskKey='" + askTaskKey + '\'' +
                ", isAskEnd=" + isAskEnd +
                ", askUserId='" + askUserId + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createId='" + createId + '\'' +
                ", updateId='" + updateId + '\'' +
                '}';
    }
}
