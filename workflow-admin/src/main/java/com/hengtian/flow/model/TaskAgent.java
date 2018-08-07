package com.hengtian.flow.model;

import lombok.Data;

import java.util.Date;

/**
 * 代理审批信息
 * @author houjinrong@chtwm.com
 * date 2018/8/3 14:14
 */
@Data
public class TaskAgent {

    /**
     * 主键，自增
     */
    private Long id;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 审批人
     */
    private String assignee;

    /**
     * 代理审批人
     */
    private String assigneeAgent;

    /**
     * 代理类型：1-办理；2-问询
     */
    private Integer agentType;

    /**
     * 创建时间
     */
    private Date createTime;
}
