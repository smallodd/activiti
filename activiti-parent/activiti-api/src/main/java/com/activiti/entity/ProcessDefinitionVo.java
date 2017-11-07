package com.activiti.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 工作流流程定义VO
 * @author liu.junyang
 * 
 */
public class ProcessDefinitionVo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * 流程定义ID
	 */
	private String id;
	/**
	 * 流程定义名称
	 */
	private String name;
	/**
	 * 流程定义的KEY
	 */
	private String key;
	/**
	 * 流程定义版本
	 */
    private Integer version;
    /**
	 * 部署ID
	 */
    private String deploymentId;
    /**
	 * bpmn资源名称
	 */
    private String resourceName;
    /**
	 * 流程图片名称
	 */
    private String imageName;
    /**
	 * 部署时间
	 */
    private Date deployTime;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getDeployTime() {
		return deployTime;
	}
	public void setDeployTime(Date deployTime) {
		this.deployTime = deployTime;
	}
	public String getSuspended() {
		return suspended;
	}
	public void setSuspended(String suspended) {
		this.suspended = suspended;
	}

}
