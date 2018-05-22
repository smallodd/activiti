package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

/**
 * Created by ma on 2018/5/21.
 * 代理人信息表
 */
@TableName("t_approval_agent")
public class TApprovalAgent {
    /**
     * 主键
     */
    @TableId(type = IdType.UUID)
    private  String id;
    /**
     * 委托人
     */
    @TableField(value = "client")
    private String client;
    /**
     * 代理人
     */
    @TableField(value = "agent")
    private String agent;
    /**
     * 委托开始时间
     */
    @TableField(value = "begin_time")
    private Date beginTime;
    /**
     * 委托结束时间
     */
    @TableField(value = "end_time")
    private Date endTime;
    /**
     * 委托状态
     */
    @TableField(value = "status")
    private Integer status;

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


}
