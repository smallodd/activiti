package com.hengtian.common.param;

import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * 任务管理操作接口参数
 * @author houjinrong@chtwm.com
 * date 2018/4/18 9:42
 */
public class TaskActionParam {

    /**
     * 操作类型
     *
     */
    private String actionType;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 当前任务ID
     */
    private String taskId;

    /**
     * 当前用户ID
     */
    private String userId;

    /**
     * 目标任务ID
     */
    private String targetTaskId;

    /**
     * 目标用户ID
     */
    private String targetUserId;

    /**
     * 目标任务节点KEY
     */
    private String targetTaskDefKey;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetTaskId() {
        return targetTaskId;
    }

    public void setTargetTaskId(String targetTaskId) {
        this.targetTaskId = targetTaskId;
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

    @Override
    public String toString() {
        //两种方式都可以
        //return ToStringBuilder.reflectionToString(this);
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     * 校验参数
     * @return
     */
    public Result validate(){
        Result result = new Result();
        result.setCode(Constant.FAIL);
        String actionType = this.getActionType();
        if(TaskActionEnum.contains(actionType)){
            if(TaskActionEnum.JUMP.value.equals(actionType)){
                //跳转-参数校验
                if(StringUtils.isBlank(getTaskId()) || StringUtils.isBlank(getTargetTaskDefKey())){
                    result.setMsg("参数taskId，targetTaskDefKey都不能为空");
                }
            }else if(TaskActionEnum.TRANSFER.value.equals(actionType)){
                //转办-参数校验
                if(StringUtils.isBlank(getTaskId()) || StringUtils.isBlank(getTargetUserId())){
                    result.setMsg("参数taskId，targetUserId都不能为空");
                }
            }else if(TaskActionEnum.REMIND.value.equals(actionType)){
                //催办-参数校验
                if(StringUtils.isBlank(getProcessInstanceId()) || StringUtils.isBlank(getTargetTaskDefKey())){
                    result.setMsg("参数processInstanceId，targetTaskDefKey都不能为空");
                }
            }else if(TaskActionEnum.ENQUIRE.value.equals(actionType)){
                //问询-参数校验
                if(StringUtils.isBlank(getProcessInstanceId()) || StringUtils.isBlank(getTargetTaskDefKey())){
                    result.setMsg("参数processInstanceId，targetTaskDefKey都不能为空");
                }
            }else if(TaskActionEnum.CONFIRMENQUIRE.value.equals(actionType)){
                //确认问询-参数校验
                if(StringUtils.isBlank(getProcessInstanceId())){
                    result.setMsg("参数processInstanceId不能为空");
                }
            }else if(TaskActionEnum.REVOKE.value.equals(actionType)){
                //撤回-参数校验
                if(StringUtils.isBlank(getProcessInstanceId())){
                    result.setMsg("参数processInstanceId不能为空");
                }
            }else if(TaskActionEnum.CANCEL.value.equals(actionType)){
                //取消-参数校验
                if(StringUtils.isBlank(getProcessInstanceId())){
                    result.setMsg("参数processInstanceId不能为空");
                }
            }else if(TaskActionEnum.SUSPEND.value.equals(actionType)){
                //挂起-参数校验
                if(StringUtils.isBlank(getTaskId())){
                    result.setMsg("参数taskId不能为空");
                }
            }else if(TaskActionEnum.ACTIVATE.value.equals(actionType)){
                //激活-参数校验
                if(StringUtils.isBlank(getTaskId())){
                    result.setMsg("参数taskId不能为空");
                }
            }else {
                result.setCode(Constant.SUCCESS);
                result.setSuccess(true);
            }
        }else {
            result.setMsg("参数actionType不合法,actionType值只能为:"+TaskActionEnum.valuesToString());
        }
        return result;
    }
}
