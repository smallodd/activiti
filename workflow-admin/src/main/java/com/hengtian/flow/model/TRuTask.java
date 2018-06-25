package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ma on 2018/4/17.
 * 扩展任务表信息
 */
@TableName("t_ru_task")
@Data
public class TRuTask implements Serializable {
    @TableId(type = IdType.UUID)
    private String id;
    @TableField(value = "task_id")
    private String taskId;
    @TableField(value = "task_def_key")
    private String taskDefKey;
    @TableField(value = "task_def_name")
    private String taskDefName;
    @TableField(value = "assignee")
    private String assignee;
    @TableField(value = "assignee_name")
    private String assigneeName;
    @TableField(value = "assignee_type")
    private Integer assigneeType;
    @TableField(value = "task_type")
    private String taskType;
    @TableField(value = "owner")
    private String owner;
    @TableField(value = "expire_time")
    private Date expireTime;
    @TableField(value = "updater")
    private String updater;
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "app_key")
    private Integer appKey;
    @TableField(value = "assignee_real")
    private String assigneeReal;
    @TableField(value = "proc_inst_id")
    private String procInstId;
}
