package com.hengtian.flow.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.hengtian.application.model.AppModel;
import com.hengtian.application.service.AppModelService;
import com.hengtian.common.enums.*;
import com.hengtian.common.param.ProcessParam;
import com.hengtian.common.param.TaskParam;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import com.hengtian.common.result.TaskNodeResult;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.common.utils.DateUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.workflow.cmd.DeleteActiveTaskCmd;
import com.hengtian.common.workflow.cmd.StartActivityCmd;
import com.hengtian.enquire.model.EnquireTask;
import com.hengtian.enquire.service.EnquireService;
import com.hengtian.flow.model.AppProcinst;
import com.hengtian.flow.model.RemindTask;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.*;
import com.hengtian.flow.vo.CommentVo;
import com.hengtian.system.model.SysUser;
import com.hengtian.system.service.SysUserService;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private AppModelService appModelService;

    @Autowired
    private AppProcinstService appProcinstService;

    @Autowired
    TUserTaskService tUserTaskService;

    @Autowired
    TRuTaskService tRuTaskService;

    @Override
    public Result startProcessInstance(ProcessParam processParam) {
        Result result=new Result();
        String jsonVariables = processParam.getJsonVariables();
        Map<String, Object> variables = new HashMap<>();
        if (StringUtils.isNotBlank(jsonVariables)) {
            variables = JSON.parseObject(jsonVariables);
        }

        EntityWrapper<AppModel> wrapperApp = new EntityWrapper();

        wrapperApp.where("app_key={0}", processParam.getAppKey()).andNew("model_key={0}", processParam.getProcessDefinitionKey());
        AppModel appModelResult = appModelService.selectOne(wrapperApp);
        //系统与流程定义之间没有配置关系
        if (appModelResult == null) {
            log.info("系统键值：【{}】对应的modelKey:【{}】关系不存在!", processParam.getAppKey(), processParam.getProcessDefinitionKey());
            result.setCode(Constant.RELATION_NOT_EXIT);
            result.setMsg("系统键值：【" + processParam.getAppKey() + "】对应的modelKey:【" + processParam.getProcessDefinitionKey() + "】关系不存在!");
            result.setSuccess(false);
            return result;

        }
        //校验当前业务主键是否已经在系统中存在
        boolean isInFlow = checkBusinessKeyIsInFlow(processParam.getProcessDefinitionKey(), processParam.getBussinessKey(), processParam.getAppKey());

        if (isInFlow) {
            log.info("业务主键【{}】已经提交过任务", processParam.getBussinessKey());
            //已经创建过则返回错误信息
            result.setSuccess(false);
            result.setMsg("此条信息已经提交过任务");
            result.setCode(Constant.BUSSINESSKEY_EXIST);
            return result;
        } else {
            variables.put("customApprover", processParam.isCustomApprover());
            variables.put("appKey", processParam.getAppKey());
            //生成任务
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processParam.getProcessDefinitionKey(), processParam.getBussinessKey(), variables);

            //添加应用-流程实例对应关系
            AppProcinst appProcinst = new AppProcinst(processParam.getAppKey(), processInstance.getProcessInstanceId());
            appProcinstService.insert(appProcinst);

            //给对应实例生成标题
            runtimeService.setProcessInstanceName(processInstance.getId(), processParam.getTitle());
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().latestVersion().singleResult();
            //查询创建完任务之后生成的任务信息
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
            //String aa=net.sf.json.JSONObject.fromObject(taskList);
            if (!processParam.isCustomApprover()) {
                log.info("工作流平台设置审批人");
                for (int i = 0; i < taskList.size(); i++) {
                    Task task = taskList.get(0);
                    EntityWrapper entityWrapper = new EntityWrapper();
                    entityWrapper.where("proc_def_key={0}", processParam.getProcessDefinitionKey()).andNew("task_def_key={0}", task.getTaskDefinitionKey()).andNew("version_={0}", processDefinition.getVersion());
                    //查询当前任务任务节点信息
                    TUserTask tUserTask = tUserTaskService.selectOne(entityWrapper);
                    boolean flag = setApprover(task, tUserTask);
                    if (!flag) {
                        taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), "生成扩展任务时失败，删除任务！");//备注
                        runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), "");
                        historyService.deleteHistoricProcessInstance(processInstance.getProcessInstanceId());//(顺序不能换)

                        result.setSuccess(false);
                        result.setCode(Constant.FAIL);
                        result.setMsg("生成扩展任务失败，删除其他信息");
                        return result;
                    }
                }
                result.setSuccess(true);
                result.setCode(Constant.SUCCESS);
                result.setMsg("申请成功");
                result.setObj(TaskNodeResult.toTaskNodeResultList(taskList));

            } else {
                log.info("业务平台设置审批人");
//
                result.setSuccess(true);
                result.setCode(Constant.SUCCESS);
                result.setMsg("申请成功");
                result.setObj(TaskNodeResult.toTaskNodeResultList(taskList));

            }
        }
        return result;
    }
    /**
     * 设置审批人接口
     *
     * @param task
     * @param tUserTask
     */
    @Override
    public Boolean setApprover(Task task, TUserTask tUserTask) {
        try {
            Map<String,Object> map=taskService.getVariables(task.getId());
            String approvers = tUserTask.getCandidateIds();
            String [] strs=approvers.split(",");
            List list=  Arrays.asList(strs);
            Set set = new HashSet(list);
            String [] rid=(String [])set.toArray(new String[0]);

//taskService.setAssignee(task.getId(), approvers);
            TRuTask tRuTask = new TRuTask();

            List<TRuTask> tRuTaskList=new ArrayList<>();

            //生成扩展任务信息
            for(String approver : rid) {
                tRuTask.setApprover(approver);
                tRuTask.setApproverType(tUserTask.getAssignType());
                tRuTask.setOwer(task.getOwner());
                tRuTask.setTaskId(task.getId());
                tRuTask.setTaskType(tUserTask.getTaskType());
                if(AssignType.ROLE.code.intValue()==tUserTask.getAssignType().intValue()||AssignType.GROUP.code.intValue()==tUserTask.getAssignType().intValue()||AssignType.DEPARTMENT.code.intValue()==tUserTask.getAssignType().intValue()) {
                    tRuTask.setStatus(-1);
                }else{
                    tRuTask.setStatus(0);
                    tRuTask.setApproverReal(approver);
                }tRuTask.setExpireTime(task.getDueDate());
                tRuTask.setAppKey(Integer.valueOf(map.get("appKey").toString()));

                tRuTaskService.insert(tRuTask);
            }



            return true;
        }catch (Exception e){
            log.error("设置审批人失败",e);
            return false;
        }
    }
    /**
     * 判断某个用户是否拥有审批某个角色的权限
     * @param task
     * @param taskParam
     * @return
     */
    @Override
    public Object approveTask(Task  task, TaskParam taskParam){

        Result result=new Result();
        result.setCode(Constant.SUCCESS);
        result.setMsg("审批完成");
        EntityWrapper entityWrapper=new EntityWrapper();
        entityWrapper.where("task_id={0}",task.getId());
        entityWrapper.like("approver_real","%"+taskParam.getApprover()+"%");
        ProcessDefinition processDefinition=repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        String jsonVariables = taskParam.getJsonVariables();
        Map<String, Object> variables = new HashMap<>();
        if (StringUtils.isNotBlank(jsonVariables)) {
            variables = JSON.parseObject(jsonVariables);
        }
        Map map = taskService.getVariables(task.getId());
        map.putAll(variables);
        TRuTask ruTask=  tRuTaskService.selectOne(entityWrapper);
        if(ruTask==null){
            result.setMsg("该用户没有操作此任务的权限");
            result.setCode( Constant.TASK_NOT_BELONG_USER);
            return result;
        }else{
            if(ruTask.getApproverType()!=taskParam.getAssignType()){
                result.setMsg("审批人类型参数错误！");
                result.setCode( Constant.PARAM_ERROR);
                return result;
            }
            if(!ruTask.getTaskType().equals(taskParam.getTaskType())){
                result.setMsg("任务类型参数错误！");
                result.setCode( Constant.PARAM_ERROR);
                return result;
            }
            Task t=taskService.createTaskQuery().taskId(task.getId()).singleResult();
            EntityWrapper wrapper=new EntityWrapper();
            wrapper.where("task_def_key={0}",task.getTaskDefinitionKey()).andNew("version_={0}",processDefinition.getVersion()).andNew("proc_def_key={0}",processDefinition.getKey());

            TUserTask tUserTask=tUserTaskService.selectOne(wrapper);
            if(TaskType.COUNTERSIGN.value.equals(tUserTask.getTaskType())) {


                int total= (int) map.get("approve_total");
                int pass= (int) map.get("approve_pass");
                int not_pass= (int) map.get("approve_not_pass");
                total=total+1;

                if(taskParam.getPass()==1){
                    pass=pass+1;
                    //设置原生工作流表哪些审批了
                    taskService.setAssignee(task.getId(),StringUtils.isBlank(t.getAssignee())?taskParam.getApprover():t.getAssignee()+","+taskParam.getApprover()+"_Y");

                }else if(taskParam.getPass()==2){
                    not_pass=not_pass+1;
                    taskService.setAssignee(task.getId(),StringUtils.isBlank(t.getAssignee())?taskParam.getApprover():t.getAssignee()+","+taskParam.getApprover()+"_N");

                }
                map.put("approve_total",total);
                map.put("approve_pass",pass);
                map.put("not_pass",not_pass);
                double passPer = pass / tUserTask.getUserCountTotal();
                double not_pass_per=not_pass/tUserTask.getUserCountTotal();
                taskService.setVariables(task.getId(),map);
                if (passPer >=tUserTask.getUserCountNeed()) {

                    taskService.complete(task.getId());
                    TRuTask tRuTask=new TRuTask();
                    tRuTask.setStatus(1);
                    EntityWrapper truWrapper=new EntityWrapper();
                    truWrapper.where("task_id",t.getId());
                    tRuTaskService.update(tRuTask,truWrapper);


                }else if(not_pass_per>1-tUserTask.getUserCountNeed()){
                    taskService.deleteTask(task.getId(),"任务没有达到通过率");
                    TRuTask tRuTask=new TRuTask();
                    tRuTask.setStatus(2);
                    EntityWrapper truWrapper=new EntityWrapper();
                    truWrapper.where("task_id",t.getId());
                    tRuTaskService.update(tRuTask,truWrapper);
                }

            }else {
                if(taskParam.getPass()==1){
                    //设置原生工作流表哪些审批了
                    taskService.setAssignee(t.getId(),taskParam.getApprover()+"_Y");
                    taskService.complete(t.getId(),map);


                }else if(taskParam.getPass()==2){
                    taskService.setAssignee(task.getId(),taskParam.getApprover()+"_N");
                    taskService.deleteTask(t.getId(),"拒绝此任务");


                }
            }
            List<Task> taskList= taskService.createTaskQuery().processInstanceId(t.getProcessInstanceId()).list();
            if(!Boolean.valueOf(map.get("customApprover").toString())){
                for(Task task1: taskList){
                    EntityWrapper tuserWrapper = new EntityWrapper();
                    tuserWrapper.where("proc_def_key={0}", processDefinition.getKey()).andNew("task_def_key={0}", task.getTaskDefinitionKey()).andNew("version_={0}", processDefinition.getVersion());
                    //查询当前任务任务节点信息
                    TUserTask tUserTask1 = tUserTaskService.selectOne(tuserWrapper);
                    boolean flag = setApprover(task1, tUserTask1);
                    if (!flag) {
                        taskService.addComment(task1.getId(), t.getProcessInstanceId(), "生成扩展任务时失败，删除任务！");//备注
                        runtimeService.deleteProcessInstance(t.getProcessInstanceId(), "");
                        historyService.deleteHistoricProcessInstance(t.getProcessInstanceId());//(顺序不能换)

                        result.setSuccess(false);
                        result.setCode(Constant.FAIL);
                        result.setMsg("生成扩展任务失败，删除其他信息");
                        return result;
                    }
                }
            }
            result.setObj(TaskNodeResult.toTaskNodeResultList(taskList));
            return result;
        }
    }
    /**
     * 校验业务主键是否已经生成过任务
     *
     * @param processDefiniKey
     * @param bussinessKey
     * @param appKey
     * @return
     */
    protected Boolean checkBusinessKeyIsInFlow(String processDefiniKey, String bussinessKey, Integer appKey) {
        TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey(processDefiniKey).processInstanceBusinessKey(bussinessKey);
        taskQuery.processVariableValueEquals("appKey", appKey);
        Task task = taskQuery.singleResult();

        if (task != null) {
            return true;
        }
        return false;
    }

    /**
     * 任务认领 部门，角色，组审批时，需具体人员认领任务
     * @param userId 认领人ID
     * @param  taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 14:55
     */
    @Override
    public Result taskClaim(String userId, String taskId){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task == null){
            return new Result(false,ResultEnum.TASK_NOT_EXIT.code,ResultEnum.TASK_NOT_EXIT.msg);
        }
        String assignee = task.getAssignee();
        if(StringUtils.isNotBlank(assignee)){
            assignee = assignee + "," + userId;
        }else {
            assignee = userId;
        }
        taskService.setAssignee(taskId, assignee);

        return new Result(true, ResultEnum.SUCCESS.code,ResultEnum.SUCCESS.msg);
    }

    /**
     * 取消任务认领
     * @param userId 认领人ID
     * @param  taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 14:55
     */
    @Override
    public Result taskUnclaim(String userId, String taskId){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task == null){
            return new Result(false,ResultEnum.TASK_NOT_EXIT.code,ResultEnum.TASK_NOT_EXIT.msg);
        }
        String assignee = task.getAssignee();
        if(StringUtils.isBlank(assignee)){
            return new Result(false,ResultEnum.TASK_NOT_EXIT.code,ResultEnum.TASK_NOT_EXIT.msg);
        }else if(StringUtils.contains(assignee, userId)){
            List<String> list = Arrays.asList(StringUtils.split(","));
            if(list.contains(userId)){
                list.remove(userId);
            }
            taskService.setAssignee(Joiner.on(",").join(list), assignee);
        }else {
            return new Result(false, ResultEnum.ILLEGAL_REQUEST.code,ResultEnum.ILLEGAL_REQUEST.msg);
        }

        return new Result(true, ResultEnum.SUCCESS.code,ResultEnum.SUCCESS.msg);
    }

    /**
     * 跳转 管理员权限不受限制，可以任意跳转到已完成任务节点
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
     * @param commentResult    意见
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    public Result taskEnquire(String userId, String taskId, String targetTaskDefKey, String commentResult) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        if (StringUtils.isNotBlank(commentResult)) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), commentResult);
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
     * 挂起任务
     *
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    public Result taskSuspend(String userId, String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        //todo 挂起任务
        return new Result(true, "挂起成功");
    }

    /**
     * 激活任务
     *
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:03
     */
    @Override
    public Result taskActivate(String userId, String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        //todo 激活任务
        return new Result(true, "激活成功");
    }

    /**
     * 挂起流程
     *
     * @param userId            操作人ID
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:03
     */
    @Override
    public Result processSuspend(String userId, String processInstanceId) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        runtimeService.suspendProcessInstanceById(processInstanceId);
        return new Result(true, "挂起流程成功");
    }

    /**
     * 激活流程
     *
     * @param userId            操作人ID
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:03
     */
    @Override
    public Result processActivate(String userId, String processInstanceId) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        runtimeService.activateProcessInstanceById(processInstanceId);
        return new Result(true, "激活流程成功");
    }

    /**
     * 问询意见查询接口
     *
     * @param userId 操作人ID
     * @param taskId 流程实例ID
     * @return
     */
    @Override
    public Result enquireComment(String userId, String taskId) {
        List<Comment> commentList = taskService.getTaskComments(taskId);
        List<CommentVo> comments = new ArrayList<>();
        commentList.forEach(comment -> {
            CommentEntity entity = (CommentEntity) comment;
            SysUser user = sysUserService.selectById(comment.getUserId());
            CommentVo vo = new CommentVo();
            vo.setCommentUser(user.getUserName());
            vo.setCommentTime(DateUtils.formatDateToString(comment.getTime()));
            vo.setCommentContent(entity.getMessage());
            comments.add(vo);
        });
        Result result = new Result(true, "查询成功");
        result.setObj(comments);
        return result;
    }

    /**
     * 未办任务列表
     *
     * @param taskQueryParam 任务查询条件
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:35
     */
    @Override
    public PageInfo openTaskList(TaskQueryParam taskQueryParam) {
        String con = " WHERE trt.STATUS = " + TaskStatusEnum.OPEN.status;
        String re = "SELECT art.*";
        String reC = "SELECT COUNT(*)";
        StringBuffer sb = new StringBuffer();
        sb.append(" FROM t_ru_task AS trt LEFT JOIN act_ru_task AS art ON trt.TASK_ID=art.ID_ ");
        if (StringUtils.isNotBlank(taskQueryParam.getAppKey())) {
            sb.append(" LEFT JOIN t_app_procinst AS tap ON art.PROC_INST_ID_=tap.PROC_INST_ID ");
            con = con + " AND tap.APP_KEY LIKE #{appKey}";
        }

        if(StringUtils.isNotBlank(taskQueryParam.getTitle()) || StringUtils.isNotBlank(taskQueryParam.getCreater())){
            sb.append(" LEFT JOIN act_hi_procinst AS ahp ON art.PROC_INST_ID_=ahp.PROC_INST_ID_ ");
            if(StringUtils.isNotBlank(taskQueryParam.getTitle())){
                con = con + " AND tap.APP_KEY LIKE #{title} ";
            }
            if(StringUtils.isNotBlank(taskQueryParam.getCreater())){
                con = con + " AND art.START_USER_ID_ = #{creater} ";
            }
        }

        if(StringUtils.isNotBlank(taskQueryParam.getTaskName())){
            con = con + " AND art.NAME_ LIKE #{taskName} ";
        }

        if(StringUtils.isNotBlank(taskQueryParam.getApprover())){
            con = con + " AND art.ASSIGNEE_ LIKE #{approver} ";
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        String sql = sb.toString() + con;
        List<Task> tasks = taskService.createNativeTaskQuery().sql(re + sql)
                .parameter("appKey",taskQueryParam.getAppKey())
                .parameter("title","%"+taskQueryParam.getTitle()+"%")
                .parameter("creater",taskQueryParam.getCreater())
                .parameter("taskName","%"+taskQueryParam.getTaskName()+"%")
                .parameter("approver","%"+taskQueryParam.getApprover()+"%")
                .listPage(pageInfo.getFrom(), pageInfo.getSize());
        pageInfo.setRows(tasks);
        pageInfo.setTotal((int)taskService.createNativeTaskQuery().sql(reC + sql)
                .parameter("appKey",taskQueryParam.getAppKey())
                .parameter("title","%"+taskQueryParam.getTitle()+"%")
                .parameter("creater",taskQueryParam.getCreater())
                .parameter("taskName","%"+taskQueryParam.getTaskName()+"%")
                .parameter("approver","%"+taskQueryParam.getApprover()+"%")
                .count());
        return pageInfo;
    }

    /**
     * 已办任务列表
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @Override
    public PageInfo closeTaskList(TaskQueryParam taskQueryParam) {
        String con = " WHERE trt.STATUS IN(" + TaskStatusEnum.getCloseStatus() + ") ";
        String re = "SELECT art.*";
        String reC = "SELECT COUNT(*)";
        StringBuffer sb = new StringBuffer();
        sb.append(" FROM t_ru_task AS trt LEFT JOIN act_hi_taskinst AS art ON trt.TASK_ID=art.ID_ ");
        if (StringUtils.isNotBlank(taskQueryParam.getAppKey())) {
            sb.append(" LEFT JOIN t_app_procinst AS tap ON art.PROC_INST_ID_=tap.PROC_INST_ID ");
            con = con + " AND tap.APP_KEY LIKE #{appKey} ";
        }

        if(StringUtils.isNotBlank(taskQueryParam.getTitle()) || StringUtils.isNotBlank(taskQueryParam.getCreater())){
            sb.append(" LEFT JOIN act_hi_procinst AS ahp ON art.PROC_INST_ID_=ahp.PROC_INST_ID_ ");
            if(StringUtils.isNotBlank(taskQueryParam.getTitle())){
                con = con + " AND tap.APP_KEY LIKE #{title} ";
            }
            if(StringUtils.isNotBlank(taskQueryParam.getCreater())){
                con = con + " AND art.START_USER_ID_ = #{creater} ";
            }
        }

        if(StringUtils.isNotBlank(taskQueryParam.getTaskName())){
            con = con + " AND art.NAME_ LIKE #{taskName} ";
        }

        if(StringUtils.isNotBlank(taskQueryParam.getApprover())){
            con = con + " AND art.ASSIGNEE_ LIKE #{approver} ";
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        String sql = sb.toString() + con;

        List<HistoricTaskInstance> tasks = historyService.createNativeHistoricTaskInstanceQuery().sql(re + sql)
                .parameter("appKey",taskQueryParam.getAppKey())
                .parameter("title","%"+taskQueryParam.getTitle()+"%")
                .parameter("creater",taskQueryParam.getCreater())
                .parameter("taskName","%"+taskQueryParam.getTaskName()+"%")
                .parameter("approver","%"+taskQueryParam.getApprover()+"%")
                .listPage(pageInfo.getFrom(), pageInfo.getSize());

        pageInfo.setRows(tasks);

        pageInfo.setTotal((int)historyService.createNativeHistoricTaskInstanceQuery().sql(reC + sql)
                .parameter("appKey",taskQueryParam.getAppKey())
                .parameter("title","%"+taskQueryParam.getTitle()+"%")
                .parameter("creater",taskQueryParam.getCreater())
                .parameter("taskName","%"+taskQueryParam.getTaskName()+"%")
                .parameter("approver","%"+taskQueryParam.getApprover()+"%")
                .count());
        return pageInfo;
    }

    /**
     * 待处理任务（包括待认领和待办任务）
     * @param taskQueryParam 任务查询条件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:01
     */
    @Override
    public PageInfo activeTaskList(TaskQueryParam taskQueryParam) {
        return null;
    }

    /**
     * 待认领任务列表， 任务签收后变为待办任务，待办任务可取消签认领
     * @param taskQueryParam 任务查询条件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 15:59
     */
    @Override
    public PageInfo claimTaskList(TaskQueryParam taskQueryParam) {
        return null;
    }
}