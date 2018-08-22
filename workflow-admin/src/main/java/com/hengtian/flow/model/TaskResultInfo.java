package com.hengtian.flow.model;

import lombok.Data;

import java.util.Set;

/**
 * 任务信息-返回值
 */
@Data
public class TaskResultInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 任务节点ID
     */
    private String taskId;

    /**
     * 任务节点名称
     */
    private String taskName;

    /**
     * 审批人工号
     */
    private Set<String> assignee;
}
