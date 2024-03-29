package com.hengtian.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * 查询任务-入参
 * @author houjinrong@chtwm.com
 * date 2018/4/19 15:10
 */
@Data
public class TaskQueryParam {

    /**
     * 系统定义的key
     */
    @ApiModelProperty(value = "系统定义的key", required = true, example = "1")
    @NotBlank(message = "系统定义的key不能为空")
    private Integer appKey;
    /**
     * 审批人id
     */
    @ApiModelProperty(value = "审批人id", example = "H000000")
    @NotBlank(message = "审批人id不能为空")
    private String assignee;
    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id", example = "H000000")
    private String creator;
    /**
     * 创建人部门编号
     */
    @ApiModelProperty(value = "创建人部门编号", example = "01009")
    private String creatorDept;
    /**
     * 创建人部门名称
     */
    @ApiModelProperty(value = "创建人部门名称", example = "研发一部")
    private String creatorDeptName;
    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务ID", example = "110")
    private String taskId;
    /**
     * 任务状态
     * 已办任务状态：1-同意；2-拒绝
     * 待办任务状态：1：待审批；2-意见征询中
     */
    @ApiModelProperty(value = "任务状态", example = "110")
    private String taskState;
    /**
     * 流程实例id
     */
    @ApiModelProperty(value = "流程实例id", example = "110")
    private String procInstId;
    /**
     * 流程名称
     */
    @ApiModelProperty(value = "流程定义名称", example = "110")
    private String procDefName;
    /**
     * 创建任务的标题
     */
    @ApiModelProperty(value = "创建任务的标题", example = "测试任务标题")
    private String title;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称", example = "任务名称")
    private String taskName;
    /**
     * 业务系统主键
     */
    @ApiModelProperty(value = "业务系统主键", example = "业务系统主键")
    private String businessKey;
    /**
     * 流程实例状态 1：运行结束；0：正在运行
     */
    @ApiModelProperty(value = "流程实例状态", example = "0")
    private Integer procInstState;
    /**
     * 创建日期-开始
     */
    @ApiModelProperty(value = "创建日期-开始", example = "2018-05-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String createTimeStart;
    /**
     * 创建日期-结束
     */
    @ApiModelProperty(value = "创建日期-结束", example = "2018-05-30")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String createTimeEnd;
    /**
     * 流程定义key
     */
    @ApiModelProperty(value = "流程定义key", example = "0")
    private String procDefKey;
    /**
     * 分页-当前页
     */
    @ApiModelProperty(value = "当前页", required = true, example = "1")
    private int page = 1;
    /**
     * 分页-每页条数
     */
    @ApiModelProperty(value = "每页条数", required = true, example = "10")
    private int rows = 10;
    /**
     * 代理信息
     */
    @ApiModelProperty(value = "代理信息", hidden = true)
    private List<TaskAgentQueryParam> taskAgentList;
    /**
     * 代理信息
     */
    @ApiModelProperty(value = "代理信息", hidden = true)
    private String assigneeAgent;

    /**
     * 代理人加密信息
     */
    private String assigneeAgentSecret;
}