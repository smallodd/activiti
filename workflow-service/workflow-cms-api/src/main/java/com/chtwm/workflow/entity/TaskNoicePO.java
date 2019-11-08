package com.chtwm.workflow.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanyuexing
 * @date 2019/11/7 17:26
 * 任务通知的实体类
 */
public class TaskNoicePO implements Serializable {

    private static final long serialVersionUID = 8545100665157685064L;

    //主键
    private String id;

    //流程id
    private String procInstId;

    //流程名称
    private String procInstName;

    //系统主键
    private String appKey;

    //业务主键
    private String businessKey;

    //任务id
    private String taskId;

    //任务名称
    private String taskName;

    //处理人编号
    private String empNo;

    //处理人姓名
    private String empName;

    //任务类型 0审批 1转办 2问询 3回复
    private Integer type;

    //任务状态 0未处理 1已处理
    private Integer state;

    //处理结果 1通过  2拒绝
    private Integer action;

    //处理意见
    private String message;

    //纷享逍客通知状态 0未通知 1已通知 2通知失败
    private Integer xkNoticeState;

    //创建时间
    private Date createTime;

    //创建人编号
    private String createId;

    //创建人姓名
    private String createName;

    //更新时间
    private String updateTime;

    //更新人编号
    private String updateId;

    //更新人姓名
    private String updateName;

    //是否删除 0正常 1已删除
    private Integer isDelete;


    @Override
    public String toString() {
        return "TaskNoicePO{" +
                "id='" + id + '\'' +
                ", procInstId='" + procInstId + '\'' +
                ", procInstName='" + procInstName + '\'' +
                ", appKey='" + appKey + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", empNo='" + empNo + '\'' +
                ", empName='" + empName + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", action=" + action +
                ", message='" + message + '\'' +
                ", xkNoticeState=" + xkNoticeState +
                ", createTime=" + createTime +
                ", createId='" + createId + '\'' +
                ", createName='" + createName + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", updateId='" + updateId + '\'' +
                ", updateName='" + updateName + '\'' +
                ", isDelete=" + isDelete +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
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

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getXkNoticeState() {
        return xkNoticeState;
    }

    public void setXkNoticeState(Integer xkNoticeState) {
        this.xkNoticeState = xkNoticeState;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}
