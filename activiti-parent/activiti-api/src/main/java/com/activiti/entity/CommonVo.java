package com.activiti.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import javax.persistence.Transient;
import java.io.Serializable;

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
	 * 对应业务的KEY
	 */
	private String businessKey;

	/**
	 * 流程定义key
	 */
	private  String proDefinedKey;

	
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
	public String getProDefinedKey() {
		return proDefinedKey;
	}

	public void setProDefinedKey(String proDefinedKey) {
		this.proDefinedKey = proDefinedKey;
	}




	@Override
	public String toString() {
		return "CommonVo{" +
				"applyUserId='" + applyUserId + '\'' +
				", applyTitle='" + applyTitle + '\'' +
				", applyUserName='" + applyUserName + '\'' +
				", businessType='" + businessType + '\'' +
				", businessKey='" + businessKey + '\'' +
				", prodefinedKey='" + proDefinedKey + '\'' +
				'}';
	}
}
