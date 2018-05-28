package com.hengtian.common.param;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by ma on 2018/4/18.
 * 任务信息接收参数
 */

public class TaskParam {
    //任务id
    @ApiModelProperty(value = "任务id", required = true, example="100001")
    private String taskId;
    //任务类型
    @ApiModelProperty(value = "任务类型", example="assignee/candidateUser/counterSign")
    private String taskType;
    //审批人类型
    @ApiModelProperty(value = "审批人类型", example="1,2,3,4")
    private Integer assignType;
    //审批人
    @ApiModelProperty(value = "审批人", example="H00001")
    private String assignee;

    @ApiModelProperty(value = "审批时传参数", example="审批意见")
    private String comment;
    //通过状态  1 通过 2 是拒绝 3通过参数流转

    @ApiModelProperty(value = "审批时传参", example="1是通过，2是拒绝，3是通过条件参数流转")
    private Integer pass;
    //参数的json格式

    @ApiModelProperty(value = "审批时传的条件参数", example="{a:b}")
    private String jsonVariables;
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

    public Integer getAssignType() {
        return assignType;
    }

    public void setAssignType(Integer assignType) {
        this.assignType = assignType;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getPass() {
        return pass;
    }

    public void setPass(Integer pass) {
        this.pass = pass;
    }

    public String getJsonVariables() {
        return jsonVariables;
    }

    public void setJsonVariables(String jsonVariables) {
        this.jsonVariables = jsonVariables;
    }
}
