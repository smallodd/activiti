package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

/**
 * 催办任务
 * @author houjinrong@chtwm.com
 * date 2018/4/19 10:18
 */
@TableName("t_remind_task")
public class RemindTask {

    /**
     * 主键
     */
    @TableId(type = IdType.UUID)
    private long id;
    /**
     * 流程实例ID
     */
    @TableField(value = "proc_inst_id")
    private String procInstId;
    /**
     * 任务ID
     */
    @TableField(value = "task_id")
    private String taskId;
    /**
     * 任务名称
     */
    @TableField(value = "task_name")
    private String taskName;
    /**
     * 任务节点key
     */
    @TableField(value = "task_def_key")
    private String taskDefKey;
    /**
     * 催办人工号
     */
    @TableField(value = "reminder_id")
    private String reminderId;
    /**
     * 催办任务是否完成
     */
    @TableField(value = "is_complete")
    private int isComplete;
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

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public void setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
    }

    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    public int getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(int isComplete) {
        this.isComplete = isComplete;
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
}
