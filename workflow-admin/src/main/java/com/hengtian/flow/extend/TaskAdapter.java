package com.hengtian.flow.extend;

import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.result.Result;
import com.hengtian.flow.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskAdapter implements TaskManager {

    @Autowired
    private WorkflowService WorkflowService;

    @Override
    public Result taskAction(TaskActionParam taskActionParam) {
        String actionType = taskActionParam.getActionType();
        if(TaskActionEnum.JUMP.value.equals(actionType)){
            //跳转
            return WorkflowService.taskJump(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getTargetTaskDefKey());
        }else if(TaskActionEnum.TRANSFER.value.equals(actionType)){
            //转办
            return WorkflowService.taskTransfer(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getTargetUserId());
        }else if(TaskActionEnum.REMIND.value.equals(actionType)){
            //催办
            return WorkflowService.taskRemind(taskActionParam.getUserId(), taskActionParam.getProcessInstanceId(), taskActionParam.getTargetTaskDefKey());
        }else if(TaskActionEnum.ENQUIRE.value.equals(actionType)){
            //问询
            return WorkflowService.taskEnquire(taskActionParam.getUserId(), taskActionParam.getProcessInstanceId(), taskActionParam.getTargetTaskDefKey());
        }else if(TaskActionEnum.CONFIRMENQUIRE.value.equals(actionType)){
            //确认问询
            return WorkflowService.taskConfirmEnquire(taskActionParam.getUserId(), taskActionParam.getTaskId());
        }else if(TaskActionEnum.REVOKE.value.equals(actionType)){
            //撤回
            return WorkflowService.taskRevoke(taskActionParam.getUserId(), taskActionParam.getProcessInstanceId());
        }else if(TaskActionEnum.CANCEL.value.equals(actionType)){
            //取消
            return WorkflowService.taskCancel(taskActionParam.getUserId(), taskActionParam.getProcessInstanceId());
        }else if(TaskActionEnum.SUSPEND.value.equals(actionType)){
            //挂起
            return WorkflowService.taskSuspend(taskActionParam.getUserId(), taskActionParam.getTaskId());
        }else if(TaskActionEnum.ACTIVATE.value.equals(actionType)){
            //激活
            return WorkflowService.taskActivate(taskActionParam.getUserId(), taskActionParam.getTaskId());
        }

        return new Result();
    }
}
