package com.hengtian.common.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by ma on 2018/4/18.
 * 任务信息接收参数
 */
@ApiModel(value = "任务生成参数实体", description = "processParam")
public class TaskParam {
    //任务id
    @ApiModelProperty(value = "任务id", required = true, example="100001")
    private String taskId;
    //任务类型
    @ApiModelProperty(value = "任务类型", example="100001")
    private String taskType;
    //审批人类型
    @ApiModelProperty(value = "审批人类型", example="assignee/candidateUser/counterSign")
    private String assignType;
    //审批人
    @ApiModelProperty(value = "审批人", example="H00001")
    private String approver;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getAssignType() {
        return assignType;
    }

    public void setAssignType(String assignType) {
        this.assignType = assignType;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }
}
