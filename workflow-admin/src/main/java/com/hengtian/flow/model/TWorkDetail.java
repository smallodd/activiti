package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作记录详情
 *
 * @author chenzhangyan  on 2018/4/24.
 */
@TableName("t_work_detail")
public class TWorkDetail implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.UUID)
    private long id;

    /**
     * 流程实例ID
     */
    @TableField(value = "proc_inst_id")
    private String processInstanceId;

    /**
     * 流程实例ID
     */
    @TableField(value = "task_id")
    private String taskId;

    /**
     * 操作人
     */
    @TableField(value = "operator")
    private String operator;
    /**
     * 操作详情
     */
    @TableField(value = "detail")
    private String detail;
    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TWorkDetail{" +
                "id=" + id +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", operator='" + operator + '\'' +
                ", detail='" + detail + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
