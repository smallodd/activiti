package com.activiti.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;


/**
 * <p>
 * 用户表
 * </p>
 *
 * @author junyang.liu
 * @since 2017-08-09
 */
@TableName("sys_user")
public class SysUser implements Serializable{


	@TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id",type = IdType.UUID)
	private String id;
    /**
     * 登录名
     */
	@TableField("login_name")
	private String loginName;
    /**
     * 登录密码
     */
	@TableField("login_pwd")
	private String loginPwd;
    /**
     * 昵称
     */
	@TableField("user_name")
	private String userName;
    /**
     * 手机
     */
	@TableField("user_phone")
	private String userPhone;
    /**
     * 邮箱
     */
	@TableField("user_email")
	private String userEmail;
    /**
     * 性别
     */
	@TableField("user_sex")
	private String userSex;
	/**
	 * 用户类型 (0:管理员  1:普通用户)
	 */
	@TableField("user_type")
	private String userType;
    /**
     * 注册时间
     */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@TableField("register_time")
	private java.util.Date registerTime;
    /**
     * 部门外键
     */
	@TableField("department_id")
	private String departmentId;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserSex() {
		return userSex;
	}

	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}

	public java.util.Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(java.util.Date registerTime) {
		this.registerTime = registerTime;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

}
