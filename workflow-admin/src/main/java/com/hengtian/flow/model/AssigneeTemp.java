package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

/**
 * 审批人临时表，设置下一步审批人时暂存
 *
 * @author houjinrong@chtwm.com
 * date 2018/6/11 16:08
 */
@Data
@TableName("t_assignee_temp")
public class AssigneeTemp {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 流程实例ID
     */
    @TableField(value = "proc_inst_id")
    private String procInstId;
    /**
     * 上步节点key
     */
    @TableField(value = "task_def_key_before")
    private String taskDefKeyBefore;
    /**
     * 任务节点key
     */
    @TableField(value = "task_def_key")
    private String taskDefKey;
    /**
     * 审批人角色ID
     */
    @TableField(value = "role_code")
    private String roleCode;
    /**
     * 审批人角色名称
     */
    @TableField(value = "role_name")
    private String roleName;
    /**
     * 审批人ID
     */
    @TableField(value = "assignee_code")
    private String assigneeCode;
    /**
     * 审批人名称
     */
    @TableField(value = "assignee_name")
    private String assigneeName;
    /**
     * 删除标识：0：正常（默认）；1：删除
     */
    @TableField(value = "delete_flag")
    private Integer deleteFlag;
    /**
     * 创建人
     */
    @TableField(value = "creator")
    private String creator;
    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;
}