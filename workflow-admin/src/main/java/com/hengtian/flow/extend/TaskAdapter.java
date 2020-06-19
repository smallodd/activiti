package com.hengtian.flow.extend;

import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.SpringBeanUtil;
import com.hengtian.flow.service.WorkflowService;
import com.hengtian.flow.service.impl.WorkflowServiceImpl;

public class TaskAdapter implements TaskManager {



    @Override
    public Result taskAction(TaskActionParam taskActionParam,WorkflowService workflowService) {
        String actionType = taskActionParam.getActionType();
        if (TaskActionEnum.CLAIM.value.equals(actionType)) {
            //认领
            return workflowService.taskClaim(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getWorkId());
        } else if (TaskActionEnum.UNCLAIM.value.equals(actionType)) {
            //取消认领
            return workflowService.taskUnclaim(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getWorkId());
        } else if (TaskActionEnum.JUMP.value.equals(actionType)) {
            //跳转
            return workflowService.taskJump(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getTargetTaskDefKey());
        } else if (TaskActionEnum.TRANSFER.value.equals(actionType)) {
            //转办
            return workflowService.taskTransfer(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getTargetUserId());
        } else if (TaskActionEnum.REMIND.value.equals(actionType)) {
            //催办
            return workflowService.taskRemind(taskActionParam.getUserId(), taskActionParam.getTaskId());
        } else if (TaskActionEnum.ENQUIRE.value.equals(actionType)) {
            //意见征询
            return workflowService.taskEnquire(taskActionParam.getUserId(), taskActionParam.getProcessInstanceId(), taskActionParam.getCurrentTaskDefKey(), taskActionParam.getTargetTaskDefKey(), taskActionParam.getCommentResult(),taskActionParam.getTargetUserId(), null);
        } else if (TaskActionEnum.CONFIRMENQUIRE.value.equals(actionType)) {
            //确认意见征询
            return workflowService.taskConfirmEnquire(taskActionParam.getUserId(), taskActionParam.getAskId(), taskActionParam.getCommentResult());
        } else if (TaskActionEnum.REVOKE.value.equals(actionType)) {
            //撤回
            return workflowService.taskRevoke(taskActionParam.getUserId(), taskActionParam.getTaskId());
        } else if (TaskActionEnum.ROLLBACK.value.equals(actionType)) {
            //驳回/退回
            return workflowService.taskRollback(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getTargetTaskDefKey());
        } else if (TaskActionEnum.CANCEL.value.equals(actionType)) {
            //取消
            return workflowService.taskCancel(taskActionParam.getUserId(), taskActionParam.getProcessInstanceId());
        } else if (TaskActionEnum.SUSPEND.value.equals(actionType)) {
            //挂起流程
            return workflowService.processSuspend(taskActionParam, true);
        } else if (TaskActionEnum.ACTIVATE.value.equals(actionType)) {
            //激活流程
            return workflowService.processActivate(taskActionParam, true);
        }

        return new Result();
    }
}
