package com.hengtian.flow.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 任务信息-返回值
 */
@Data
public class TaskResult {

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
     * 任务节点状态(1.待签收   2.待受理[办理,转办,委派,跳转])
     */
    private String taskState;
    /**
     * 当前处理人
     */
    private String assignee;

    private String assigneeName;
    /**
     * 前一步处理人
     */
    private String assigneeBefore;

    private String assigneeBeforeName;
    /**
     * 后一步处理人
     */
    private String assigneeNext;

    private String assigneeNextName;
    /**
     * 委托人工号
     */
    private String assigneeDelegate;
    /**
     * 委托人名称
     */
    private String assigneeNameDelegate;
    /**
     * 流程创建人编号
     */
    private String creatorCode;
    /**
     * 流程创建人名称
     */
    private String creatorName;
    /**
     * 流程创建人所属部门编号
     */
    private String creatorDeptCode;
    /**
     * 流程创建人所属部门名称
     */
    private String creatorDeptName;
    /**
     * 流程编号
     */
    private String processInstanceId;
    /**
     * 流程标题
     */
    private String processInstanceTitle;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 流程定义KEY
     */
    private String processDefinitionKey;
    /**
     * 流程名称
     */
    private String processDefinitionName;
    /**
     * 流程状态
     */
    private String processInstanceState;
    /**
     * 业务主键
     */
    private String businessKey;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    /**
     * 是否是意见征询的任务 0-待审批任务；1-意见征询任务
     */
    private int asked;
    /**
     * 意见征询主键
     */
    private String askId;
    /**
     * 被意见征询的节点
     */
    private String  askTaskKey;
}
