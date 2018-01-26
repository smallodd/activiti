package com.hengtian.system.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;


/**
 * <p>
 * 部门表
 * </p>
 *
 * @author junyang.liu
 * @since 2017-08-09
 */
@TableName("sys_department")
public class SysDepartment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id",type = IdType.UUID)
	private String id;
    /**
     * 部门编码
     */
	@TableField("department_code")
	private String departmentCode;
    /**
     * 部门名称
     */
	@TableField("department_name")
	private String departmentName;
    /**
     * 描述
     */
	@TableField("description")
	private String description;
	/**
     * 排序
     */
	@TableField("sequence")
	private Integer sequence;
	/**
     * 部门图标
     */
	@TableField("department_icon")
	private String departmentIcon;
    /**
     * 上级部门编码
     */
	@TableField("parent_id")
	private String parentId;
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

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getDepartmentIcon() {
		return departmentIcon;
	}

	public void setDepartmentIcon(String departmentIcon) {
		this.departmentIcon = departmentIcon;
	}
}
