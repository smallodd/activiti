package com.hengtian.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by ma on 2018/4/18.
 * 任务信息接收参数
 */
@Data
public class TaskParam {
    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id", required = true, example="100001")
    @NotBlank(message = "任务ID不能为空")
    private String taskId;
    /**
     * 审批人
     */
    @ApiModelProperty(value = "审批人", example="H00001")
    @NotBlank(message = "审批人不能为空")
    private String assignee;
    /**
     * 审批意见
     */
    @ApiModelProperty(value = "审批时传参数", example="审批意见")
    @NotBlank(message = "请填写审批意见")
    private String comment;
    /**
     * 通过状态  1 通过 2 是拒绝 3通过参数流转
     */
    @ApiModelProperty(value = "审批时传参", example="1是通过，2是拒绝，3是通过条件参数流转")
    @NotNull(message = "请选择审批结果")
    private Integer pass;
    /**
     * 参数的json格式
     */
    @ApiModelProperty(value = "审批时传的条件参数", example="{a:b}")
    private String jsonVariables;
    /**
     * 下一节点审批人信息
     */
    @ApiModelProperty(value = "下一节点审批人信息", example="H08899")
    private String assigneeNext;
    /**
     * 审批代理人
     */
    @ApiModelProperty(value = "审批代理人", example="H08899")
    private String assigneeAgent;
    /**
     * jumpType: 1-影响分支；2-不影响分支
     */
    @ApiModelProperty(value = "下一节点审批人信息", example="下一节点审批人信息")
    private Integer jumpType;
}
