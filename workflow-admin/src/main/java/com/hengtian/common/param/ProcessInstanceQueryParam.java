package com.hengtian.common.param;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 流程实例查询条件
 *
 * @author houjinrong@chtwm.com
 * date 2018/5/18 9:18
 */
public class ProcessInstanceQueryParam {

    /**
     * 系统key
     */
    @ApiModelProperty(value = "系统key", required = true, example="1")
    @NotBlank(message = "系统key不能为空！")
    private String appKey;

    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id", required = true, example="H000000")
    @NotBlank(message = "创建人id不能为空！")
    private String creator;

    /**
     * 流程编号
     */
    @ApiModelProperty(value = "流程编号", example="1")
    private Integer processInstanceId;

    /**
     * 流程状态
     * 0-进行中；1-完成；2-未完成
     */
    @ApiModelProperty(value = "流程状态", example="0-进行中；1-完成；2-未完成")
    private String processInstanceState;

    /**
     * 流程标题
     */
    @ApiModelProperty(value = "流程标题", example="流程标题")
    private String processInstanceName;

    /**
     * 流程名称
     */
    @ApiModelProperty(value = "流程名称", example="流程名称")
    private String processDefinitionName;

    /**
     * 发起日期（从）
     */
    @ApiModelProperty(value = "发起日期（从）", example="2018-05-15")
    private String startTimeFrom;

    /**
     * 发起日期（到）
     */
    @ApiModelProperty(value = "发起日期（到）", example="2018-10-30")
    private String startTimeSTo;

    /**
     * 完成日期（从）
     */
    @ApiModelProperty(value = "完成日期（从）", example="2018-05-15")
    private String endTimeFrom;

    /**
     * 完成日期（到）
     */
    @ApiModelProperty(value = "完成日期（到）", example="2018-10-30")
    private String endTimeTo;
    /**
     * 流程定义key
     */
    @ApiModelProperty(value = "流程定义key", example="0")
    private String procDefKey;
    /**
     * 分页-当前页
     */
    @ApiModelProperty(value = "当前页", required = true, example="1")
    @NotNull(message = "当前页不能为空！")
    private Integer page;
    /**
     * 分页-每页条数
     */
    @ApiModelProperty(value = "每页条数", required = true, example="10")
    @NotNull(message = "每页条数不能为空！")
    private Integer rows;

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Integer processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceState() {
        return processInstanceState;
    }

    public void setProcessInstanceState(String processInstanceState) {
        this.processInstanceState = processInstanceState;
    }

    public String getProcessInstanceName() {
        return processInstanceName;
    }

    public void setProcessInstanceName(String processInstanceName) {
        this.processInstanceName = processInstanceName;
    }

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
    }

    public String getStartTimeFrom() {
        return startTimeFrom;
    }

    public void setStartTimeFrom(String startTimeFrom) {
        this.startTimeFrom = startTimeFrom;
    }

    public String getStartTimeSTo() {
        return startTimeSTo;
    }

    public void setStartTimeSTo(String startTimeSTo) {
        this.startTimeSTo = startTimeSTo;
    }

    public String getEndTimeFrom() {
        return endTimeFrom;
    }

    public void setEndTimeFrom(String endTimeFrom) {
        this.endTimeFrom = endTimeFrom;
    }

    public String getEndTimeTo() {
        return endTimeTo;
    }

    public void setEndTimeTo(String endTimeTo) {
        this.endTimeTo = endTimeTo;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        //两种方式都可以
        //return ToStringBuilder.reflectionToString(this);
        return ReflectionToStringBuilder.toString(this);
    }
}
