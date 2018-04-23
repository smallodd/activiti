package com.hengtian.flow.service;

import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;

public interface WorkflowService {

    /**
     * 跳转 管理严权限不受限制，可以任意跳转到已完成任务节点
     *
     * @param userId           操作人ID
     * @param taskId           任务ID
     * @param targetTaskDefKey 跳转到的任务节点KEY
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:00
     */
    Result taskJump(String userId, String taskId, String targetTaskDefKey);

    /**
     * 转办 管理严权限不受限制，可以任意设置转办
     *
     * @param userId       操作人ID
     * @param taskId       任务ID
     * @param targetUserId 转办人ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:00
     */
    Result taskTransfer(String userId, String taskId, String targetUserId);

    /**
     * 催办 只有申请人可以催办
     *
     * @param userId 操作人ID
     * @param taskId 任务 ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:00
     */
    Result taskRemind(String userId, String taskId);

    /**
     * 问询
     *
     * @param userId           操作人ID
     * @param taskId           任务ID
     * @param targetTaskDefKey 问询任务节点KEY
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskEnquire(String userId, String taskId, String targetTaskDefKey);

    /**
     * 问询确认
     *
     * @param userId 操作人ID
     * @param taskId 需问询确认的任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskConfirmEnquire(String userId, String taskId);

    /**
     * 撤回
     *
     * @param userId            操作人ID
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskRevoke(String userId, String processInstanceId);

    /**
     * 取消 只有流程发起人方可进行取消操作
     *
     * @param userId            操作人ID
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskCancel(String userId, String processInstanceId);

    /**
     * 挂起任务
     *
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskSuspend(String userId, String taskId);

    /**
     * 激活任务
     *
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:03
     */
    Result taskActivate(String userId, String taskId);

    /**
     * 挂起流程
     *
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result processSuspend(String userId, String taskId);

    /**
     * 激活流程
     *
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:03
     */
    Result processActivate(String userId, String taskId);

    /**
     * 未办任务列表
     *
     * @param taskQueryParam 任务查询条件
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:35
     */
    PageInfo taskOpenList(TaskQueryParam taskQueryParam);

    /**
     * 已办任务列表
     *
     * @param taskQueryParam 任务查询条件
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:35
     */
    PageInfo taskCloseList(TaskQueryParam taskQueryParam);
}
