package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Date: 2019/11/8
 * Time: 18:19
 * User: yangkai
 * EMail: yangkai01@chtwm.com
 */
@Data
@TableName("t_task_notice")
public class TTaskNotice implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 流程id
     */
    @TableField(value = "proc_inst_id")
    private String procInstId;

    /**
     * 流程名称
     */
    @TableField(value = "proc_inst_name")
    private String procInstName;

    /**
     * 系统主键
     */
    @TableField(value = "app_key")
    private Integer appKey;

    /**
     * 业务主键
     */
    @TableField(value = "business_key")
    private String businessKey;

    /**
     * 任务id
     */
    @TableField(value = "task_id")
    private String taskId;

    /**
     * 任务名称
     */
    @TableField(value = "task_name")
    private String taskName;

    /**
     * 处理人编号
     */
    @TableField(value = "emp_no")
    private String empNo;

    /**
     * 处理人名称
     */
    @TableField(value = "emp_name")
    private String empName;

    /**
     * 任务类型 0：审批，1：转办，2：问询，3：回复
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 任务状态0：未处理， 1：已处理
     */
    @TableField(value = "state")
    private Integer state;

    /**
     * 处理结果 1：通过，2：拒绝
     */
    @TableField(value = "action")
    private Integer action;

    /**
     * 处理意见
     */
    @TableField(value = "message")
    private String message;

    /**
     * 分销逍客通知状态
     */
    @TableField(value = "fxiaoke_notice_state")
    private Integer fxiaokeNoticeState;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 创建人编号
     */
    @TableField(value = "create_id")
    private String createId;

    /**
     * 创建时间
     */
    @TableField(value = "create_name")
    private String createName;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 更新人编号
     */
    @TableField(value = "update_id")
    private String updateId;

    /**
     * 更新人名称
     */
    @TableField(value = "update_name")
    private String updateName;

    /**
     * 是否删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;

    /**
     * 用户类型
     */
    @TableField(value = "user_type")
    private Integer userType;

    /**
     * 企业微信通知状态
     */
    @TableField(value = "qweixin_notice_state")
    private Integer qweixinNoticeState;

}
