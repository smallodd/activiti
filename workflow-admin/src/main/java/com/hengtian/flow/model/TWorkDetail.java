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
     * 操作人编号
     */
    @TableField(exist = false)
    private String operatorCode;

    /**
     * 操作人姓名
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

    @TableField(value = "business_key")
    private String businessKey;
    /**
     * 操作动作
     */
    @TableField(value = "oper_action")
    private String operateAction;
    /**
     * 操作节点
     */
    @TableField(value = "oper_task_key")
    private String operTaskKey;
    /**
     * 审批意见
     */
    @TableField(value = "aprrove_info")
    private String aprroveInfo;


    @Override
    public String toString() {
        return "TWorkDetail{" +
                "id=" + id +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", operatorCode='" + operatorCode + '\'' +
                ", operator='" + operator + '\'' +
                ", detail='" + detail + '\'' +
                ", createTime=" + createTime +
                ", businessKey='" + businessKey + '\'' +
                ", operateAction='" + operateAction + '\'' +
                ", operTaskKey='" + operTaskKey + '\'' +
                ", aprroveInfo='" + aprroveInfo + '\'' +
                '}';
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperateAction() {
        return operateAction;
    }

    public void setOperateAction(String operateAction) {
        this.operateAction = operateAction;
    }

    public String getOperTaskKey() {
        return operTaskKey;
    }

    public void setOperTaskKey(String operTaskKey) {
        this.operTaskKey = operTaskKey;
    }

    public String getAprroveInfo() {
        return aprroveInfo;
    }

    public void setAprroveInfo(String aprroveInfo) {
        this.aprroveInfo = aprroveInfo;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

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

}
