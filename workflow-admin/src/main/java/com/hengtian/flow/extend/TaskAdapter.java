package com.hengtian.flow.extend;

import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.SpringBeanUtil;
import com.hengtian.flow.service.WorkflowService;
import com.hengtian.flow.service.impl.WorkflowServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;

public class TaskAdapter implements TaskManager {

    private WorkflowService WorkflowService = SpringBeanUtil.getBean(WorkflowServiceImpl.class);

    @Override
    public Result taskAction(TaskActionParam taskActionParam) {
        String actionType = taskActionParam.getActionType();
        if (TaskActionEnum.CLAIM.value.equals(actionType)) {
            //认领
            return WorkflowService.taskClaim(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getWorkId());
        } else if (TaskActionEnum.UNCLAIM.value.equals(actionType)) {
            //取消认领
            return WorkflowService.taskUnclaim(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getWorkId());
        } else if (TaskActionEnum.JUMP.value.equals(actionType)) {
            //跳转
            return WorkflowService.taskJump(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getTargetTaskDefKey());
        } else if (TaskActionEnum.TRANSFER.value.equals(actionType)) {
            //转办
            return WorkflowService.taskTransfer(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getTargetUserId());
        } else if (TaskActionEnum.REMIND.value.equals(actionType)) {
            //催办
            return WorkflowService.taskRemind(taskActionParam.getUserId(), taskActionParam.getTaskId());
        } else if (TaskActionEnum.ENQUIRE.value.equals(actionType)) {
            //问询
            return WorkflowService.taskEnquire(taskActionParam.getUserId(), taskActionParam.getProcessInstanceId(), taskActionParam.getCurrentTaskDefKey(), taskActionParam.getTargetTaskDefKey(), taskActionParam.getCommentResult());
        } else if (TaskActionEnum.CONFIRMENQUIRE.value.equals(actionType)) {
            //确认问询
            return WorkflowService.taskConfirmEnquire(taskActionParam.getUserId(), taskActionParam.getTaskId());
        } else if (TaskActionEnum.REVOKE.value.equals(actionType)) {
            //撤回
            return WorkflowService.taskRevoke(taskActionParam.getUserId(), taskActionParam.getTaskId(), taskActionParam.getTargetTaskDefKey());
        } else if (TaskActionEnum.CANCEL.value.equals(actionType)) {
            //取消
            return WorkflowService.taskCancel(taskActionParam.getUserId(), taskActionParam.getProcessInstanceId());
        } else if (TaskActionEnum.SUSPEND.value.equals(actionType)) {
            //挂起流程
            return WorkflowService.processSuspend(taskActionParam.getUserId(), taskActionParam.getTaskId());
        } else if (TaskActionEnum.ACTIVATE.value.equals(actionType)) {
            //激活流程
            return WorkflowService.processActivate(taskActionParam.getUserId(), taskActionParam.getTaskId());
        }

        return new Result();
    }
}
