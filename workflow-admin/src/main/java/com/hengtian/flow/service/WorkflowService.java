package com.hengtian.flow.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.param.ProcessParam;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.param.TaskParam;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.model.RuProcinst;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.model.TaskResult;
import com.hengtian.flow.vo.AssigneeVo;
import com.hengtian.flow.vo.TaskNodeVo;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkflowService extends IService<TaskResult> {
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
    Boolean setAssignee(Task task, TUserTask tUserTask);

    /**
     * 审批接口
     *
     * @param task
     * @param taskParam
     * @return
     */
    Object approveTask(Task task, TaskParam taskParam);

    /**
     * 校验审批人是否有权限审批
     * @param task 任务对象
     * @param assigneeSet 审批人工号
     * @param tRuTasks 节点审批信息
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/4 17:44
     */
    TRuTask validateTaskAssignee(Task task, Set<String> assigneeSet, List<TRuTask> tRuTasks);

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
     * (跳转旧方法，改跳转方法不影响分支，暂时废弃以待他用)
     *
     * @param userId           操作人ID
     * @param taskId           任务ID
     * @param targetTaskDefKey 跳转到的任务节点KEY
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:00
     */
    Result taskJumpOld(String userId, String taskId, String targetTaskDefKey);

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
     * 意见征询
     *
     * @param userId            操作人ID
     * @param processInstanceId 流程实例ID
     * @param currentTaskDefKey 意见征询任务节点KEY
     * @param targetTaskDefKey  意见征询任务节点KEY
     * @param commentResult     意见
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskEnquire(String userId, String processInstanceId, String currentTaskDefKey, String targetTaskDefKey, String commentResult,String askedUserId,String assigneeAgent);

    /**
     * 意见征询确认
     *
     * @param userId        操作人ID
     * @param askId         意见征询id
     * @param answerCommen 确认信息

     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskConfirmEnquire(String userId, String askId, String answerCommen);

    /**
     * 驳回
     *
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @param targetTaskDefKey 退回到的节点key
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskRollback(String userId, String taskId, String targetTaskDefKey);

    /**
     * 撤回
     *
     * @param userId        操作人ID
     * @param taskId        任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result taskRevoke(String userId, String taskId);

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
     * @param taskActionParam
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    Result processSuspend(TaskActionParam taskActionParam, boolean needLog);

    /**
     * 激活流程
     *
     * @param taskActionParam
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:03
     */
    Result processActivate(TaskActionParam taskActionParam, boolean needLog);

    /**
     * 意见征询意见查询接口
     *
     * @param userId 操作人ID
     * @param askId  意见征询id
     * @return
     */
    Result askComment(String userId, String askId);

    /**
     * 未办任务列表
     *
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:35
     */
    void openTaskList(PageInfo pageInfo);

    /**
     * 已办任务列表
     *
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:35
     */
    void closeTaskList(PageInfo pageInfo);

    /**
     * 待处理任务（包括待认领和待办任务）
     *
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:01
     */
    void activeTaskList(PageInfo pageInfo);

    /**
     * 待认领任务列表， 任务签收后变为待办任务，待办任务可取消签认领
     *
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 15:59
     */
    void claimTaskList(PageInfo pageInfo);


    /**
     * 获取父级任务节点
     *
     * @param taskId 当前任务节点id
     * @param userId 操作人ID
     * @param isAll  是否递归获取全部父节点
     * @param  needPerson 是否需要街道带出人员来
     * @return
     */
    Result getBeforeNodes(String taskId, String userId, boolean isAll,boolean needPerson);

    /**
     * 我的任务信息
     * @param taskQueryParam
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/15 11:12
     */
    PageInfo myTaskPage(TaskQueryParam taskQueryParam, String type);

    /**
     * 全部任务信息
     * @param taskQueryParam
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/15 11:12
     */
    PageInfo allTaskPage(TaskQueryParam taskQueryParam, String type);

    /**
     * 我的流程
     *
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 15:59
     */
    void processInstanceList(PageInfo pageInfo);

    /**
     * 待处理任务总数（包括待认领和待办任务）
     *
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:01
     */
    Long activeTaskCount(Map<String,Object> paraMap);

    /**
     * 任务详情
     * @param userId 用户ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/1 9:44
     */
    Result taskDetail(String userId, String taskId);

    /**
     * 在当前任务节点获取下一步审批人
     * @param task 任务
     * @author houjinrong@chtwm.com
     * date 2018/6/6 19:14
     */
    List<TaskNodeVo> getNextAssigneeWhenRoleApprove(TaskInfo task);

    Map getVariables(String processInstanceId);

    Comment  getComments(String taskId,String userId);

    /**
     * 获取用户名称
     * @param userId
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/25 10:44
     */
    String getUserName(String userId);

    /**
     * 获取任务节点审批人信息
     * @param task 任务对象
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/26 10:12
     */
    List<AssigneeVo> getTaskAssignee(TaskInfo task, Integer appKey);

    /**
     * 代理人不为空时，生成加密串，防止爬虫，恶意非法请求
     * @param assignee 审批人
     * @param assigneeAgent 被代理人
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/3 15:53
     */
    String getAssigneeSecret(String assignee, String assigneeAgent);

    /**
     * 流程定义列表
     * @param appKey 应用系统KEY
     * @param nameOrKey 流程定义KEY/流程定义名称
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/15 17:39
     */
    PageInfo queryProcessDefinitionList(Integer appKey, String nameOrKey, Integer page, Integer rows);

    /**
     * 判断是否第一个节点
     * @param task
     * @return
     */
    boolean isFirstNode(TaskInfo task);

    /**
     * 通过业务主键查询流程实例
     * @param appKey 系统应用KEy
     * @param businessKey 业务主键
     * @param suspensionState 挂起状态：1-激活；2-挂起
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/24 11:36
     */
    RuProcinst queryProcessInstanceByBusinessKey(Integer appKey, String businessKey, Integer suspensionState);
}
