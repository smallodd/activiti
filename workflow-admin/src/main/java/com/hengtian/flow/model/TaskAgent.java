package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

/**
 * 代理审批信息
 * @author houjinrong@chtwm.com
 * date 2018/8/3 14:14
 */
@Data
@TableName("t_task_agent")
public class TaskAgent {

    /**
     * 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    @TableField(value = "task_id")
    private String taskId;

    /**
     * 审批人
     */
    @TableField(value = "assignee")
    private String assignee;

    /**
     * 代理审批人
     */
    @TableField(value = "assignee_agent")
    private String assigneeAgent;

    /**
     * 代理类型：1-办理；2-问询
     */
    @TableField(value = "agent_type")
    private Integer agentType;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;
}
