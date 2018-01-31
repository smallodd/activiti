package com.activiti.entity;

import java.io.Serializable;

/**
 * 工作流业务公共父类
 * @author liu.junyang
 *
 */
public class CommonVo extends ApproveVo implements Serializable{
	
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
	 * 模型定义key
	 */
	private  String modelKey;



	public String getApplyUserId() {
		return applyUserId;
	}

	/**
	 *
	 * @param applyUserId  申请人id
	 */
	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}

	public String getApplyTitle() {
		return applyTitle;
	}

	/**
	 *
	 * @param applyTitle 任务的名称
	 */
	public void setApplyTitle(String applyTitle) {
		this.applyTitle = applyTitle;
	}

	public String getApplyUserName() {
		return applyUserName;
	}

	/**
	 *
	 * @param applyUserName  申请人名称
	 */
	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}

	public String getBusinessType() {
		return businessType;
	}

	/**
	 * y
	 * @param businessType  业务系统key
	 */
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	/**
	 *
	 * @param businessKey  业务系统主键
	 */
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public String getModelKey() {
		return modelKey;
	}

	/**
	 *
	 * @param modelKey 模型key
	 */
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}






	@Override
	public String toString() {
		return "CommonVo{" +
				"applyUserId='" + applyUserId + '\'' +
				", applyTitle='" + applyTitle + '\'' +
				", applyUserName='" + applyUserName + '\'' +
				", businessType='" + businessType + '\'' +
				", businessKey='" + businessKey + '\'' +
				", prodefinedKey='" + modelKey + '\'' +
				'}';
	}
}
