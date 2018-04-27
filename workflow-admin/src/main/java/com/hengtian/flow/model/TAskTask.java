package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;


import java.util.Date;

/**
 * Created by ma on 2018/4/19.
 */
@TableName("t_ask_task")
public class TAskTask {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "proc_inst_id")
    private String procInstId;
    @TableField(value = "current_task_id")
    private String currentTaskId;
    @TableField(value = "current_task_key")
    private String currentTaskKey;
    @TableField(value = "ask_task_key")
    private String askTaskKey;
    @TableField(value = "is_ask_end")
    private Integer isAskEnd;
    @TableField(value = "create_time")
    private Date createTime;
    @TableField(value = "update_time")
    private Date updateTime;
    @TableField(value = "create_id")
    private String createId;
    @TableField(value = "update_id")
    private String updateId;
    @TableField(value = "ask_user_id")
    private String askUserId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getIsAskEnd() {
        return isAskEnd;
    }

    public void setIsAskEnd(Integer isAskEnd) {
        this.isAskEnd = isAskEnd;
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

    public String getAskUserId() {
        return askUserId;
    }

    public void setAskUserId(String askUserId) {
        this.askUserId = askUserId;
    }

    @Override
    public String toString() {
        return "TAskTask{" +
                "id=" + id +
                ", procInstId='" + procInstId + '\'' +
                ", currentTaskId='" + currentTaskId + '\'' +
                ", currentTaskKey='" + currentTaskKey + '\'' +
                ", askTaskKey='" + askTaskKey + '\'' +
                ", isAskEnd=" + isAskEnd +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createId='" + createId + '\'' +
                ", updateId='" + updateId + '\'' +
                ", askUserId='" + askUserId + '\'' +
                '}';
    }
}
