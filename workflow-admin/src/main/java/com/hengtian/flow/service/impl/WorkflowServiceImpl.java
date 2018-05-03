package com.hengtian.flow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.workflow.cmd.JumpCmd;
import com.hengtian.flow.model.*;
import com.hengtian.flow.service.*;
import com.hengtian.flow.vo.AskCommentDetailVo;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.NativeHistoricTaskInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WorkflowServiceImpl extends ActivitiUtilServiceImpl implements WorkflowService {

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
    private TAskTaskService tAskTaskService;

    @Autowired
    private AppModelService appModelService;

    @Autowired
    private AppProcinstService appProcinstService;

    @Autowired
    private TUserTaskService tUserTaskService;

    @Autowired
    private TRuTaskService tRuTaskService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    TWorkDetailService workDetailService;

    @Autowired
    private ProcessEngine processEngine;


    @Override
    public Result startProcessInstance(ProcessParam processParam) {
        Result result = new Result();
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
            identityService.setAuthenticatedUserId(processParam.getCreatorId());
            //生成任务
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processParam.getProcessDefinitionKey(), processParam.getBussinessKey(), variables);

            //添加应用-流程实例对应关系
            AppProcinst appProcinst = new AppProcinst(processParam.getAppKey(), processInstance.getProcessInstanceId());
            appProcinstService.insert(appProcinst);

            //给对应实例生成标题
            runtimeService.setProcessInstanceName(processInstance.getId(), processParam.getTitle());

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processParam.getProcessDefinitionKey()).latestVersion().singleResult();
            //查询创建完任务之后生成的任务信息
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
            //String aa=net.sf.json.JSONObject.fromObject(taskList);
            String taskId = "";
            if (!processParam.isCustomApprover()) {
                log.info("工作流平台设置审批人");
                for (int i = 0; i < taskList.size(); i++) {
                    Task task = taskList.get(i);
                    taskId += task.getId();
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
                //存储操作记录
                TWorkDetail tWorkDetail = new TWorkDetail();
                tWorkDetail.setCreateTime(new Date());
                tWorkDetail.setDetail("工号【" + processParam.getCreatorId() + "】开启了" + processParam.getTitle() + "任务");
                tWorkDetail.setProcessInstanceId(processInstance.getProcessInstanceId());
                tWorkDetail.setOperator(processParam.getCreatorId());
                tWorkDetail.setTaskId(taskId);

                workDetailService.insert(tWorkDetail);
            } else {
                log.info("业务平台设置审批人");
//
                result.setSuccess(true);
                result.setCode(Constant.SUCCESS);
                result.setMsg("申请成功");
                result.setObj(TaskNodeResult.toTaskNodeResultList(taskList));

                TWorkDetail tWorkDetail = new TWorkDetail();
                tWorkDetail.setCreateTime(new Date());
                tWorkDetail.setDetail("工号【" + processParam.getCreatorId() + "】开启了" + processParam.getTitle() + "任务");
                tWorkDetail.setProcessInstanceId(processInstance.getProcessInstanceId());
                tWorkDetail.setOperator(processParam.getCreatorId());
                tWorkDetail.setTaskId(taskId);

                workDetailService.insert(tWorkDetail);
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
        log.info("进入设置审批人接口,tUserTask参数{}", JSONObject.toJSONString(tUserTask));
        try {
            //获取任务中的自定义参数
            Map<String, Object> map = taskService.getVariables(task.getId());
            String approvers = tUserTask.getCandidateIds();
            String[] strs = approvers.split(",");
            List list = Arrays.asList(strs);
            Set set = new HashSet(list);
            String[] rid = (String[]) set.toArray(new String[0]);
            TRuTask tRuTask = new TRuTask();

            //生成扩展任务信息
            for (String approver : rid) {
                tRuTask.setTaskId(task.getId());
                tRuTask.setApprover(approver);
                EntityWrapper entityWrapper = new EntityWrapper();
                entityWrapper.where("task_id={0}", task.getId()).andNew("approver={0}", approver);
                TRuTask tRu = tRuTaskService.selectOne(entityWrapper);
                if (tRu != null) {
                    continue;
                }
                tRuTask.setApproverType(tUserTask.getAssignType());
                tRuTask.setOwer(task.getOwner());

                tRuTask.setTaskType(tUserTask.getTaskType());
                //判断如果是非人员审批，需要认领之后才能审批
                if (AssignType.ROLE.code.intValue() == tUserTask.getAssignType().intValue() || AssignType.GROUP.code.intValue() == tUserTask.getAssignType().intValue() || AssignType.DEPARTMENT.code.intValue() == tUserTask.getAssignType().intValue()) {
                    tRuTask.setStatus(-1);
                } else {
                    tRuTask.setStatus(0);
                    tRuTask.setApproverReal(approver);
                }
                tRuTask.setExpireTime(task.getDueDate());
                tRuTask.setAppKey(Integer.valueOf(map.get("appKey").toString()));
                tRuTask.setProcInstId(task.getProcessInstanceId());
                tRuTaskService.insert(tRuTask);
            }


            log.info("设置审批人结束");
            return true;
        } catch (Exception e) {
            log.error("设置审批人失败", e);
            return false;
        }
    }

    /**
     * 审批任务
     *
     * @param task
     * @param taskParam
     * @return
     */
    @Override
    public Object approveTask(Task task, TaskParam taskParam) {
        log.info("审批接口进入，传入参数taskParam{}", JSONObject.toJSONString(taskParam));
        Result result = new Result();
        result.setCode(Constant.SUCCESS);
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.where("task_id={0}", task.getId());
        entityWrapper.like("approver_real", "%" + taskParam.getApprover() + "%");
        //查询流程定义信息
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        String jsonVariables = taskParam.getJsonVariables();
        Map<String, Object> variables = new HashMap<>();
        if (StringUtils.isNotBlank(jsonVariables)) {
            variables = JSON.parseObject(jsonVariables);
        }
        //获取任务参数
        Map map = taskService.getVariables(task.getId());
        map.putAll(variables);
        TRuTask ruTask = tRuTaskService.selectOne(entityWrapper);
        if (ruTask == null) {
            result.setMsg("该用户没有操作此任务的权限");
            result.setCode(Constant.TASK_NOT_BELONG_USER);
            return result;
        } else {
            if (ruTask.getApproverType() != taskParam.getAssignType()) {
                result.setMsg("审批人类型参数错误！");
                result.setCode(Constant.PARAM_ERROR);
                return result;
            }
            if (!ruTask.getTaskType().equals(taskParam.getTaskType())) {
                result.setMsg("任务类型参数错误！");
                result.setCode(Constant.PARAM_ERROR);
                return result;
            }
            if (taskParam.getPass() != 1 && taskParam.getPass() != 2) {
                result.setMsg("任务类型参数错误！");
                result.setCode(Constant.PARAM_ERROR);
                return result;
            }
            Task t = taskService.createTaskQuery().taskId(task.getId()).singleResult();
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.where("task_def_key={0}", task.getTaskDefinitionKey()).andNew("version_={0}", processDefinition.getVersion()).andNew("proc_def_key={0}", processDefinition.getKey());

            TUserTask tUserTask = tUserTaskService.selectOne(wrapper);
            identityService.setAuthenticatedUserId(taskParam.getApprover());
            taskService.addComment(taskParam.getTaskId(), task.getProcessInstanceId(), taskParam.getComment());

            if (TaskType.COUNTERSIGN.value.equals(tUserTask.getTaskType())) {


                int total = (int) map.get("approve_total");
                int pass = (int) map.get("approve_pass");
                int not_pass = (int) map.get("approve_not_pass");
                total = total + 1;

                if (taskParam.getPass() == 1) {
                    pass = pass + 1;
                    //设置原生工作流表哪些审批了
                    taskService.setAssignee(task.getId(), StringUtils.isBlank(t.getAssignee()) ? taskParam.getApprover() : t.getAssignee() + "," + taskParam.getApprover() + "_Y");

                } else if (taskParam.getPass() == 2) {
                    not_pass = not_pass + 1;
                    taskService.setAssignee(task.getId(), StringUtils.isBlank(t.getAssignee()) ? taskParam.getApprover() : t.getAssignee() + "," + taskParam.getApprover() + "_N");

                }
                map.put("approve_total", total);
                map.put("approve_pass", pass);
                map.put("not_pass", not_pass);
                double passPer = pass / tUserTask.getUserCountTotal();
                double not_pass_per = not_pass / tUserTask.getUserCountTotal();
                taskService.setVariables(task.getId(), map);
                if (passPer >= tUserTask.getUserCountNeed()) {

                    taskService.complete(task.getId(), map);
                    TRuTask tRuTask = new TRuTask();
                    tRuTask.setStatus(1);
                    EntityWrapper truWrapper = new EntityWrapper();
                    truWrapper.where("task_id={0}", t.getId()).andNew("approver_real={0}", taskParam.getApprover());
                    tRuTaskService.update(tRuTask, truWrapper);
                    EntityWrapper wra = new EntityWrapper();
                    wra.where("task_id={0}", t.getId()).andNew("status={0}", 0);
                    tRuTask.setStatus(3);
                    tRuTaskService.update(tRuTask, wra);
                    result.setSuccess(true);

                } else if (not_pass_per > 1 - tUserTask.getUserCountNeed()) {
                    taskService.deleteTask(task.getId(), "任务没有达到通过率");
                    TRuTask tRuTask = new TRuTask();
                    tRuTask.setStatus(2);
                    EntityWrapper truWrapper = new EntityWrapper();
                    truWrapper.where("task_id={0}", t.getId()).andNew("approver_real={0}", taskParam.getApprover());
                    tRuTaskService.update(tRuTask, truWrapper);

                    EntityWrapper wra = new EntityWrapper();
                    wra.where("task_id={0}", t.getId()).andNew("status={0}", 0);
                    tRuTask.setStatus(3);
                    tRuTaskService.update(tRuTask, wra);
                    result.setMsg("任务已经拒绝！");
                    result.setCode(Constant.SUCCESS);
                    result.setSuccess(true);

                    TWorkDetail tWorkDetail = new TWorkDetail();
                    tWorkDetail.setTaskId(task.getId());
                    tWorkDetail.setOperator(taskParam.getApprover());
                    tWorkDetail.setProcessInstanceId(task.getProcessInstanceId());
                    tWorkDetail.setCreateTime(new Date());
                    tWorkDetail.setDetail("工号【" + taskParam.getApprover() + "】审批了该任务，审批意见是【" + taskParam.getComment() + "】");
                    workDetailService.insert(tWorkDetail);
                    return result;
                }

            } else {
                if (taskParam.getPass() == 1) {
                    //设置原生工作流表哪些审批了
                    taskService.setAssignee(t.getId(), taskParam.getApprover() + "_Y");
                    taskService.complete(t.getId(), map);
                    TRuTask tRuTask = new TRuTask();
                    tRuTask.setStatus(1);
                    EntityWrapper truWrapper = new EntityWrapper();
                    truWrapper.where("task_id={0}", t.getId()).andNew("approver_real={0}", taskParam.getApprover());
                    ;
                    tRuTaskService.update(tRuTask, truWrapper);
                    result.setSuccess(true);

                } else if (taskParam.getPass() == 2) {
                    //拒绝任务
                    taskService.setAssignee(task.getId(), taskParam.getApprover() + "_N");
                    runtimeService.deleteProcessInstance(task.getProcessInstanceId(), taskParam.getComment());
                    //taskService.deleteTask(t.getId(), "拒绝此任务");
                    TRuTask tRuTask = new TRuTask();
                    tRuTask.setStatus(2);
                    EntityWrapper truWrapper = new EntityWrapper();
                    truWrapper.where("task_id={0}", t.getId()).andNew("approver_real={0}", taskParam.getApprover());
                    tRuTaskService.update(tRuTask, truWrapper);
                    result.setMsg("任务已经拒绝！");
                    result.setCode(Constant.SUCCESS);
                    result.setSuccess(true);


                    TWorkDetail tWorkDetail = new TWorkDetail();
                    tWorkDetail.setTaskId(task.getId());
                    tWorkDetail.setOperator(taskParam.getApprover());
                    tWorkDetail.setProcessInstanceId(task.getProcessInstanceId());
                    tWorkDetail.setCreateTime(new Date());
                    tWorkDetail.setDetail("工号【" + taskParam.getApprover() + "】审批了该任务，审批意见是【" + taskParam.getComment() + "】");
                    workDetailService.insert(tWorkDetail);
                    return result;

                } else {
//                    //通过线上条件完成任务
//                    taskService.setAssignee(task.getId(),taskParam.getApprover()+"_F");
//                    taskService.deleteTask(t.getId(),"拒绝此任务");
//                    result.setMsg("审批类型不存在");
//                    result.setSuccess(false);
                }
            }


            List<String> taskKeys=getNextTaskDefinitionKeys(t,false);
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(t.getProcessInstanceId()).list();
            for(Task tk:tasks){
                if( taskKeys.contains(tk.getTaskDefinitionKey())) {

                    continue;
                }else{
                    managementService.executeCommand(new JumpCmd(tk.getExecutionId(),tk.getTaskDefinitionKey()));
                }
            }

            deleteUnUsedTask(t.getProcessInstanceId());


            List<Task> resultList = taskService.createTaskQuery().processInstanceId(t.getProcessInstanceId()).list();
            //设置审批人处理逻辑
            if (!Boolean.valueOf(map.get("customApprover").toString())) {
                for (Task task1 : resultList) {
                    EntityWrapper tuserWrapper = new EntityWrapper();
                    tuserWrapper.where("proc_def_key={0}", processDefinition.getKey()).andNew("task_def_key={0}", task1.getTaskDefinitionKey()).andNew("version_={0}", processDefinition.getVersion());
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

            //设置操作的明细备注
            result.setObj(TaskNodeResult.toTaskNodeResultList(resultList));
            TWorkDetail tWorkDetail = new TWorkDetail();
            tWorkDetail.setTaskId(task.getId());
            tWorkDetail.setOperator(taskParam.getApprover());
            tWorkDetail.setProcessInstanceId(task.getProcessInstanceId());
            tWorkDetail.setCreateTime(new Date());
            tWorkDetail.setDetail("工号【" + taskParam.getApprover() + "】审批了该任务，审批意见是【" + taskParam.getComment() + "】");
            workDetailService.insert(tWorkDetail);
            return result;
        }
    }

    public void deleteUnUsedTask(String processInstanceId){
        String notDelete = "";
        Task ts = null;
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        //处理删除由于跳转/拿回产生冗余的数据
        EntityWrapper ew = new EntityWrapper();
        ew.where("status={0}", -2).andNew("proc_inst_id={0}", taskList.get(0).getProcessInstanceId());
        TRuTask tRuTask = tRuTaskService.selectOne(ew);
        if (tRuTask != null) {
            HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(tRuTask.getTaskId()).singleResult();
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().executionId(taskInstance.getExecutionId()).orderByTaskCreateTime().asc().list();
            notDelete = list.get(0).getTaskDefinitionKey();
            ts = taskService.createTaskQuery().taskDefinitionKey(notDelete).processInstanceId(list.get(0).getProcessInstanceId()).active().singleResult();

        }
        for (int i = 0; i < taskList.size(); i++) {
            Task tas = taskList.get(i);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String olde = sdf.format(tas.getCreateTime());

            String newDate = (ts == null ? "" : sdf.format(ts.getCreateTime()));
            if (!notDelete.contains(tas.getTaskDefinitionKey()) && tRuTask != null && olde.equals(newDate)) {

                TaskEntity resus = (TaskEntity) taskService.createTaskQuery().taskId(tas.getId()).singleResult();

                resus.setExecutionId(null);
                taskService.saveTask(resus);
                taskService.deleteTask(resus.getId(), true);
                taskList.removeIf(new java.util.function.Predicate<Task>() {
                    @Override
                    public boolean test(Task task) {
                        if (resus.getId().equals(task.getId())) {
                            return true;
                        }
                        return false;
                    }
                });
                EntityWrapper ewe = new EntityWrapper();
                ewe.where("task_id={0}", tRuTask.getTaskId()).andNew("status={0}", -2);
                tRuTaskService.delete(ewe);

            }

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
     * 认领是需要将认领人放置到t_ru_task表的approver_real字段
     *
     * @param userId 认领人ID
     * @param taskId 任务ID
     * @param workId 节点任务具体执行ID，一个任务taskId对应多个审批人，每个审批人对应一个执行ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 14:55
     */
    @Override
    public Result taskClaim(String userId, String taskId, String workId) {
        TRuTask tRuTask = tRuTaskService.selectById(workId);
        if (tRuTask == null || !StringUtils.equals(taskId, tRuTask.getTaskId())) {
            return new Result(false, ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        String assignee = tRuTask.getApproverReal();
        if (StringUtils.isNotBlank(assignee)) {
            assignee = assignee + "," + userId;
        } else {
            assignee = userId;
        }
        tRuTask = new TRuTask();
        tRuTask.setId(workId);
        tRuTask.setApproverReal(assignee);
        boolean updateFlag = tRuTaskService.updateById(tRuTask);
        if (updateFlag) {
            return new Result(true, ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg);
        } else {
            return new Result(false, ResultEnum.FAIL.code, ResultEnum.FAIL.msg);
        }
    }

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
    @Override
    public Result taskUnclaim(String userId, String taskId, String workId) {
        TRuTask tRuTask = tRuTaskService.selectById(workId);
        if (tRuTask == null || !StringUtils.equals(taskId, tRuTask.getTaskId())) {
            return new Result(false, ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        String assignee = tRuTask.getApproverReal();
        if (StringUtils.isBlank(assignee)) {
            return new Result(false, ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        } else if (StringUtils.contains(assignee, userId)) {
            List<String> list = Arrays.asList(StringUtils.split(","));
            if (list.contains(userId)) {
                list.remove(userId);
            }
            assignee = Joiner.on(",").join(list);
        } else {
            return new Result(false, ResultEnum.ILLEGAL_REQUEST.code, ResultEnum.ILLEGAL_REQUEST.msg);
        }
        tRuTask = new TRuTask();
        tRuTask.setId(workId);
        tRuTask.setApproverReal(assignee);
        boolean updateFlag = tRuTaskService.updateById(tRuTask);
        if (updateFlag) {
            return new Result(true, ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg);
        } else {
            return new Result(false, ResultEnum.FAIL.code, ResultEnum.FAIL.msg);
        }
    }

    /**
     * todo 初始化任务属性值
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
        //根据要跳转的任务ID获取其任务
        HistoricTaskInstance hisTask = historyService
                .createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult();
        //进而获取流程实例
        ProcessInstance instance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(hisTask.getProcessInstanceId())
                .singleResult();
        //取得流程定义
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(hisTask.getProcessDefinitionId());
        //获取历史任务的Activity
        ActivityImpl hisActivity = definition.findActivity(targetTaskDefKey);
        //实现跳转
        ExecutionEntity e = managementService.executeCommand(new JumpCmd(hisTask.getExecutionId(), hisActivity.getId()));


        boolean customApprover = (boolean) runtimeService.getVariable(instance.getProcessInstanceId(), "customApprover");

        if (!customApprover) {
            List<TaskEntity> tasks = e.getTasks();
            //设置审批人
            log.info("工作流平台设置审批人");
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if (task.getTaskDefinitionKey().equals(targetTaskDefKey)) {
                    taskId += task.getId();
                    EntityWrapper entityWrapper = new EntityWrapper();
                    entityWrapper.where("proc_def_key={0}", definition.getKey()).andNew("task_def_key={0}", task.getTaskDefinitionKey()).andNew("version_={0}", definition.getVersion());
                    //查询当前任务任务节点信息
                    TUserTask tUserTask = tUserTaskService.selectOne(entityWrapper);
                    boolean flag = setApprover(task, tUserTask);
                }
            }
        }
        return new Result();
    }

    /**
     * todo 用户组权限判断
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
        //用户组权限判断
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
     * @param userId            操作人ID
     * @param processInstanceId 任务流程实例ID
     * @param currentTaskDefKey 当前问询任务节点KEY
     * @param targetTaskDefKey  目标问询任务节点KEY
     * @param commentResult     意见
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result taskEnquire(String userId, String processInstanceId, String currentTaskDefKey, String targetTaskDefKey, String commentResult) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(currentTaskDefKey).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        //todo 可询问节点 应限制只能为上级节点  ,已有询问的不能在询问

        TAskTask askTask = new TAskTask();
        askTask.setProcInstId(task.getProcessInstanceId());
        askTask.setCurrentTaskId(task.getId());
        askTask.setCurrentTaskKey(task.getTaskDefinitionKey());
        askTask.setIsAskEnd(0);
        askTask.setAskTaskKey(targetTaskDefKey);
        askTask.setCreateTime(new Date());
        askTask.setUpdateTime(new Date());
        askTask.setCreateId(userId);
        askTask.setUpdateId(userId);
        askTask.setAskUserId(userId);
        askTask.setAskComment(commentResult);
        boolean success = tAskTaskService.insert(askTask);
        if (!success) {
            return new Result(false, "问询失败");
        }
        return new Result(true, "问询成功");
    }

    /**
     * 问询确认
     *
     * @param userId            操作人ID
     * @param processInstanceId 流程实例ID
     * @param taskDefKey        需问询确认的任务key
     * @param answerComment     确认信息
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result taskConfirmEnquire(String userId, String processInstanceId, String taskDefKey, String answerComment) {
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskDefinitionKey(taskDefKey).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        EntityWrapper<TAskTask> wrapper = new EntityWrapper<>();
        wrapper.where("`ask_user_id`={0}", userId)
                .and("is_ask_end={0}", 0)
                .and("ask_task_key={0}", task.getTaskDefinitionKey());
        TAskTask tAskTask = tAskTaskService.selectOne(wrapper);
        tAskTask.setUpdateTime(new Date());
        tAskTask.setAnswerComment(answerComment);
        tAskTask.setIsAskEnd(1);
        boolean success = tAskTaskService.updateById(tAskTask);
        if (!success) {
            return new Result(false, "问询确认失败");
        }
        return new Result(true, "问询确认成功");
    }

    /**
     * 任务驳回
     *
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    public Result taskRollback(String userId, String taskId) {
        try {
            Map<String, Object> variables;
            // 取得当前任务
            HistoricTaskInstance currTask = historyService
                    .createHistoricTaskInstanceQuery().taskId(taskId)
                    .singleResult();
            // 取得流程实例
            ProcessInstance instance = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(currTask.getProcessInstanceId())
                    .singleResult();
            if (instance == null) {
                //流程结束
                return new Result(ResultEnum.PROCINST_NOT_EXIT.code, ResultEnum.PROCINST_NOT_EXIT.msg);
            }
            variables = instance.getProcessVariables();
            // 取得流程定义
            ProcessDefinitionEntity definition = (ProcessDefinitionEntity) (processEngine.getRepositoryService().getProcessDefinition(currTask
                    .getProcessDefinitionId()));

            if (definition == null) {
                //log.error("流程定义未找到");
                return new Result(ResultEnum.PROCESS_NOT_EXIT.code, ResultEnum.PROCESS_NOT_EXIT.msg);
            }
            // 取得上一步活动
            ActivityImpl currActivity = ((ProcessDefinitionImpl) definition).findActivity(currTask.getTaskDefinitionKey());
            List<PvmTransition> nextTransitionList = currActivity.getIncomingTransitions();
            // 清除当前活动的出口
            List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
            List<PvmTransition> pvmTransitionList = currActivity.getOutgoingTransitions();
            for (PvmTransition pvmTransition : pvmTransitionList) {
                oriPvmTransitionList.add(pvmTransition);
            }
            pvmTransitionList.clear();

            // 建立新出口
            List<TransitionImpl> newTransitions = new ArrayList<TransitionImpl>();
            for (PvmTransition nextTransition : nextTransitionList) {
                PvmActivity nextActivity = nextTransition.getSource();
                ActivityImpl nextActivityImpl = ((ProcessDefinitionImpl) definition)
                        .findActivity(nextActivity.getId());
                TransitionImpl newTransition = currActivity
                        .createOutgoingTransition();
                newTransition.setDestination(nextActivityImpl);
                newTransitions.add(newTransition);
            }
            // 完成任务
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(instance.getId())
                    .taskDefinitionKey(currTask.getTaskDefinitionKey()).list();
            for (Task task : tasks) {
                taskService.complete(task.getId(), variables);
                historyService.deleteHistoricTaskInstance(task.getId());
            }
            // 恢复方向
            for (TransitionImpl transitionImpl : newTransitions) {
                currActivity.getOutgoingTransitions().remove(transitionImpl);
            }
            for (PvmTransition pvmTransition : oriPvmTransitionList) {
                pvmTransitionList.add(pvmTransition);
            }
        } catch (Exception e) {
            return new Result(ResultEnum.FAIL.code, ResultEnum.FAIL.msg);
        }

        return new Result(true, "撤回成功");
    }

    /**
     * 任务撤回
     *
     * @param userId        用户ID
     * @param taskId        任务ID
     * @param targetTaskKey 要撤回到的任务节点key
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/27 17:03
     */
    @Override
    public Result taskRevoke(String userId, String taskId, String targetTaskKey) {
        TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
        if (!isAllowRollback(taskEntity)) {
            return new Result();
        }
        /*//查询任务
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
        List<TaskDefinition> taskDefinitions = getBeforeTaskDefinitions(historicTaskInstance, false);
        //查询流程实例
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(historicTaskInstance.getProcessDefinitionId());
        //跳转前终止原任务流程
        *//*Command<Void> deleteCmd = new DeleteActiveTaskCmd(taskEntity, "jump", true);
        managementService.executeCommand(deleteCmd);*//*

        List<String> freeTaskDefKeys = Lists.newArrayList();
        //开启上一部节点任务
        for(TaskDefinition def : taskDefinitions){
            if(!def.getKey().equals(targetTaskKey)){
                freeTaskDefKeys.add(def.getKey());
                Command<Void> cmd = new CreateHisTaskCmd(taskEntity.getExecutionId());
                managementService.executeCommand(cmd);
            }else{
                //查询任务节点
                //ActivityImpl activity = processDefinitionEntity.findActivity(def.getKey());
                //从跳转目标节点开启新的任务流程
                //Command<Void> startCmd = new StartActivityCmd(taskEntity.getExecutionId(), activity);
                //managementService.executeCommand(startCmd);
                taskJump(userId, taskId, targetTaskKey);
            }
        }

        ExecutionEntity execution = (ExecutionEntity)runtimeService.createExecutionQuery().executionId(taskEntity.getExecutionId()).singleResult();

        //如果是并行任务自动办理其他分支任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(taskEntity.getProcessInstanceId()).list();


        for(Task t : taskList){
            if(freeTaskDefKeys.contains(t.getTaskDefinitionKey())){
                //taskService.setAssignee(t.getId(), "AUTO_FREE");
                taskService.complete(t.getId());

            }
        }*/
        return new Result();
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
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (historicProcessInstance != null) {
            String startUserId = historicProcessInstance.getStartUserId();
            if (startUserId.equals(processInstanceId)) {
                runtimeService.deleteProcessInstance(processInstanceId, "");
            } else {
                return new Result(false, ResultEnum.PERMISSION_DENY.code, ResultEnum.PERMISSION_DENY.msg);
            }
        } else {
            return new Result(false, ResultEnum.PROCINST_NOT_EXIT.code, ResultEnum.PROCINST_NOT_EXIT.msg);
        }

        return new Result(true, ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg);
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
        runtimeService.activateProcessInstanceById(processInstanceId);
        return new Result(true, "激活流程成功");
    }

    /**
     * 问询意见查询接口
     *
     * @param userId            操作人ID
     * @param processInstanceId 流程实例ID
     * @param askTaskKey        任务key
     * @return
     */
    @Override
    public Result askComment(String userId, String processInstanceId, String askTaskKey) {
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskDefinitionKey(askTaskKey).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIT.code, ResultEnum.TASK_NOT_EXIT.msg);
        }
        Result result = new Result(true, "查询成功");
        EntityWrapper<TAskTask> wrapper = new EntityWrapper<>();
        wrapper.where("proc_inst_id={0}", processInstanceId);
        wrapper.where("ask_task_key={0}", askTaskKey);
        TAskTask askTask = tAskTaskService.selectOne(wrapper);
        AskCommentDetailVo detailVo = new AskCommentDetailVo();
        detailVo.setAskComment(askTask.getAskComment());
        detailVo.setAnswerComment(askTask.getAnswerComment());
        detailVo.setProcInstId(askTask.getProcInstId());
        detailVo.setCurrentTaskKey(askTask.getCurrentTaskKey());
        detailVo.setAskTaskKey(askTask.getAskTaskKey());
        result.setObj(detailVo);
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

        if (StringUtils.isNotBlank(taskQueryParam.getTitle()) || StringUtils.isNotBlank(taskQueryParam.getCreater())) {
            sb.append(" LEFT JOIN act_hi_procinst AS ahp ON art.PROC_INST_ID_=ahp.PROC_INST_ID_ ");
            if (StringUtils.isNotBlank(taskQueryParam.getTitle())) {
                con = con + " AND tap.APP_KEY LIKE #{title} ";
            }
            if (StringUtils.isNotBlank(taskQueryParam.getCreater())) {
                con = con + " AND art.START_USER_ID_ = #{creater} ";
            }
        }

        if (StringUtils.isNotBlank(taskQueryParam.getTaskName())) {
            con = con + " AND art.NAME_ LIKE #{taskName} ";
        }

        if (StringUtils.isNotBlank(taskQueryParam.getApprover())) {
            con = con + " AND art.ASSIGNEE_ LIKE #{approver} ";
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        String sql = sb.toString() + con;
        List<Task> tasks = taskService.createNativeTaskQuery().sql(re + sql)
                .parameter("appKey", taskQueryParam.getAppKey())
                .parameter("title", "%" + taskQueryParam.getTitle() + "%")
                .parameter("creater", taskQueryParam.getCreater())
                .parameter("taskName", "%" + taskQueryParam.getTaskName() + "%")
                .parameter("approver", "%" + taskQueryParam.getApprover() + "%")
                .listPage(pageInfo.getFrom(), pageInfo.getSize());
        pageInfo.setRows(tasks);
        pageInfo.setTotal((int) taskService.createNativeTaskQuery().sql(reC + sql)
                .parameter("appKey", taskQueryParam.getAppKey())
                .parameter("title", "%" + taskQueryParam.getTitle() + "%")
                .parameter("creater", taskQueryParam.getCreater())
                .parameter("taskName", "%" + taskQueryParam.getTaskName() + "%")
                .parameter("approver", "%" + taskQueryParam.getApprover() + "%")
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
        sb.append(" FROM act_hi_taskinst AS art ");
        if (StringUtils.isNotBlank(taskQueryParam.getAppKey())) {
            sb.append(" LEFT JOIN t_app_procinst AS tap ON art.PROC_INST_ID_=tap.PROC_INST_ID ");
            con = con + " AND tap.APP_KEY LIKE #{appKey} ";
        }

        if (StringUtils.isNotBlank(taskQueryParam.getTitle()) || StringUtils.isNotBlank(taskQueryParam.getCreater())) {
            sb.append(" LEFT JOIN act_hi_procinst AS ahp ON art.PROC_INST_ID_=ahp.PROC_INST_ID_ ");
            if (StringUtils.isNotBlank(taskQueryParam.getTitle())) {
                con = con + " AND tap.APP_KEY LIKE #{title} ";
            }
            if (StringUtils.isNotBlank(taskQueryParam.getCreater())) {
                con = con + " AND art.START_USER_ID_ = #{creater} ";
            }
        }

        if (StringUtils.isNotBlank(taskQueryParam.getTaskName())) {
            con = con + " AND art.NAME_ LIKE #{taskName} ";
        }

        if (StringUtils.isNotBlank(taskQueryParam.getApprover())) {
            con = con + " AND art.ASSIGNEE_ LIKE #{approver} ";
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        String sql = sb.toString() + con;

        List<HistoricTaskInstance> tasks = historyService.createNativeHistoricTaskInstanceQuery().sql(re + sql)
                .parameter("appKey", taskQueryParam.getAppKey())
                .parameter("title", "%" + taskQueryParam.getTitle() + "%")
                .parameter("creater", taskQueryParam.getCreater())
                .parameter("taskName", "%" + taskQueryParam.getTaskName() + "%")
                .parameter("approver", "%" + taskQueryParam.getApprover() + "_%")
                .listPage(pageInfo.getFrom(), pageInfo.getSize());

        pageInfo.setRows(tasks);

        pageInfo.setTotal((int) historyService.createNativeHistoricTaskInstanceQuery().sql(reC + sql)
                .parameter("appKey", taskQueryParam.getAppKey())
                .parameter("title", "%" + taskQueryParam.getTitle() + "%")
                .parameter("creater", taskQueryParam.getCreater())
                .parameter("taskName", "%" + taskQueryParam.getTaskName() + "%")
                .parameter("approver", "%" + taskQueryParam.getApprover() + "%")
                .count());
        return pageInfo;
    }

    /**
     * 待处理任务（包括待认领和待办任务）
     *
     * @param taskQueryParam 任务查询条件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:01
     */
    @Override
    public PageInfo activeTaskList(TaskQueryParam taskQueryParam) {
        return taskPage(taskQueryParam, TaskListEnum.ACTIVE.type);
    }

    /**
     * 待认领任务列表， 任务签收后变为待办任务，待办任务可取消签认领
     *
     * @param taskQueryParam 任务查询条件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 15:59
     */
    @Override
    public PageInfo claimTaskList(TaskQueryParam taskQueryParam) {
        return taskPage(taskQueryParam, TaskListEnum.CLAIM.type);
    }

    /**
     * 任务相关列表查询
     *
     * @param taskQueryParam
     * @param type
     * @return
     */
    private PageInfo taskPage(TaskQueryParam taskQueryParam, String type) {
        StringBuffer sb = new StringBuffer();
        StringBuffer con = new StringBuffer();
        con.append(" WHERE 1=1 ");
        String re = "SELECT art.*";
        String reC = "SELECT COUNT(*)";

        String approver = taskQueryParam.getApprover();
        String departmentId = null;
        String roleId = null;
        String groupId = null;
        if (TaskListEnum.CLOSE.type.equals(type)) {
            sb.append(" FROM act_hi_taskinst AS art ");
        } else {
            sb.append(" FROM t_ru_task AS trt LEFT JOIN act_ru_task AS art ON trt.TASK_ID=art.ID_ ");
        }
        if (StringUtils.isNotBlank(taskQueryParam.getAppKey())) {
            sb.append(" LEFT JOIN t_app_procinst AS tap ON art.PROC_INST_ID_=tap.PROC_INST_ID ");
            con.append(" AND tap.APP_KEY = #{appKey}");
        }

        if (StringUtils.isNotBlank(taskQueryParam.getTitle()) || StringUtils.isNotBlank(taskQueryParam.getCreater())) {
            sb.append(" LEFT JOIN act_hi_procinst AS ahp ON art.PROC_INST_ID_=ahp.PROC_INST_ID_ ");
            if (StringUtils.isNotBlank(taskQueryParam.getTitle())) {
                con.append(" AND tap.APP_KEY LIKE #{title} ");
            }
            if (StringUtils.isNotBlank(taskQueryParam.getCreater())) {
                con.append(" AND ahp.START_USER_ID_ = #{creater} ");
            }
        }

        if (StringUtils.isNotBlank(taskQueryParam.getTaskName())) {
            con.append(" AND art.NAME_ LIKE #{taskName} ");
        }

        if (StringUtils.isNotBlank(taskQueryParam.getApprover())) {
            if (TaskListEnum.CLOSE.type.equals(type)) {
                con.append(" AND art.ASSIGNEE_ LIKE #{approver} ");
                approver = approver + "_";
            } else if (TaskListEnum.CLAIM.type.equals(type)) {
                con.append(" AND trt.STATUS=" + TaskStatusEnum.BEFORESIGN.status);
                con.append(" AND (");
                con.append(" (trt.APPROVER_TYPE=" + AssignType.DEPARTMENT.code + " AND trt.APPROVER = #{departmentId}) ");
                con.append(" OR (trt.APPROVER_TYPE =" + AssignType.ROLE.code + " AND trt.APPROVER = #{roleId}) ");
                con.append(" OR (trt.APPROVER_TYPE =" + AssignType.GROUP.code + " AND trt.APPROVER = #{groupId}) ");
                con.append(" OR (trt.APPROVER_TYPE =" + AssignType.PERSON.code + " AND trt.APPROVER = #{approver}) ");
                con.append(")");
            } else if (TaskListEnum.ACTIVE.type.equals(type)) {
                con.append(" AND trt.STATUS IN (" + TaskStatusEnum.BEFORESIGN.status + "," + TaskStatusEnum.OPEN.status + ") ");
                con.append(" AND (");
                con.append(" (trt.APPROVER_TYPE=" + AssignType.DEPARTMENT.code + " AND trt.APPROVER = #{departmentId}) ");
                con.append(" OR (trt.APPROVER_TYPE =" + AssignType.ROLE.code + " AND trt.APPROVER = #{roleId}) ");
                con.append(" OR (trt.APPROVER_TYPE =" + AssignType.GROUP.code + " AND trt.APPROVER = #{groupId}) ");
                con.append(" OR (trt.APPROVER = #{approver}) ");
                con.append(")");
            } else {
                con.append(" AND trt.STATUS=" + TaskStatusEnum.OPEN.status);
                con.append(" AND trt.APPROVER_REAL LIKE #{approver} ");
            }
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPageNum(), taskQueryParam.getPageSize());
        String sql = sb.toString() + con.toString();
        if (TaskListEnum.CLOSE.type.equals(type)) {
            NativeHistoricTaskInstanceQuery query = historyService.createNativeHistoricTaskInstanceQuery().sql(re + sql)
                    .parameter("appKey", taskQueryParam.getAppKey())
                    .parameter("title", "%" + taskQueryParam.getTitle() + "%")
                    .parameter("creater", taskQueryParam.getCreater())
                    .parameter("taskName", "%" + taskQueryParam.getTaskName() + "%")
                    .parameter("approver", "%" + approver + "%")
                    .parameter("departmentId", departmentId)
                    .parameter("roleId", roleId)
                    .parameter("groupId", groupId);
            List<HistoricTaskInstance> tasks = query.sql(re + sql).listPage(pageInfo.getFrom(), pageInfo.getSize());
            pageInfo.setTotal((int) query.sql(reC + sql).count());
            pageInfo.setRows(tasks);
        } else {
            NativeTaskQuery query = taskService.createNativeTaskQuery()
                    .parameter("appKey", taskQueryParam.getAppKey())
                    .parameter("title", "%" + taskQueryParam.getTitle() + "%")
                    .parameter("creater", taskQueryParam.getCreater())
                    .parameter("taskName", "%" + taskQueryParam.getTaskName() + "%")
                    .parameter("approver", approver)
                    .parameter("departmentId", departmentId)
                    .parameter("roleId", roleId)
                    .parameter("groupId", groupId);
            List<Task> tasks = query.sql(re + sql).listPage(pageInfo.getFrom(), pageInfo.getSize());
            pageInfo.setRows(tasks);
            pageInfo.setTotal((int) query.sql(reC + sql).count());
            pageInfo.setRows(tasks);
        }

        return pageInfo;
    }
}