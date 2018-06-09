package com.hengtian.flow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hengtian.flow.vo.TaskNodeVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class ProcessInstanceResult {

    /**
     * 流程编号
     */
    private String processInstanceId;
    /**
     * 标题
     */
    private String processInstanceName;
    /**
     * 流程名称
     */
    private String processDefinitionName;
    /**
     * 流程状态
     */
    private String processInstanceState;
    /**
     * 当前节点信息
     */
    private List<TaskNodeVo> currentTaskNode;
    /**
     * 系统标识
     */
    @JsonIgnore
    private Integer appKey;
    /**
     * 发起时间
     */
    @DateTimeFormat(pattern = "YYYY-mm-dd")
    private Date startTime;
    /**
     * 完成时间
     */
    @DateTimeFormat(pattern = "YYYY-mm-dd")
    private Date endTime;

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceName() {
        return processInstanceName;
    }

    public void setProcessInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
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

    public List<TaskNodeVo> getCurrentTaskNode() {
        return currentTaskNode;
    }

    public void setCurrentTaskNode(List<TaskNodeVo> currentTaskNode) {
        this.currentTaskNode = currentTaskNode;
    }

    public Integer getAppKey() {
        return appKey;
    }

    public void setAppKey(Integer appKey) {
        this.appKey = appKey;
    }
}
