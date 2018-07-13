package com.hengtian.flow.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.user.entity.emp.Emp;
import com.user.entity.emp.EmpVO;

/**
 * 工作流用户任务VO
 * @author houjinrong@chtwm.com
 * date 2018/5/10 14:28
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
	/**
	 * 流程实例ID
	 */
	private String processInstanceId;
	/**
	 * 任务结束时间
	 */
	private Date taskEndTime;
	/**
	 * 系统名称
	 */
	private String appName;
	/**
	 * 业务主键
	 */
	private String businessKey;

	private String formKey;

	public String getFormKey() {
		return formKey;
	}

	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}

	/**
	 * 节点对应的人员信息
	 */
	List<EmpVO> emps;

	public List<EmpVO> getEmps() {
		return emps;
	}

	public void setEmps(List<EmpVO> emps) {
		this.emps = emps;
	}

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
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getTaskEndTime() {
		return taskEndTime;
	}

	public void setTaskEndTime(Date taskEndTime) {
		this.taskEndTime = taskEndTime;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
}
