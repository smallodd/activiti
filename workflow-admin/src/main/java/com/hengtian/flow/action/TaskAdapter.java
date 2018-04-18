package com.hengtian.flow.action;

import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.result.Result;
import com.hengtian.flow.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskAdapter implements TaskManager {

    @Autowired
    private WorkflowService WorkflowService;

    @Override
    public Result taskAction(String actionType) {
        if(TaskActionEnum.JUMP.value.equals(actionType)){
            //跳转
            return WorkflowService.taskJump(actionType);
        }else if(TaskActionEnum.TRANSFER.value.equals(actionType)){
            //转办
            return WorkflowService.taskTransfer(actionType);
        }else if(TaskActionEnum.REMIND.value.equals(actionType)){
            //催办
            return WorkflowService.taskRemind(actionType);
        }else if(TaskActionEnum.ENQUIRE.value.equals(actionType)){
            //问询
            return WorkflowService.taskEnquire(actionType);
        }else if(TaskActionEnum.CONFIRMENQUIRE.value.equals(actionType)){
            //确认问询
            return WorkflowService.taskConfirmEnquire(actionType);
        }else if(TaskActionEnum.REVOKE.value.equals(actionType)){
            //撤回
            return WorkflowService.taskRevoke(actionType);
        }else if(TaskActionEnum.CANCEL.value.equals(actionType)){
            //取消
            return WorkflowService.taskCancel(actionType);
        }else if(TaskActionEnum.SUSPEND.value.equals(actionType)){
            //挂起
            return WorkflowService.taskSuspend(actionType);
        }else if(TaskActionEnum.ACTIVATE.value.equals(actionType)){
            //激活
            return WorkflowService.taskActivate(actionType);
        }

        return new Result();
    }
}
