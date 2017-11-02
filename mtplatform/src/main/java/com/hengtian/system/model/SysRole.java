package com.hengtian.system.model;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * <p>
 * 角色表
 * </p>
 *
 * @author junyang.liu
 * @since 2017-08-09
 */
@TableName("sys_role")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id",type = IdType.UUID)
	private String id;
    /**
     * 角色编码
     */
	@TableField("role_code")
	private String roleCode;
    /**
     * 创建时间
     */
	@TableField("create_time")
	private java.util.Date createTime;
    /**
     * 描述
     */
	private String description;
    /**
     * 角色名称
     */
	@TableField("role_name")
	private String roleName;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
