package com.hengtian.flow.vo;

import lombok.Data;

import java.util.List;

/**
 * 任务节点对象
 * @author houjinrong@chtwm.com
 * date 2018/6/9 10:38
 */
@Data
public class TaskNodeVo {


    /**
     * 流程定义key
     */
    private String taskId;
    /**
     * 流程定义key
     */
    private String processDefinitionKey;

    /**
     * 任务节点key
     */
    private String taskDefinitionKey;

    /**
     * 节任务点名称
     */
    private String taskDefinitionName;
    /**
     * 审批人
     */
    private List<AssigneeVo> assignee;

    /**
     * 审批人 多个逗号隔开
     */
    private String assigneeStr;
}
