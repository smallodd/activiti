package com.hengtian.system.model;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
/**
 * <p>
 * 系统操作日志
 * </p>
 *
 * @author junyang.liu
 * @since 2017-08-27
 */
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.UUID)
    private String id;
    /**
     * 操作用户ID
     */
    @TableField(value="oper_user_id")
    private String operUserId;
    /**
     * 操作用户名
     */
    @TableField(value="oper_user_name")
    private String operUserName;
    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="oper_time")
    private java.util.Date operTime;
    /**
     * 客户端IP地址
     */
    @TableField(value="oper_client_ip")
    private String operClientIp;
    /**
     * 请求地址
     */
    @TableField(value="request_url")
    private String requestUrl;
    /**
     * 请求方法
     */
    @TableField(value="request_method")
    private String requestMethod;
    /**
     * 操作事件（删除，新增，修改，查询，登录，退出）
     */
    @TableField(value="oper_event")
    private String operEvent;
    /**
     * 操作状态（1：成功，2：失败）
     */
    @TableField(value="oper_status")
    private Integer operStatus;
    /**
     * 描述信息
     */
    @TableField(value="log_description")
    private String logDescription;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperUserId() {
        return operUserId;
    }

    public void setOperUserId(String operUserId) {
        this.operUserId = operUserId;
    }

    public String getOperUserName() {
        return operUserName;
    }

    public void setOperUserName(String operUserName) {
        this.operUserName = operUserName;
    }

    public java.util.Date getOperTime() {
        return operTime;
    }

    public void setOperTime(java.util.Date operTime) {
        this.operTime = operTime;
    }

    public String getOperClientIp() {
        return operClientIp;
    }

    public void setOperClientIp(String operClientIp) {
        this.operClientIp = operClientIp;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getOperEvent() {
        return operEvent;
    }

    public void setOperEvent(String operEvent) {
        this.operEvent = operEvent;
    }

    public Integer getOperStatus() {
        return operStatus;
    }

    public void setOperStatus(Integer operStatus) {
        this.operStatus = operStatus;
    }

    public String getLogDescription() {
        return logDescription;
    }

    public void setLogDescription(String logDescription) {
        this.logDescription = logDescription;
    }

}
