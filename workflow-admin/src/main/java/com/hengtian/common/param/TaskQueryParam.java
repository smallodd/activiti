package com.hengtian.common.param;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 查询任务-入参
 * @author houjinrong@chtwm.com
 * date 2018/4/19 15:10
 */
public class TaskQueryParam {

    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id", example="H000000")
    private String creator;
    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人部门编号", example="01009")
    private String creatorDept;
    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务ID", example="110")
    private String taskId;
    /**
     * 流程实例id
     */
    @ApiModelProperty(value = "流程实例id", example="110")
    private String procInstId;
    /**
     * 流程实例名称
     */
    @ApiModelProperty(value = "流程实例名称", example="110")
    private String procInstName;
    /**
     * 审批人id
     */
    @ApiModelProperty(value = "审批人id", example="H000000")
    private String assignee;
    /**
     * 创建任务的标题
     */
    @ApiModelProperty(value = "创建任务的标题",  example="测试任务标题")
    private String title;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称", example="任务名称")
    private String taskName;
    /**
     * 系统定义的key
     */
    @ApiModelProperty(value = "系统定义的key", required = true, example="系统定义的key")
    private Integer appKey;
    /**
     * 业务系统主键
     */
    @ApiModelProperty(value = "业务系统主键", required = true, example="业务系统主键")
    private String businessKey;
    /**
     * 业务系统主键 1：运行结束；0：正在运行
     */
    @ApiModelProperty(value = "流程示例状态", required = true, example="0")
    private Integer processState;
    /**
     * 业务系统主键
     */
    @ApiModelProperty(value = "创建日期-开始", required = true, example="2018-05-01")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private String createTimeStart;
    /**
     * 业务系统主键
     */
    @ApiModelProperty(value = "创建日期-结束", required = true, example="2018-05-30")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private String createTimeEnd;
    /**
     * 分页-当前页
     */
    @ApiModelProperty(value = "当前页", required = true, example="1")
    private int page;
    /**
     * 分页-每页条数
     */
    @ApiModelProperty(value = "每页条数", required = true, example="10")
    private int rows;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorDept() {
        return creatorDept;
    }

    public void setCreatorDept(String creatorDept) {
        this.creatorDept = creatorDept;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getProcInstName() {
        return procInstName;
    }

    public void setProcInstName(String procInstName) {
        this.procInstName = procInstName;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getAppKey() {
        return appKey;
    }

    public void setAppKey(Integer appKey) {
        this.appKey = appKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public Integer getProcessState() {
        return processState;
    }

    public void setProcessState(Integer processState) {
        this.processState = processState;
    }
}
