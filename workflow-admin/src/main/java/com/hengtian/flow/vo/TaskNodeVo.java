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
     * 流程定义名称
     */
    private String processDefinitionName;

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

    /**
     * 流程实例创建人
     */
    private String processCreator;

    /**
     * 是否是第一个节点：0-否’1-是
     */
    private Integer isFirst;

    /**
     * 是否是最后一个节点：0-否’1-是
     */
    private Integer isLast;
}
