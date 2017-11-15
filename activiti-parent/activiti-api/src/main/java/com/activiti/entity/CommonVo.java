package com.activiti.entity;

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
	 * 模型定义key
	 */
	private  String modelKey;

	

	public String getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}

	public String getApplyTitle() {
		return applyTitle;
	}
	public void setApplyTitle(String applyTitle) {
		this.applyTitle = applyTitle;
	}

	public String getApplyUserName() {
		return applyUserName;
	}
	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}

	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public String getModelKey() {
		return modelKey;
	}

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
