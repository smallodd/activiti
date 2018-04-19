package com.hengtian.flow.service.impl;

import com.hengtian.common.result.Result;
import com.hengtian.flow.service.WorkflowService;

public class WorkflowServiceImpl implements WorkflowService {

    /**
     * 跳转 管理严权限不受限制，可以任意跳转到已完成任务节点
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @param targetTaskDefKey 跳转到的任务节点KEY
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:00
     */
    @Override
    public Result taskJump(String userId, String taskId, String targetTaskDefKey) {
        return null;
    }

    /**
     * 转办 管理严权限不受限制，可以任意设置转办
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @param targetUserId 转办人ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:00
     */
    @Override
    public Result taskTransfer(String userId, String taskId, String targetUserId) {
        return null;
    }

    /**
     * 催办 只有申请人可以催办
     * @param userId 操作人ID
     * @param taskId 任务 ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:00
     */
    @Override
    public Result taskRemind(String userId, String taskId) {

        return null;
    }

    /**
     * 问询
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @param targetTaskDefKey 问询任务节点KEY
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    public Result taskEnquire(String userId, String taskId, String targetTaskDefKey) {
        return null;
    }

    /**
     * 问询确认
     * @param userId 操作人ID
     * @param taskId 需问询确认的任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    public Result taskConfirmEnquire(String userId, String taskId) {
        return null;
    }

    /**
     * 撤回
     * @param userId 操作人ID
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    public Result taskRevoke(String userId, String processInstanceId) {
        return null;
    }

    /**
     * 取消 只有流程发起人方可进行取消操作
     * @param userId 操作人ID
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    public Result taskCancel(String userId, String processInstanceId) {
        return null;
    }

    /**
     * 挂起
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    public Result taskSuspend(String userId, String taskId) {
        return null;
    }

    /**
     * 激活
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:03
     */
    @Override
    public Result taskActivate(String userId, String taskId) {
        return null;
    }
}
