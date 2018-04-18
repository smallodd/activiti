package com.hengtian.flow.action;

import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.result.Result;

public class TaskAdapter implements TaskManager {

    ConcreteTaskManager concreteTaskManager = new ConcreteTaskManager();

    @Override
    public Result action(String actionType) {
        if(TaskActionEnum.JUMP.value.equals(actionType)){
            //跳转
            return concreteTaskManager.actionJump(actionType);
        }else if(TaskActionEnum.TRANSFER.value.equals(actionType)){
            //转办
            return concreteTaskManager.actionTransfer(actionType);
        }else if(TaskActionEnum.REMIND.value.equals(actionType)){
            //催办
            return concreteTaskManager.actionRemind(actionType);
        }else if(TaskActionEnum.ENQUIRE.value.equals(actionType)){
            //问询
            return concreteTaskManager.actionEnquire(actionType);
        }else if(TaskActionEnum.CONFIRMENQUIRE.value.equals(actionType)){
            //确认问询
            return concreteTaskManager.actionConfirmEnquire(actionType);
        }else if(TaskActionEnum.REVOKE.value.equals(actionType)){
            //撤回
            return concreteTaskManager.actionRevoke(actionType);
        }else if(TaskActionEnum.CANCEL.value.equals(actionType)){
            //取消
            return concreteTaskManager.actionCancel(actionType);
        }else if(TaskActionEnum.SUSPEND.value.equals(actionType)){
            //挂起
            return concreteTaskManager.actionSuspend(actionType);
        }else if(TaskActionEnum.ACTIVATE.value.equals(actionType)){
            //激活
            return concreteTaskManager.actionActivate(actionType);
        }

        return new Result();
    }
}
