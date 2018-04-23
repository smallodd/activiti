package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Maps;
import com.hengtian.common.enums.ResultEnum;
import com.hengtian.common.enums.TaskStatusEnum;
import com.hengtian.common.enums.TaskStatus;
import com.hengtian.common.enums.TaskType;
import com.hengtian.common.enums.TaskVariable;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.workflow.cmd.DeleteActiveTaskCmd;
import com.hengtian.common.workflow.cmd.StartActivityCmd;
import com.hengtian.enquire.model.EnquireTask;
import com.hengtian.enquire.service.EnquireService;
import com.hengtian.flow.model.RemindTask;
import com.hengtian.flow.service.RemindTaskService;
import com.hengtian.flow.service.WorkflowService;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RemindTaskService remindTaskService;

    @Autowired
    private EnquireService enquireService;

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
        //todo 并行分支校验,不允许跳出分支

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
        //todo 初始化任务属性值
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
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        //todo 用户组权限判断
        if (!ConstantUtils.ADMIN_ID.equals(userId) && !userId.equals(task.getOwner())) {
            return new Result(false, "您所在的用户组没有权限进行该操作");
        }
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
        remindTask.setIsFinished(TaskStatusEnum.REMIND_UNFINISHED.status);

        boolean insertFlag = remindTaskService.insert(remindTask);
        if (insertFlag) {
            //发送邮件

            return new Result(true, ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg);
        }
        return new Result(false, ResultEnum.FAIL.code, ResultEnum.FAIL.msg);
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
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        EnquireTask enquireTask = new EnquireTask();
        enquireTask.setProcInstId(task.getProcessInstanceId());
        enquireTask.setCurrentTaskId(taskId);
        enquireTask.setCurrentTaskKey(task.getTaskDefinitionKey());
        enquireTask.setIsAskEnd(0);
        enquireTask.setAskTaskKey(targetTaskDefKey);
        enquireTask.setCreateTime(new Date());
        enquireTask.setUpdateTime(new Date());
        enquireTask.setCreateId(userId);
        enquireTask.setUpdateId(userId);
        enquireTask.setAskUserId(userId);
        boolean success = enquireService.insert(enquireTask);
        if (!success) {
            return new Result(false, "问询失败");
        }
        return new Result(true, "问询成功");
    }

    /**
     * 问询确认
     * todo 问询详情
     *
     * @param userId 操作人ID
     * @param taskId 需问询确认的任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    public Result taskConfirmEnquire(String userId, String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        EntityWrapper<EnquireTask> wrapper = new EntityWrapper<>();
        wrapper.where("`ask_user_id`={0}", userId)
                .and("is_ask_end={0}", 0)
                .and("ask_task_key={0}", task.getTaskDefinitionKey());
        EnquireTask enquireTask = enquireService.selectOne(wrapper);
        enquireTask.setUpdateTime(new Date());
        enquireTask.setIsAskEnd(1);
        boolean success = enquireService.updateById(enquireTask);
        if (!success) {
            return new Result(false, "问询确认失败");
        }
        return new Result(true, "问询确认成功");
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
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        //todo 撤回
        runtimeService.deleteProcessInstance(processInstanceId, "revoke");
        return new Result(true, "撤回成功");
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

    /**
     * 未办任务列表
     * @param taskQueryParam 任务查询条件
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:35
     */
    @Override
    public PageInfo taskOpenList(TaskQueryParam taskQueryParam) {
        String con = " WHERE trt.STATUS = " + TaskStatusEnum.OPEN.status;
        String re = "SELECT art.*";
        String reC = "SELECT COUNT(*)";
        StringBuffer sb = new StringBuffer();
        sb.append(" FROM t_ru_task AS trt LEFT JOIN act_ru_task AS art ON trt.TASK_ID=art.ID_ ");
        if(StringUtils.isNotBlank(taskQueryParam.getAppKey())){
            sb.append(" LEFT JOIN t_app_procinst AS tap ON art.PROC_INST_ID_=tap.PROC_INST_ID ");
            con = con + " AND tap.APP_KEY LIKE '%" + taskQueryParam.getAppKey() + "%' ";
        }
        if(StringUtils.isNotBlank(taskQueryParam.getTitle())){
            sb.append(" LEFT JOIN act_hi_procinst AS ahp ON art.PROC_INST_ID_=ahp.PROC_INST_ID_ ");
            con = con + " AND tap.APP_KEY LIKE '%" + taskQueryParam.getAppKey() + "%' ";
        }
        if(StringUtils.isNotBlank(taskQueryParam.getTaskName())){
            con = con + " AND art.NAME_ LIKE '%" + taskQueryParam.getTaskName() + "%' ";
        }
        if(StringUtils.isNotBlank(taskQueryParam.getUserId())){
            con = con + " AND art.ASSIGNEE_ LIKE '%" + taskQueryParam.getUserId() + "%' ";
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPageNum(),taskQueryParam.getPageSize());
        String sql = sb.toString() + con;
        List<Task> tasks = taskService.createNativeTaskQuery().sql(re + sql).listPage(pageInfo.getFrom(), pageInfo.getSize());
        pageInfo.setRows(tasks);
        pageInfo.setTotal((int)taskService.createNativeTaskQuery().sql(reC + sql).count());
        return pageInfo;
    }

    /**
     * 已办任务列表
     * @param taskQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @Override
    public PageInfo taskCloseList(TaskQueryParam taskQueryParam) {
        String con = " WHERE trt.STATUS IN(" + TaskStatusEnum.getCloseStatus()+") ";
        String re = "SELECT art.*";
        String reC = "SELECT COUNT(*)";
        StringBuffer sb = new StringBuffer();
        sb.append(" FROM t_ru_task AS trt LEFT JOIN act_hi_taskinst AS art ON trt.TASK_ID=art.ID_ ");
        if(StringUtils.isNotBlank(taskQueryParam.getAppKey())){
            sb.append(" LEFT JOIN t_app_procinst AS tap ON art.PROC_INST_ID_=tap.PROC_INST_ID ");
            con = con + " AND tap.APP_KEY LIKE '%" + taskQueryParam.getAppKey() + "%' ";
        }
        if(StringUtils.isNotBlank(taskQueryParam.getTitle())){
            sb.append(" LEFT JOIN act_hi_procinst AS ahp ON art.PROC_INST_ID_=ahp.PROC_INST_ID_ ");
            con = con + " AND tap.APP_KEY LIKE '%" + taskQueryParam.getAppKey() + "%' ";
        }
        if(StringUtils.isNotBlank(taskQueryParam.getTaskName())){
            con = con + " AND art.NAME_ LIKE '%" + taskQueryParam.getTaskName() + "%' ";
        }
        if(StringUtils.isNotBlank(taskQueryParam.getUserId())){
            con = con + " AND art.ASSIGNEE_ LIKE '%" + taskQueryParam.getUserId() + "%' ";
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPageNum(),taskQueryParam.getPageSize());
        String sql = sb.toString() + con;
        List<HistoricTaskInstance> tasks = historyService.createNativeHistoricTaskInstanceQuery().sql(re + sql).listPage(pageInfo.getFrom(), pageInfo.getSize());
        
        pageInfo.setRows(tasks);
        pageInfo.setTotal((int)historyService.createNativeHistoricTaskInstanceQuery().sql(reC + sql).count());
        return pageInfo;
    }
}
