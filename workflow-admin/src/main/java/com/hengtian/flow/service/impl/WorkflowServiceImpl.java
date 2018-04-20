package com.hengtian.flow.service.impl;

import com.google.common.collect.Maps;
import com.hengtian.common.enums.ResultEnum;
import com.hengtian.common.enums.TaskStatus;
import com.hengtian.common.enums.TaskType;
import com.hengtian.common.enums.TaskVariable;
import com.hengtian.common.result.Result;
import com.hengtian.common.workflow.cmd.DeleteActiveTaskCmd;
import com.hengtian.common.workflow.cmd.StartActivityCmd;
import com.hengtian.flow.model.RemindTask;
import com.hengtian.flow.service.RemindTaskService;
import com.hengtian.flow.service.WorkflowService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public class WorkflowServiceImpl implements WorkflowService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RemindTaskService remindTaskService;

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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result taskJump(String userId, String taskId, String targetTaskDefKey) {
        //查询任务
        TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
        if (taskEntity == null) {
            log.error("任务不存在taskId:{}", taskId);
            return new Result(false, "任务跳转失败");
        }
        //跳转前终止原任务流程
        Command<Void> deleteCmd = new DeleteActiveTaskCmd(taskEntity, "jump", true);
        managementService.executeCommand(deleteCmd);

        //查询流程实例
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(taskEntity.getProcessDefinitionId());
        //查询任务节点
        ActivityImpl activity = processDefinitionEntity.findActivity(targetTaskDefKey);
        //从跳转目标节点开启新的任务流程
        Command<Void> startCmd = new StartActivityCmd(taskEntity.getExecutionId(), activity);
        managementService.executeCommand(startCmd);
        String assignee = taskEntity.getAssignee();
        if (StringUtils.isNotBlank(assignee)) {
            Task task = taskService.createTaskQuery().processInstanceId(taskEntity.getProcessInstanceId()).singleResult();
            taskService.setOwner(task.getId(), assignee);
        }
        //初始化任务属性值 todo
        return new Result(true, "任务跳转成功");
    }

    /**
     * todo 事务 && 用户组权限判断
     * 转办 管理员权限不受限制，可以任意设置转办
     *
     * @param userId       操作人ID
     * @param taskId       任务ID
     * @param targetUserId 转办人ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:00
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result taskTransfer(String userId, String taskId, String targetUserId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return new Result(false, "任务不存在");
        }
        //todo 用户组权限判断

        String assignee = task.getAssignee();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        //获取参数: 任务类型
        String taskType = (String) taskService.getVariable(taskId, taskDefinitionKey + ":" + TaskVariable.TASKTYPE.value);
        if (TaskType.COUNTERSIGN.value.equals(taskType) || TaskType.CANDIDATEUSER.value.equals(taskType)) {
            //会签 | 修改会签人
            String candidateIds = taskService.getVariable(taskId, taskDefinitionKey + ":" + TaskVariable.TASKUSER.value) + "";
            if (StringUtils.contains(candidateIds, targetUserId)) {
                return new Result(false, "【" + targetUserId + "】已在当前任务中<br/>（同一任务节点同一个人最多可办理一次）");
            }
            taskService.setAssignee(taskId, assignee.replace(userId, targetUserId));
            //修改会签人相关属性值
            Map<String, Object> variable = Maps.newHashMap();
            variable.put(taskDefinitionKey + ":" + userId, userId + ":" + TaskStatus.TRANSFER.value);
            variable.put(taskDefinitionKey + ":" + targetUserId, targetUserId + ":" + TaskStatus.UNFINISHED.value);
            variable.put(taskDefinitionKey + ":" + TaskVariable.TASKUSER.value, candidateIds.replace(userId, targetUserId));
            taskService.setVariablesLocal(taskId, variable);
        } else {
            Map<String, Object> variable = Maps.newHashMap();
            variable.put(taskDefinitionKey + ":" + userId, TaskStatus.TRANSFER.value);
            variable.put(taskDefinitionKey + ":" + targetUserId, targetUserId + ":" + TaskStatus.UNFINISHED.value);
            variable.put(taskDefinitionKey + ":" + TaskVariable.TASKUSER.value, targetUserId);
            taskService.setVariablesLocal(taskId, variable);
            taskService.setAssignee(taskId, targetUserId);
            if (StringUtils.isNoneBlank(assignee)) {
                taskService.setOwner(taskId, assignee);
            }
        }
        return new Result(true, "转办任务成功");
    }

    /**
     * 催办 只有申请人可以催办
     *
     * @param userId 操作人ID
     * @param taskId 任务 ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:00
     */
    @Override
    public Result taskRemind(String userId, String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }

        RemindTask remindTask = new RemindTask();
        remindTask.setReminderId(userId);
        remindTask.setProcInstId(task.getProcessInstanceId());
        remindTask.setTaskId(taskId);
        remindTask.setTaskName(task.getName());
        remindTask.setIsComplete(0);

        boolean insertFlag = remindTaskService.insert(remindTask);
        if (insertFlag) {
            //发送邮件
        }
        return null;
    }

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
    @Override
    public Result taskEnquire(String userId, String taskId, String targetTaskDefKey) {
        return null;
    }

    /**
     * 问询确认
     *
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
     *
     * @param userId            操作人ID
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
     *
     * @param userId            操作人ID
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
     *
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
     *
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
