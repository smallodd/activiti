package com.hengtian.activiti.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 工作流用户任务VO
 * @author liu.junyang
 *
 */
public class TaskVo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 任务节点ID
	 */
	private String id;
	/**
	 * 任务节点名称
	 */
	private String taskName;
	/**
	 * 任务节点状态(1.待签收   2.待受理[办理,转办,委派,跳转])
	 */
	private String taskState;
	/**
	 * 任务签收人
	 */
	private String taskAssign;
	/**
	 * 任务创建时间
	 */
	private Date taskCreateTime;
	/**
	 * 业务名称
	 */
	private String businessName;
	/**
	 * 任务节点KEY
	 */
	private String taskDefinitionKey;
	/**
	 * 流程定义ID
	 */
	private String processDefinitionId;
	/**
	 * 流程定义KEY(例如:请假流程为TVacation)
	 */
	private String processDefinitionKey;
	/**
	 * 流程发起人(业务申请人)
	 */
	private String processOwner;
	/**
	 * 挂起状态(1.未挂起 2.已挂起)
	 */
    private String suspended;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getTaskAssign() {
		return taskAssign;
	}
	public void setTaskAssign(String taskAssign) {
		this.taskAssign = taskAssign;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}
	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}
	public String getProcessOwner() {
		return processOwner;
	}
	public void setProcessOwner(String processOwner) {
		this.processOwner = processOwner;
	}
	public String getSuspended() {
		return suspended;
	}
	public void setSuspended(String suspended) {
		this.suspended = suspended;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getTaskCreateTime() {
		return taskCreateTime;
	}
	public void setTaskCreateTime(Date taskCreateTime) {
		this.taskCreateTime = taskCreateTime;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}
	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}
    
    

}
