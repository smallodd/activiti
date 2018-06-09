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
     * 节点key
     */
    private String taskDefinitionKey;

    /**
     * 节点名称
     */
    private String taskDefinitionName;
    /**
     * 审批人
     */
    private List<AssigneeVo> assignee;
}
