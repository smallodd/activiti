package com.hengtian.flow.vo;

import java.io.Serializable;
import javax.persistence.Transient;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 工作流业务公共父类
 * @author liu.junyang
 *
 */
public class CommonVo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * 申请人ID
	 */
	private String applyUserId;
	/**
	 * 申请的标题
	 */
	private String applyTitle;
	/**
	 * 申请人名称
	 */
	private String applyUserName;
	/**
	 * 业务类型
	 */
	private String businessType;
	/**
	 * 模型的key
	 */
	private  String modelKey;
	/**
	 * 对应业务的KEY
	 */
	private String businessKey;
	/**
	 * 流程任务
	 */
	@JsonBackReference 
    private Task task;
	/**
	 * 运行中的流程实例
	 */
	@JsonBackReference 
    private ProcessInstance processInstance;
	/**
	 * 历史的流程实例
	 */
	@JsonBackReference 
    private HistoricProcessInstance historicProcessInstance;
	/**
	 * 历史任务
	 */
	@JsonBackReference 
    private HistoricTaskInstance historicTaskInstance;
	/**
	 * 流程定义
	 */
	@JsonBackReference 
    private ProcessDefinition processDefinition;
	
	@Transient
	public String getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}
	@Transient
	public String getApplyTitle() {
		return applyTitle;
	}
	public void setApplyTitle(String applyTitle) {
		this.applyTitle = applyTitle;
	}
	@Transient
	public String getApplyUserName() {
		return applyUserName;
	}
	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}
	@Transient
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	@Transient
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	@Transient
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	@Transient
	public ProcessInstance getProcessInstance() {
		return processInstance;
	}
	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}
	@Transient
	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}
	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}
	@Transient
	public HistoricProcessInstance getHistoricProcessInstance() {
		return historicProcessInstance;
	}
	public void setHistoricProcessInstance(HistoricProcessInstance historicProcessInstance) {
		this.historicProcessInstance = historicProcessInstance;
	}
	@Transient
	public HistoricTaskInstance getHistoricTaskInstance() {
		return historicTaskInstance;
	}
	public void setHistoricTaskInstance(HistoricTaskInstance historicTaskInstance) {
		this.historicTaskInstance = historicTaskInstance;
	}
	@Transient
	public String getModelKey() {
		return modelKey;
	}

	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}
}
