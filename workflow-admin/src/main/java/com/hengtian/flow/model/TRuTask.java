package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ma on 2018/4/17.
 * 扩展任务表信息
 */
@TableName("t_ru_task")
public class TRuTask implements Serializable {
    @TableId(type = IdType.UUID)
    private String id;
    @TableField(value = "task_id")
    private String taskId;
    @TableField(value = "assignee")
    private String assignee;
    @TableField(value = "assignee_type")
    private Integer assigneeType;
    @TableField(value = "task_type")
    private String taskType;
    @TableField(value = "owner")
    private String ower;
    @TableField(value = "expire_time")
    private Date expireTime;
    @TableField(value = "updater")
    private String updater;
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "app_key")
    private Integer appKey;
    @TableField(value = "assignee_real")
    private String assigneeReal;
    @TableField(value = "proc_inst_id")
    private String procInstId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Integer getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(Integer assigneeType) {
        this.assigneeType = assigneeType;
    }

    public String getAssigneeReal() {
        return assigneeReal;
    }

    public void setAssigneeReal(String assigneeReal) {
        this.assigneeReal = assigneeReal;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getOwer() {
        return ower;
    }

    public void setOwer(String ower) {
        this.ower = ower;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAppKey() {
        return appKey;
    }

    public void setAppKey(Integer appKey) {
        this.appKey = appKey;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }
}
