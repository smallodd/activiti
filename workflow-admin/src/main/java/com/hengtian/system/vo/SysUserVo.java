package com.hengtian.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hengtian.system.model.SysRole;

import java.io.Serializable;
import java.util.List;

public class SysUserVo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
     * 主键
     */
	private String id;
    /**
     * 登录名
     */
	private String loginName;
    /**
     * 登录密码
     */
	private String loginPwd;
    /**
     * 昵称
     */
	private String userName;
    /**
     * 手机
     */
	private String userPhone;
    /**
     * 邮箱
     */
	private String userEmail;
    /**
     * 性别
     */
	private String userSex;
	/**
	 * 用户类型 (0:管理员  1:普通用户)
	 */
	private String userType;
    /**
     * 注册时间
     */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private java.util.Date registerTime;
    /**
     * 部门外键
     */
	private String departmentId;
	/**
	 * 部门名称
	 */
	private String departmentName;
	/**
	 * roleIds
	 */
	private String roleIds;
	/**
	 * 角色List
	 */
	private List<SysRole> roleList;
	
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
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getRoleIds() {
		return roleIds;
	}
	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}
	public List<SysRole> getRoleList() {
		return roleList;
	}
	public void setRoleList(List<SysRole> roleList) {
		this.roleList = roleList;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}

}
