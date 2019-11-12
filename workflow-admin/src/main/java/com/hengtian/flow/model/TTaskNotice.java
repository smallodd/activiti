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

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "proc_inst_id")
    private String procInstId;

    @TableField(value = "proc_inst_name")
    private String procInstName;

    @TableField(value = "app_key")
    private Integer appKey;

    @TableField(value = "business_key")
    private String businessKey;

    @TableField(value = "task_id")
    private String taskId;

    @TableField(value = "task_name")
    private String taskName;

    @TableField(value = "emp_no")
    private String empNo;

    @TableField(value = "emp_name")
    private String empName;

    @TableField(value = "type")
    private Integer type;

    @TableField(value = "state")
    private Integer state;

    @TableField(value = "action")
    private Integer action;

    @TableField(value = "message")
    private String message;

    @TableField(value = "fxiaoke_notice_state")
    private Integer fxiaokeNoticeState;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "create_id")
    private String createId;

    @TableField(value = "create_name")
    private String createName;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "update_id")
    private String updateId;

    @TableField(value = "update_name")
    private String updateName;

    @TableField(value = "is_delete")
    private Integer isDelete;
    @TableField(value = "user_type")
    private Integer userType;

}
