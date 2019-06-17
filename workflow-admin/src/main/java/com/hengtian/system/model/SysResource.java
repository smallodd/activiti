package com.hengtian.system.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;


/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author junyang.liu
 * @since 2017-08-09
 */
@TableName("sys_resource")
public class SysResource implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id",type = IdType.UUID)
	private String id;
    /**
     * 资源路径
     */
	@TableField("resource_url")
	private String resourceUrl;
    /**
     * 资源编码
     */
	@TableField("resource_code")
	private String resourceCode;
    /**
     * 资源名称
     */
	@TableField("resource_name")
	private String resourceName;
    /**
     * 资源类型
     */
	@TableField("resource_type")
	private String resourceType;
    /**
     * 资源图标
     */
	@TableField("resource_icon")
	private String resourceIcon;
	/**
     * 打开方式
     */
	@TableField("open_mode")
	private String openMode;
    
    /**
     * 上级资源编码
     */
	@TableField("parent_id")
	private String parentId;
    /**
     * 排序
     */
	private Integer sequence;
    /**
     * 创建时间
     */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@TableField("create_time")
	private java.util.Date createTime;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	public String getResourceIcon() {
		return resourceIcon;
	}

	public void setResourceIcon(String resourceIcon) {
		this.resourceIcon = resourceIcon;
	}

	public String getOpenMode() {
		return openMode;
	}

	public void setOpenMode(String openMode) {
		this.openMode = openMode;
	}

}
