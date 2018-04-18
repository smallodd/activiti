package com.hengtian.common.param;

import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
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
     * 当前任务ID
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

            }else if(TaskActionEnum.JUMP.value.equals(actionType)){

            }else if(TaskActionEnum.JUMP.value.equals(actionType)){

            }else if(TaskActionEnum.JUMP.value.equals(actionType)){

            }else if(TaskActionEnum.JUMP.value.equals(actionType)){

            }else if(TaskActionEnum.JUMP.value.equals(actionType)){

            }else if(TaskActionEnum.JUMP.value.equals(actionType)){

            }else if(TaskActionEnum.JUMP.value.equals(actionType)){

            }
        }else{
            result.setMsg("参数actionType不合法,actionType值只能为"+TaskActionEnum.valuesToString());
        }
        return result;
    }
}
