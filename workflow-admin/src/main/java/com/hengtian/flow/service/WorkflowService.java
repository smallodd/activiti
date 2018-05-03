package com.hengtian.flow.service;

import com.hengtian.common.param.ProcessParam;
import com.hengtian.common.param.TaskParam;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.model.TUserTask;
import org.activiti.engine.task.Task;

public interface WorkflowService {
    /**
     * 申请任务
     *
     * @param processParam
     * @return
     */
    Result startProcessInstance(ProcessParam processParam);

    /**
     * 设置审批人
     *
     * @param task
     * @param tUserTask
     * @return
     */
    Boolean setApprover(Task task, TUserTask tUserTask);

    /**
     * 审批接口
     *
     * @param task
     * @param taskParam
     * @return
     */
    Object approveTask(Task task, TaskParam taskParam);

    /**
     * 任务认领 部门，角色，组审批时，需具体人员认领任务
     *
     * @param userId 认领人ID
     * @param taskId 任务ID
     * @param workId 节点任务具体执行ID，一个任务taskId对应多个审批人，每个审批人对应一个执行ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 14:55
     */
    Result taskClaim(String userId, String taskId, String workId);

    /**
     * 取消任务认领
     *
     * @param userId 认领人ID
     * @param taskId 任务ID
     * @param workId 节点任务具体执行ID，一个任务taskId对应多个审批人，每个审批人对应一个执行ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 14:55
     */
    Result taskUnclaim(String userId, String taskId, String workId);

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
     * @param userId            操作人ID
     * @param processInstanceId 流程实例ID
     * @param currentTaskDefKey 问询任务节点KEY
     * @param targetTaskDefKey  问询任务节点KEY
     * @param commentResult     意见
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskEnquire(String userId, String processInstanceId, String currentTaskDefKey, String targetTaskDefKey, String commentResult);

    /**
     * 问询确认
     *
     * @param userId        操作人ID
     * @param askId         问询id
     * @param answerComment 确认信息
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskConfirmEnquire(String userId, String askId, String answerComment);

    /**
     * 驳回
     *
     * @param userId        操作人ID
     * @param taskId        任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskRollback(String userId, String taskId);

    /**
     * 撤回
     *
     * @param userId        操作人ID
     * @param taskId        任务ID
     * @param targetTaskKey 要撤回到的任务节点key
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskRevoke(String userId, String taskId, String targetTaskKey);

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
     * 挂起流程
     *
     * @param userId            操作人ID
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result processSuspend(String userId, String processInstanceId);

    /**
     * 激活流程
     *
     * @param userId            操作人ID
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:03
     */
    Result processActivate(String userId, String processInstanceId);

    /**
     * 问询意见查询接口
     *
     * @param userId 操作人ID
     * @param askId  问询id
     * @return
     */
    Result askComment(String userId, String askId);

    /**
     * 未办任务列表
     *
     * @param taskQueryParam 任务查询条件
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:35
     */
    PageInfo openTaskList(TaskQueryParam taskQueryParam);

    /**
     * 已办任务列表
     *
     * @param taskQueryParam 任务查询条件
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:35
     */
    PageInfo closeTaskList(TaskQueryParam taskQueryParam);

    /**
     * 待处理任务（包括待认领和待办任务）
     *
     * @param taskQueryParam 任务查询条件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:01
     */
    PageInfo activeTaskList(TaskQueryParam taskQueryParam);

    /**
     * 待认领任务列表， 任务签收后变为待办任务，待办任务可取消签认领
     *
     * @param taskQueryParam 任务查询条件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 15:59
     */
    PageInfo claimTaskList(TaskQueryParam taskQueryParam);
}
