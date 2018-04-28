package com.hengtian.common.param;

import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * 任务管理操作接口参数
 *
 * @author houjinrong@chtwm.com
 * date 2018/4/18 9:42
 */
public class TaskActionParam {

    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型", required = true, example = "actionType")
    private String actionType;

    /**
     * 流程实例ID
     */
    @ApiModelProperty(value = "流程实例ID", required = false, example = "processInstanceId")
    private String processInstanceId;

    /**
     * 当前任务ID
     */
    @ApiModelProperty(value = "当前任务ID", required = false, example = "taskId")
    private String taskId;

    /**
     * 任务对应的不同审批人的具体执行ID
     */
    @ApiModelProperty(value = "当前任务ID", required = false, example = "workId")
    private String workId;

    /**
     * 操作人ID
     */
    @ApiModelProperty(value = "操作人ID", required = true, example = "H000000")
    private String userId;

    /**
     * 目标用户ID
     */
    @ApiModelProperty(value = "目标用户ID", required = false, example = "H000001")
    private String targetUserId;

    /**
     * 目标任务节点KEY
     */
    @ApiModelProperty(value = "目标任务节点KEY", required = false, example = "targetTaskDefKey")
    private String targetTaskDefKey;
    /**
     * 意见
     */
    @ApiModelProperty(value = "意见", required = false, example = "commentResult")
    private String commentResult;

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetTaskDefKey() {
        return targetTaskDefKey;
    }

    public void setTargetTaskDefKey(String targetTaskDefKey) {
        this.targetTaskDefKey = targetTaskDefKey;
    }

    public String getCommentResult() {
        return commentResult;
    }

    public void setCommentResult(String commentResult) {
        this.commentResult = commentResult;
    }

    @Override
    public String toString() {
        //两种方式都可以
        //return ToStringBuilder.reflectionToString(this);
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     * 校验参数
     *
     * @return
     */
    public Result validate() {
        Result result = new Result();
        result.setCode(Constant.PARAM_ERROR);
        String actionType = this.getActionType();
        if (TaskActionEnum.contains(actionType)) {
            if (TaskActionEnum.CLAIM.value.equals(actionType)) {
                //认领-参数校验
                if (StringUtils.isBlank(getTaskId())) {
                    result.setMsg("参数taskId不能为空");
                }
            } else if (TaskActionEnum.UNCLAIM.value.equals(actionType)) {
                //取消认领-参数校验
                if (StringUtils.isBlank(getTaskId())) {
                    result.setMsg("参数taskId不能为空");
                }
            } else if (TaskActionEnum.JUMP.value.equals(actionType)) {
                //跳转-参数校验
                if (StringUtils.isBlank(getTaskId()) || StringUtils.isBlank(getTargetTaskDefKey())) {
                    result.setMsg("参数taskId，targetTaskDefKey都不能为空");
                }
            } else if (TaskActionEnum.TRANSFER.value.equals(actionType)) {
                //转办-参数校验
                if (StringUtils.isBlank(getTaskId()) || StringUtils.isBlank(getTargetUserId())) {
                    result.setMsg("参数taskId，targetUserId都不能为空");
                }
            } else if (TaskActionEnum.REMIND.value.equals(actionType)) {
                //催办-参数校验
                if (StringUtils.isBlank(getTaskId())) {
                    result.setMsg("参数processInstanceId，targetTaskDefKey都不能为空");
                }
            } else if (TaskActionEnum.ENQUIRE.value.equals(actionType)) {
                //问询-参数校验
                if (StringUtils.isBlank(getTaskId()) || StringUtils.isBlank(getTargetTaskDefKey())) {
                    result.setMsg("参数taskId，targetTaskDefKey都不能为空");
                }
            } else if (TaskActionEnum.CONFIRMENQUIRE.value.equals(actionType)) {
                //问询确认-参数校验
                if (StringUtils.isBlank(getTaskId())) {
                    result.setMsg("参数taskId不能为空");
                }
            } else if (TaskActionEnum.REVOKE.value.equals(actionType)) {
                //撤回-参数校验
                if (StringUtils.isBlank(getTaskId()) || StringUtils.isBlank(getTargetTaskDefKey())) {
                    result.setMsg("参数taskId，targetTaskDefKey不能为空");
                }
            } else if (TaskActionEnum.CANCEL.value.equals(actionType)) {
                //取消-参数校验
                if (StringUtils.isBlank(getProcessInstanceId())) {
                    result.setMsg("参数processInstanceId不能为空");
                }
            } else if (TaskActionEnum.SUSPEND.value.equals(actionType)) {
                //挂起流程-参数校验
                if (StringUtils.isBlank(getProcessInstanceId())) {
                    result.setMsg("参数processInstanceId不能为空");
                }
            } else if (TaskActionEnum.ACTIVATE.value.equals(actionType)) {
                //激活流程-参数校验
                if (StringUtils.isBlank(getProcessInstanceId())) {
                    result.setMsg("参数processInstanceId不能为空");
                }
            } else {
                result.setCode(Constant.SUCCESS);
                result.setSuccess(true);
            }
        } else {
            result.setMsg("参数actionType不合法,actionType值只能为:" + TaskActionEnum.valuesToString());
        }
        result.setSuccess(true);
        return result;
    }
}
