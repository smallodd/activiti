package com.hengtian.flow.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.common.common.CodeConts;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hengtian.application.model.AppModel;
import com.hengtian.application.service.AppModelService;
import com.hengtian.common.enums.AssignTypeEnum;
import com.hengtian.common.enums.CommonEnum;
import com.hengtian.common.enums.ExprEnum;
import com.hengtian.common.enums.ProcessStatusEnum;
import com.hengtian.common.enums.ResultEnum;
import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.enums.TaskListEnum;
import com.hengtian.common.enums.TaskStatusEnum;
import com.hengtian.common.enums.TaskTypeEnum;
import com.hengtian.common.enums.TaskVariableEnum;
import com.hengtian.common.param.ProcessParam;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.param.TaskAgentQueryParam;
import com.hengtian.common.param.TaskParam;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import com.hengtian.common.result.TaskNodeResult;
import com.hengtian.common.utils.BeanUtils;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.workflow.cmd.CreateCmd;
import com.hengtian.common.workflow.cmd.DestoryExecutionCmd;
import com.hengtian.common.workflow.cmd.JumpCmd;
import com.hengtian.common.workflow.cmd.TaskJumpCmd;
import com.hengtian.common.workflow.exception.WorkFlowException;
import com.hengtian.flow.dao.WorkflowDao;
import com.hengtian.flow.model.AssigneeTemp;
import com.hengtian.flow.model.ProcessInstanceResult;
import com.hengtian.flow.model.RemindTask;
import com.hengtian.flow.model.RuProcinst;
import com.hengtian.flow.model.TAskTask;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.model.TTaskNotice;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.model.TWorkDetail;
import com.hengtian.flow.model.TaskAgent;
import com.hengtian.flow.model.TaskResult;
import com.hengtian.flow.service.AssigneeTempService;
import com.hengtian.flow.service.RemindTaskService;
import com.hengtian.flow.service.RuProcinstService;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.TRuTaskService;
import com.hengtian.flow.service.TTaskNoticeService;
import com.hengtian.flow.service.TUserTaskService;
import com.hengtian.flow.service.TWorkDetailService;
import com.hengtian.flow.service.TaskAgentService;
import com.hengtian.flow.service.WorkflowService;
import com.hengtian.flow.vo.AskCommentDetailVo;
import com.hengtian.flow.vo.AssigneeVo;
import com.hengtian.flow.vo.ProcessDefinitionVo;
import com.hengtian.flow.vo.TaskNodeVo;
import com.hengtian.flow.vo.TaskVo;
import com.rbac.dubbo.RbacDomainContext;
import com.rbac.entity.RbacPrivilege;
import com.rbac.entity.RbacRole;
import com.rbac.entity.RbacUser;
import com.rbac.service.PrivilegeService;
import com.user.entity.emp.Emp;
import com.user.entity.emp.EmpVO;
import com.user.service.emp.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.NativeHistoricTaskInstanceQuery;
import org.activiti.engine.impl.juel.Builder;
import org.activiti.engine.impl.juel.IdentifierNode;
import org.activiti.engine.impl.juel.Tree;
import org.activiti.engine.impl.juel.TreeBuilder;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.NativeProcessDefinitionQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class WorkflowServiceImpl extends ActivitiUtilServiceImpl implements WorkflowService {

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
    private RuProcinstService ruProcinstService;

    @Autowired
    private TUserTaskService tUserTaskService;

    @Autowired
    private TRuTaskService tRuTaskService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    TWorkDetailService workDetailService;

    @Reference(loadbalance = "rbac")
    private PrivilegeService privilegeService;

    @Autowired
    private WorkflowDao workflowDao;

    @Reference(registry = "chtwm")
    private EmpService empService;

    @Autowired
    private AssigneeTempService assigneeTempService;

    @Autowired
    private TaskAgentService taskAgentService;

    @Autowired
    private TTaskNoticeService tTaskNoticeService;
    @Value("${rbac.key}")
    String rbacKey;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result startProcessInstance(ProcessParam processParam) {
        log.info("创建任务开始入参：{}",JSONObject.toJSONString(processParam));
        Result result = new Result();
        String jsonVariables = processParam.getJsonVariables();
        Map<String, Object> variables = new HashMap<>();
        if (StringUtils.isNotBlank(jsonVariables)) {
            variables = JSON.parseObject(jsonVariables);
        }
        //查询业务系统与模型之间是否存在关联关系
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
        boolean isInFlow = checkBusinessKeyIsInFlow(processParam.getProcessDefinitionKey(), processParam.getBusinessKey(), processParam.getAppKey());

        if (isInFlow) {
            log.info("业务主键【{}】已经提交过任务", processParam.getBusinessKey());
            //已经创建过则返回错误信息
            result.setSuccess(false);
            result.setMsg("此条信息已经提交过任务");
            result.setCode(Constant.BUSSINESSKEY_EXIST);
            return result;
        } else {
            //查询流程定义对象
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processParam.getProcessDefinitionKey()).latestVersion().singleResult();
            if(!processParam.isCustomApprover()){

                    EntityWrapper entityWrapper = new EntityWrapper();
                    entityWrapper.where("proc_def_key={0}", processParam.getProcessDefinitionKey()).andNew("version_={0}", processDefinition.getVersion()).isNull("candidate_ids");
                    //查询当前任务任务节点信息
                    TUserTask query=new TUserTask();
                    query.setProcDefKey(processParam.getProcessDefinitionKey());
                    query.setVersion(processDefinition.getVersion());
                    long count = tUserTaskService.selectNotSetAssign(query);
                    if(count>0){
                        log.info("{}有存在未设置审批人的节点",processParam.getProcessDefinitionKey());
                        result.setCode(Constant.TASK_NOT_SET_APPROVER);
                        result.setMsg("启动流程失败：存在未设置审批人的节点");
                        result.setSuccess(false);
                        return result;
                    }
            }

            String creator = processParam.getCreatorId();
            variables.put(ConstantUtils.SET_ASSIGNEE_FLAG, processParam.isCustomApprover());
            variables.put("appKey", processParam.getAppKey());
            identityService.setAuthenticatedUserId(creator);
            //生成任务
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processParam.getProcessDefinitionKey(), processParam.getBusinessKey(), variables);

            //给对应实例生成标题
            runtimeService.setProcessInstanceName(processInstance.getId(), processParam.getTitle());
            log.info("生成任务成功,生成任务procId为{}",processInstance.getProcessInstanceId());

            //查询创建完任务之后生成的任务信息
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();

            String taskId = "";
            String assigee="";
            if (!processParam.isCustomApprover()) {
                log.info("工作流平台设置审批人");

                for (int i = 0; i < taskList.size(); i++) {
                    Task task = taskList.get(i);
                    taskId += task.getId();
                    EntityWrapper entityWrapper = new EntityWrapper();
                    entityWrapper.where("proc_def_key={0}", processParam.getProcessDefinitionKey()).andNew("task_def_key={0}", task.getTaskDefinitionKey()).andNew("version_={0}", processDefinition.getVersion());
                    //查询当前任务任务节点信息
                    TUserTask tUserTask = tUserTaskService.selectOne(entityWrapper);
                    if(tUserTask == null){
                        log.info("设置审批人异常");
                        throw new WorkFlowException("启动流程失败：未设置审批人");
                    }
                    //将流程创建人暂存到expr字段
                    tUserTask.setExpr(creator);
                    boolean flag = setAssignee(task, tUserTask);
                    if(!flag){
                        log.info("设置审批人失败!");
                        throw new WorkFlowException("设置审批人异常");
                    }
                    //如果创建任务后只有一条任务，则记录审批人字段
                    if(taskList.size()==1){
                        assigee=tUserTask.getCandidateIds();
                    }

                }
                result.setSuccess(true);
                result.setCode(Constant.SUCCESS);
                result.setMsg("申请成功");

                //存储操作记录
                TWorkDetail tWorkDetail = new TWorkDetail();
                tWorkDetail.setCreateTime(new Date());
                tWorkDetail.setDetail("工号【" + processParam.getCreatorId() + "】开启了" + processParam.getTitle() + "任务");
                tWorkDetail.setProcessInstanceId(processInstance.getProcessInstanceId());
                tWorkDetail.setOperator(processParam.getCreatorId());
                tWorkDetail.setTaskId(taskId);
                tWorkDetail.setBusinessKey(processInstance.getBusinessKey());
                tWorkDetail.setAprroveInfo("生成任务");
                List<HistoricTaskInstance> historicTaskInstances=historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstance.getProcessInstanceId()).orderByTaskCreateTime().desc().list();
                tWorkDetail.setOperateAction("提交");
                tWorkDetail.setOperTaskKey(historicTaskInstances.get(0).getName());
                workDetailService.insert(tWorkDetail);


                //如果第一个节点是申请人，则审批自动通过
                if (ExprEnum.CREATOR.expr.equals(assigee)) {
                    Task taskApprove = taskService.createTaskQuery().taskId(taskId).singleResult();
                    TaskParam taskParam = new TaskParam();
                    taskParam.setTaskId(taskApprove.getId());
                    taskParam.setPass(1);
                    taskParam.setComment("通过");
                    taskParam.setAssignee(creator);
                    approveTask(taskApprove, taskParam);
                }

            } else {
                for (int i = 0; i < taskList.size(); i++) {
                    taskId+=taskList.get(0).getId();
                }
                log.info("业务平台设置审批人");

                result.setSuccess(true);
                result.setCode(Constant.SUCCESS);
                result.setMsg("申请成功");


                TWorkDetail tWorkDetail = new TWorkDetail();
                tWorkDetail.setCreateTime(new Date());
                tWorkDetail.setDetail("工号【" + processParam.getCreatorId() + "】开启了" + processParam.getTitle() + "任务");
                tWorkDetail.setProcessInstanceId(processInstance.getProcessInstanceId());
                tWorkDetail.setOperator(processParam.getCreatorId());
                tWorkDetail.setTaskId(taskId);
                tWorkDetail.setBusinessKey(processInstance.getBusinessKey());

                tWorkDetail.setAprroveInfo("生成任务");
                List<HistoricTaskInstance> historicTaskInstances=historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstance.getProcessInstanceId()).orderByTaskCreateTime().desc().list();
                tWorkDetail.setOperateAction("提交");
                tWorkDetail.setOperTaskKey(historicTaskInstances.get(0).getName());
                workDetailService.insert(tWorkDetail);
            }

            List<Task> list=taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list();
            result.setObj(setButtons(TaskNodeResult.toTaskNodeResultList(list)));

            //添加应用-流程实例对应关系 t_ru_procinst表
            String creatorDeptName = "";
            String creatorDeptCode = "";
            String userName = "";
            Emp user=empService.selectByCode(creator);

            if(user != null){
                userName = user.getName();
            }
            if(StringUtils.isNotBlank(processParam.getDeptCode())) {
               creatorDeptName=processParam.getDeptName();
               creatorDeptCode=processParam.getDeptCode();
            }else if(user!=null){
                userName = user.getName();
                creatorDeptName = user.getDeptName();
                creatorDeptCode = user.getDeptCode();
            }

            String currentTaskKey = null;
            for(Task t : list){
                currentTaskKey = currentTaskKey == null?t.getTaskDefinitionKey():currentTaskKey+","+t.getTaskDefinitionKey();
            }
            RuProcinst ruProcinst = new RuProcinst(processParam.getAppKey(), processInstance.getProcessInstanceId(), creator, userName, creatorDeptCode, creatorDeptName, processDefinition.getKey(), processDefinition.getName(), currentTaskKey, processParam.getBusinessKey());
            ruProcinstService.insert(ruProcinst);


        }
        log.info("生成任务接口调用成功，出参：{}",JSONObject.toJSONString(result));
        return result;
    }

    /**
     * 设置审批人接口
     *
     * @param task
     * @param tUserTask
     */
    @Override
    public Boolean setAssignee(Task task, TUserTask tUserTask) {
        return setAssignee(task, tUserTask,false, null);
    }

    /**
     * 设置审批人接口
     *
     * @param task
     * @param tUserTask
     */
    public Boolean setAssignee(Task task, TUserTask tUserTask, boolean needSetNext, Map<String, AssigneeTemp> assigneeTempMap) {
        log.info("进入设置审批人接口,tUserTask参数{}", JSONObject.toJSONString(tUserTask));
        //获取任务中的自定义参数
        Integer appKey = (Integer)taskService.getVariable(task.getId(), "appKey");
        boolean customApprover = (boolean) runtimeService.getVariable(task.getProcessInstanceId(), ConstantUtils.SET_ASSIGNEE_FLAG);
        List<TRuTask> ruTaskList = Lists.newArrayList();
        if(customApprover){
            //应用系统端设置审批人
            String assignee = tUserTask.getCandidateIds();
            if(StringUtils.isBlank(assignee)){
                log.info("审批人信息为空");
                throw new WorkFlowException("审批人信息为空");
            }
            String[] assigneeArray = assignee.split(",");

            for(String userId : assigneeArray){
                TRuTask tRuTask = new TRuTask();
                tRuTask.setTaskId(task.getId());
                tRuTask.setAssignee(userId);
                tRuTask.setAssigneeName(getUserName(userId));
                tRuTask.setAssigneeReal(userId);
                tRuTask.setExpireTime(task.getDueDate());
                tRuTask.setAppKey(appKey);
                tRuTask.setProcInstId(task.getProcessInstanceId());
                tRuTask.setTaskDefKey(task.getTaskDefinitionKey());
                tRuTask.setTaskDefName(task.getName());
                if(userId.startsWith("H")) {
                    tRuTask.setAssigneeType(AssignTypeEnum.PERSON.code);
                }else{
                    tRuTask.setAssigneeType(AssignTypeEnum.ROLE.code);
                }
                tRuTask.setOwner(task.getOwner());
                tRuTask.setTaskType(TaskTypeEnum.ASSIGNEE.value);

                ruTaskList.add(tRuTask);
            }
        }else{
            //根据系统配置设置审批人
            String assignee = null;
            //生成扩展任务信息
            if(needSetNext && assigneeTempMap != null && assigneeTempMap.size() > 0){
                log.info("手动设置审批人，前端传来参数：{}",JSONObject.toJSONString(assigneeTempMap));
                //需手动设置审批人，不从流程配置表中设置
                Set<String> keySet = assigneeTempMap.keySet();
                for(String key : keySet){
                    AssigneeTemp assigneeTemp = assigneeTempMap.get(key);
                    TRuTask tRuTask = new TRuTask();
                    tRuTask.setTaskId(task.getId());
                    tRuTask.setAssignee(assigneeTemp.getRoleCode());
                    tRuTask.setAssigneeName(assigneeTemp.getRoleName());
                    tRuTask.setAssigneeReal(assigneeTemp.getAssigneeCode());
                    tRuTask.setExpireTime(task.getDueDate());
                    tRuTask.setAppKey(appKey);
                    tRuTask.setProcInstId(task.getProcessInstanceId());
                    tRuTask.setTaskDefKey(task.getTaskDefinitionKey());
                    tRuTask.setTaskDefName(task.getName());
                    //tRuTask.setAssigneeType(tUserTask.getAssignType());
                    //指派的任务，assigneeType由角色改为人员
                    if(assigneeTemp.getRoleCode().startsWith("H")) {
                        tRuTask.setAssigneeType(AssignTypeEnum.PERSON.code);
                    }else{
                        tRuTask.setAssigneeType(AssignTypeEnum.ROLE.code);
                    }
                    tRuTask.setOwner(task.getOwner());
                    tRuTask.setTaskType(tUserTask.getTaskType());
                    ruTaskList.add(tRuTask);
                }
            }else {
                log.info("通过工作流平台设置审批人");
                String assignees = tUserTask.getCandidateIds();
                String[] assigneeArray = assignees.split(",");

                for (int i=0;i<assigneeArray.length;i++) {
                    assignee = assigneeArray[i];
                    TRuTask tRuTask = new TRuTask();
                    tRuTask.setTaskId(task.getId());
                    tRuTask.setAssignee(assignee);
                    tRuTask.setAssigneeName(getUserName(assigneeArray[i]));
                    EntityWrapper entityWrapper = new EntityWrapper();
                    entityWrapper.where("task_id={0}", task.getId()).andNew("assignee={0}", assignee);
                    TRuTask tRu = tRuTaskService.selectOne(entityWrapper);
                    if (tRu != null) {
                        continue;
                    }
                    tRuTask.setAssigneeType(tUserTask.getAssignType());
                    tRuTask.setOwner(task.getOwner());

                    tRuTask.setTaskType(tUserTask.getTaskType());

                    //判断如果是非人员审批中的认领任务，需要认领之后才能审批
                    if (AssignTypeEnum.ROLE.code.intValue() == tUserTask.getAssignType().intValue()) {
                        tRuTask.setStatus(-1);
                    } else if(AssignTypeEnum.EXPR.code.intValue() == tUserTask.getAssignType().intValue()){
                        //表达式
                        List<Emp> empLeader = Lists.newArrayList();
                        String assigneeReal = null;

                        if(ExprEnum.LEADER.expr.equals(assignee)){
                            //上级节点领导
                            List<String> beforeTaskDefKeys = findBeforeTaskDefKeys(task, false);
                            if(CollectionUtils.isNotEmpty(beforeTaskDefKeys)){
                                for(String taskDefKey : beforeTaskDefKeys){
                                    log.info("查询信息历史节点开始，{}，{}",task.getProcessInstanceId(),taskDefKey);
                                    List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(taskDefKey).list();
                                    if(historicTaskInstances.size()==0||StringUtils.isBlank(historicTaskInstances.get(0).getAssignee())){
                                        continue;
                                    }
                                    HistoricTaskInstance historicTaskInstance = historicTaskInstances.get(0);
                                    log.info("获取信息为：{}",historicTaskInstance.getAssignee());
                                    String str = historicTaskInstance.getAssignee().replaceAll("_Y","").replaceAll("_N","");
                                    for(String a : str.split(",")){
                                        List<Emp> emps = empService.selectDirectSupervisorByCode(a);
                                        if(CollectionUtils.isNotEmpty(emps)){
                                            empLeader.addAll(emps);
                                        }
                                    }
                                }
                            }
                        }else if(ExprEnum.LEADER_CREATOR.expr.equals(assignee)){
                            //流程创建人领导
                            String creator = tUserTask.getExpr();
                            if(StringUtils.isBlank(creator)){
                                creator = getProcessCreator(task.getProcessInstanceId());
                            }
                            List<Emp> emps = empService.selectDirectSupervisorByCode(creator);
                            if(CollectionUtils.isNotEmpty(emps)){
                                empLeader.addAll(emps);
                            }
                        }else if(ExprEnum.CREATOR.expr.equals(assignee)){
                            //申请人
                            String creator = tUserTask.getExpr();
                            if(StringUtils.isBlank(creator)){
                                creator = getProcessCreator(task.getProcessInstanceId());
                            }
                            Emp emp = new Emp();
                            emp.setCode(creator);
                            empLeader.add(emp);
                        }

                        if(CollectionUtils.isNotEmpty(empLeader)){
                            Set<String> assigneeSet = Sets.newHashSet();
                            for(Emp emp : empLeader){
                                assigneeSet.add(emp.getCode());
                            }
                            assigneeReal = StringUtils.join(assigneeSet.toArray(), ",");
                            tRuTask.setAssigneeReal(assigneeReal);

                            /*JSONObject approveCountJson = new JSONObject();
                            approveCountJson.put(TaskVariableEnum.APPROVE_COUNT_TOTAL.value, assigneeSet.size());
                            approveCountJson.put(TaskVariableEnum.APPROVE_COUNT_NEED.value, assigneeSet.size());
                            approveCountJson.put(TaskVariableEnum.APPROVE_COUNT_NOW.value, 0);
                            approveCountJson.put(TaskVariableEnum.APPROVE_COUNT_REFUSE.value, 0);

                            taskService.setVariableLocal(task.getId(), task.getTaskDefinitionKey()+":"+ TaskVariableEnum.APPROVE_COUNT.value,approveCountJson.toJSONString());*/
                        }else{
                            return false;
                        }
                    } else{
                        tRuTask.setStatus(0);
                        tRuTask.setAssigneeReal(assignee);
                    }
                    tRuTask.setExpireTime(task.getDueDate());
                    tRuTask.setAppKey(appKey);
                    tRuTask.setProcInstId(task.getProcessInstanceId());
                    tRuTask.setTaskDefKey(task.getTaskDefinitionKey());
                    tRuTask.setTaskDefName(task.getName());
                    ruTaskList.add(tRuTask);
                }
            }
        }

        //TODO 创建审批任务，给task设置审批人tUserTask
//        final ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
//        processInstance.getBusinessKey();

        if(CollectionUtils.isNotEmpty(ruTaskList)){
            log.info("审批插入t_ru_task数据：{}", JSONObject.toJSONString(ruTaskList));
            //TODO 循环ruTaskList  推送审批任务
            if (!ExprEnum.CREATOR.expr.equals(tUserTask.getCandidateIds())) {
                List<TTaskNotice> tTaskNoticeList = new ArrayList<>();
                for(TRuTask tRuTask:ruTaskList){

                    TTaskNotice tTaskNotice=new TTaskNotice();
                    if(tRuTask.getAssignee().startsWith("H")){
                        tTaskNotice.setUserType(AssignTypeEnum.PERSON.code);
                    }else if(tRuTask.getAssignee().equals(ExprEnum.LEADER.expr)||tRuTask.getAssignee().equals(ExprEnum.LEADER_CREATOR.expr)){
                        String assigneeReal = tRuTask.getAssigneeReal();
                        String[] split = assigneeReal.split(",");
                        for(String emp:split) {
                            tTaskNotice=new TTaskNotice();
                            tTaskNotice.setUserType(AssignTypeEnum.PERSON.code);
                            tTaskNotice.setEmpNo(emp);
                            tTaskNotice.setAppKey(tRuTask.getAppKey());
                            tTaskNotice.setTaskId(tRuTask.getTaskId());
                            tTaskNotice.setProcInstId(tRuTask.getProcInstId());
                            tTaskNotice.setCreateId(emp);
                            tTaskNotice.setCreateTime(new Date());
                            tTaskNotice.setUpdateId(emp);
                            tTaskNotice.setUpdateTime(new Date());
                            tTaskNotice.setState(0);
                            tTaskNotice.setEmpName(tRuTask.getAssigneeName());
                            tTaskNotice.setUpdateName(tRuTask.getAssigneeName());
                            tTaskNotice.setCreateName(tRuTask.getAssigneeName());
                            tTaskNotice.setType(0);
                            tTaskNoticeList.add(tTaskNotice);
                        }
                        continue;
                    }else{
                        tTaskNotice.setUserType(AssignTypeEnum.ROLE.code);
                    }
                    tTaskNotice.setAppKey(tRuTask.getAppKey());
                    tTaskNotice.setTaskId(tRuTask.getTaskId());
                    tTaskNotice.setProcInstId(tRuTask.getProcInstId());
                    tTaskNotice.setCreateId(tRuTask.getAssignee());
                    tTaskNotice.setCreateTime(new Date());
                    tTaskNotice.setUpdateId(tRuTask.getAssignee());
                    tTaskNotice.setUpdateTime(new Date());
                    tTaskNotice.setState(0);
                    tTaskNotice.setEmpNo(tRuTask.getAssignee());
                    tTaskNotice.setEmpName(tRuTask.getAssigneeName());
                    tTaskNotice.setUpdateName(tRuTask.getAssigneeName());
                    tTaskNotice.setCreateName(tRuTask.getAssigneeName());
                    tTaskNotice.setType(0);

                    tTaskNoticeList.add(tTaskNotice);

                }
                if (tTaskNoticeList.size()>0) tTaskNoticeService.insertBatch(tTaskNoticeList);

            }
            return tRuTaskService.insertBatch(ruTaskList);
        }


        log.info("设置审批人结束");
        return true;
    }


    /**
     * 审批任务
     *
     * @param task
     * @param taskParam
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object approveTask(Task task, TaskParam taskParam){
        //现阶段只开放影响分支的跳转
        taskParam.setJumpType(1);
        log.info("审批接口进入，传入参数taskParam{}", JSONObject.toJSONString(taskParam));
        Result result = new Result();
        if(StringUtils.isBlank(taskParam.getAssignee())){
            log.info("审批人参数不合法！");
            result.setMsg("审批人不合法");
            result.setCode(Constant.PARAM_ERROR);
            result.setSuccess(false);
            return result;
        }

        result.setCode(Constant.SUCCESS);

        EntityWrapper<TRuTask> entityWrapper = new EntityWrapper();
        entityWrapper.where("task_id={0}", task.getId());
        List<TRuTask> tRuTasks = tRuTaskService.selectList(entityWrapper);

        //审批人信息集合
        Set<String> assigneeSet = Sets.newLinkedHashSet();
        assigneeSet.add(taskParam.getAssignee());
        //检验代理人信息
        if(StringUtils.isNotBlank(taskParam.getAssigneeAgent())){
            if(StringUtils.isNotBlank(taskParam.getAssigneeAgentSecret())){
                if(!getAssigneeSecret(taskParam.getAssignee(), taskParam.getAssigneeAgent()).equals(taskParam.getAssigneeAgentSecret())){
                    return new Result("【"+taskParam.getAssignee()+"】没有权限代理【"+taskParam.getAssigneeAgent()+"】审批任务【"+task.getId()+"】");
                }
            }else{
                return new Result("【"+taskParam.getAssignee()+"】没有权限代理【"+taskParam.getAssigneeAgent()+"】审批任务【"+task.getId()+"】");
            }
            assigneeSet.addAll(Arrays.asList(taskParam.getAssigneeAgent().split(",")));
        }
        TRuTask ruTask = validateTaskAssignee(task, assigneeSet, tRuTasks);
        if(ruTask == null){
            log.info("{}没有操作任务{}的权限",assigneeSet,task.getId());
            result.setMsg("该用户没有操作此任务的权限");
            result.setCode(Constant.TASK_NOT_BELONG_USER);
            result.setSuccess(false);
            return result;
        }

        if(task.getAssignee() != null && task.getAssignee().indexOf(taskParam.getAssignee()) >= 0){
            result.setMsg("用户【"+taskParam.getAssignee()+"】已审批，不可重复操作");
            result.setCode(Constant.BUSSINESSKEY_EXIST);
            result.setSuccess(false);
            return result;
        }

        //查询流程定义信息
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(task.getProcessDefinitionId());

        String jsonVariables = taskParam.getJsonVariables();
        Map<String, Object> variables = Maps.newHashMap();
        if (StringUtils.isNotBlank(jsonVariables)) {
            variables = JSON.parseObject(jsonVariables);
        }

        //参数校验
        result = validateApproveParam(ruTask, taskParam);
        if(!result.isSuccess()){
            log.info("参数校验失败，{}",JSONObject.toJSONString(result));
            return result;
        }

        //获取任务参数
        Map map = taskService.getVariables(task.getId());
        map.putAll(variables);

        //查询审批时该节点的执行execution
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.where("task_def_key={0}", task.getTaskDefinitionKey()).andNew("version_={0}", processDefinition.getVersion()).andNew("proc_def_key={0}", processDefinition.getKey());

        TUserTask tUserTask = tUserTaskService.selectOne(wrapper);

        //设置下步审批人
        Map<String, Map<String,AssigneeTemp>> assigneeMap = Maps.newHashMap();
        if(CommonEnum.OTHER.value.equals(tUserTask.getNeedSetNext()) && taskParam.getPass() == TaskStatusEnum.COMPLETE_AGREE.status){
            result = setNextAssigneeTemp(task, processDefinition.getKey(), taskParam.getAssigneeNext(), task.getProcessInstanceId(), taskParam.getAssignee(), tUserTask.getTaskDefKey(), processDefinition.getVersion(), assigneeMap);
            if(!result.isSuccess()){
                log.info("设置下一审批人失败，{}",JSONObject.toJSONString(result));
                return result;
            }
        }
        EntityWrapper wrapper1 = new EntityWrapper();
        TTaskNotice tTaskNotice=new TTaskNotice();
        if(tUserTask.getAssignType().equals(3)){

            tTaskNotice.setAction(taskParam.getPass()==3?1:taskParam.getPass());

            wrapper1.where("task_id={0}",task.getId()).andNew("emp_no={0}",taskParam.getAssignee());

        }else{
            tTaskNotice.setAction(taskParam.getPass()==3?1:taskParam.getPass());
            wrapper1.where("task_id={0}",task.getId()).andNew("emp_no={0}",tUserTask.getCandidateIds());
        }
        tTaskNoticeService.update(tTaskNotice,wrapper1);
        identityService.setAuthenticatedUserId(taskParam.getAssignee());
        taskService.addComment(taskParam.getTaskId(), task.getProcessInstanceId(),taskParam.getPass()+"", taskParam.getComment());
        taskService.setVariables(task.getId(), map);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        //设置操作的明细备注
        TWorkDetail tWorkDetail = new TWorkDetail();
        tWorkDetail.setTaskId(task.getId());
        tWorkDetail.setOperator(taskParam.getAssignee());
        tWorkDetail.setProcessInstanceId(task.getProcessInstanceId());
        tWorkDetail.setBusinessKey(processInstance.getBusinessKey());
        tWorkDetail.setCreateTime(new Date());
        tWorkDetail.setDetail("工号【" + taskParam.getAssignee() + "】审批了该任务，审批意见是【" + taskParam.getComment() + "】");

        tWorkDetail.setAprroveInfo(taskParam.getComment());
        List<HistoricTaskInstance> historicTaskInstances=historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).orderByTaskCreateTime().desc().list();

        tWorkDetail.setOperTaskKey(historicTaskInstances.get(0).getName());

        boolean customApprover = (boolean) runtimeService.getVariable(task.getProcessInstanceId(), ConstantUtils.SET_ASSIGNEE_FLAG);

        //如果是代理人审批，添加记录
        if(StringUtils.isNotBlank(taskParam.getAssigneeAgent()) && !assigneeSet.contains(taskParam.getAssignee())){
            List<TaskAgent> taskAgentList = Lists.newArrayList();
            for(String assigneeAgent : assigneeSet){
                TaskAgent taskAgent = new TaskAgent();
                taskAgent.setId(null);
                taskAgent.setAgentType(2);
                taskAgent.setAssignee(assigneeAgent);
                taskAgent.setAssigneeAgent(taskParam.getAssignee());
                taskAgent.setTaskId(task.getId());
                taskAgent.setCreateTime(new Date());

                taskAgentList.add(taskAgent);
            }
            taskAgentService.insertBatch(taskAgentList);
        }

        if (TaskTypeEnum.COUNTERSIGN.value.equals(tUserTask.getTaskType()) || AssignTypeEnum.EXPR.code.equals(tUserTask.getAssignType())) {
            //会签,表达式
            JSONObject approveCountJson = new JSONObject();
            String approveCount = (String)map.get(task.getTaskDefinitionKey()+":"+ TaskVariableEnum.APPROVE_COUNT.value);
            if(StringUtils.isBlank(approveCount)){
                approveCountJson.put(TaskVariableEnum.APPROVE_COUNT_TOTAL.value, tUserTask.getUserCountTotal());
                approveCountJson.put(TaskVariableEnum.APPROVE_COUNT_NEED.value, tUserTask.getUserCountNeed());
                approveCountJson.put(TaskVariableEnum.APPROVE_COUNT_NOW.value, 0);
                approveCountJson.put(TaskVariableEnum.APPROVE_COUNT_REFUSE.value, 0);
            }else {
                approveCountJson = JSONObject.parseObject(approveCount);
            }

            int approveCountNow = approveCountJson.getInteger(TaskVariableEnum.APPROVE_COUNT_NOW.value);
            int approveCountTotal = approveCountJson.getInteger(TaskVariableEnum.APPROVE_COUNT_TOTAL.value);
            int approveCountNeed = approveCountJson.getInteger(TaskVariableEnum.APPROVE_COUNT_NEED.value);
            int approveCountRefuse = approveCountJson.getInteger(TaskVariableEnum.APPROVE_COUNT_REFUSE.value);

            if(taskParam.getPass() == TaskStatusEnum.COMPLETE_REFUSE.status){
                approveCountRefuse ++;
            }

            if(assigneeSet.size() + approveCountNow - approveCountRefuse < approveCountNeed){
                approveCountNow = assigneeSet.size() + approveCountNow;
            }else{
                assigneeSet = ImmutableSet.copyOf(Iterables.limit(assigneeSet, approveCountNeed + approveCountRefuse - approveCountNow));
                approveCountNow = approveCountNeed + approveCountRefuse;
            }

            approveCountJson.put(TaskVariableEnum.APPROVE_COUNT_NOW.value,approveCountNow);
            approveCountJson.put(TaskVariableEnum.APPROVE_COUNT_REFUSE.value,approveCountRefuse);
            taskService.setVariableLocal(task.getId(), task.getTaskDefinitionKey()+":"+ TaskVariableEnum.APPROVE_COUNT.value,approveCountJson.toJSONString());

            int approveCountAgree = approveCountNow - approveCountRefuse;
            if(approveCountAgree >= approveCountNeed){
                //------------任务完成-通过------------
                String assignee = task.getAssignee();
                String assignee_ = null;
                for(String a : assigneeSet){
                    assignee_ = (assignee_==null?"":assignee_+",")+a+"_Y";
                }
                taskService.setAssignee(task.getId(),StringUtils.isBlank(assignee)?(assignee_):(assignee+","+assignee_));
                taskService.complete(task.getId(), map);
                if(AssignTypeEnum.PERSON.code.equals(ruTask.getAssigneeType())){
                    //人员审批
                    TRuTask tRuTask = new TRuTask();
                    tRuTask.setStatus(TaskStatusEnum.SKIP.status);
                    EntityWrapper wrapper_ = new EntityWrapper();
                    wrapper_.where("task_id={0}", task.getId()).andNew("status={0}", TaskStatusEnum.OPEN.status);
                    tRuTaskService.update(tRuTask, wrapper_);

                    wrapper_ = new EntityWrapper();
                    wrapper_.where("task_id={0}", task.getId()).andNew("assignee_real={0}", taskParam.getAssignee());
                    tRuTask.setStatus(TaskStatusEnum.AGREE.status);
                    tRuTaskService.update(tRuTask, wrapper_);

                    result = new Result(true,Constant.SUCCESS, "通过成功");
                }

                tWorkDetail.setOperateAction("审批同意");
                tWorkDetail.setDetail("工号【" + taskParam.getAssignee() + "】通过了该任务，审批意见是【" + taskParam.getComment() + "】");
                workDetailService.insert(tWorkDetail);
                try {
                    EntityWrapper wrapper_ = new EntityWrapper();
                    wrapper_.where("task_id={0}", task.getId());
                    TTaskNotice tTaskNotice1 = new TTaskNotice();
                    tTaskNotice1.setAction(1);
                    tTaskNotice1.setMessage(taskParam.getComment());
                    tTaskNoticeService.update(tTaskNotice1, wrapper_);
                }catch (Exception e){
                    log.warn("更新消息推送表失败",e);
                }

            }else{
                if(approveCountTotal - approveCountNow + approveCountAgree < approveCountNeed){
                    //------------任务完成-未通过------------
                    String assignee = task.getAssignee();
                    String assignee_ = null;
                    for(String a : assigneeSet){
                        assignee_ = (assignee_==null?"":assignee_+",")+a+"_N";
                    }
                    taskService.setAssignee(task.getId(),StringUtils.isBlank(assignee)?(assignee_):(assignee+","+assignee_));
                    deleteProcessInstance(task.getProcessInstanceId(), "refused");
                    if(AssignTypeEnum.PERSON.code.equals(ruTask.getAssigneeType())){
                        TRuTask tRuTask = new TRuTask();
                        tRuTask.setStatus(TaskStatusEnum.SKIP.status);
                        EntityWrapper wrapper_ = new EntityWrapper();
                        wrapper_.where("task_id={0}", task.getId()).andNew("status={0}", TaskStatusEnum.OPEN.status);
                        tRuTaskService.update(tRuTask, wrapper_);

                        wrapper_ = new EntityWrapper();
                        wrapper_.where("task_id={0}", task.getId()).andNew("assignee_real={0}", taskParam.getAssignee());
                        tRuTask.setStatus(TaskStatusEnum.REFUSE.status);
                        tRuTaskService.update(tRuTask, wrapper_);
                    }
                    tWorkDetail.setDetail("工号【" + taskParam.getAssignee() + "】拒绝了该任务，审批意见是【" + taskParam.getComment() + "】");
                    tWorkDetail.setOperateAction("审批拒绝");
                    workDetailService.insert(tWorkDetail);

                    // 新需求  HTXQ2020-79
                    updateAskTask(task);
                    try {
                        EntityWrapper wrapper_ = new EntityWrapper();
                        wrapper_.where("task_id={0}", task.getId());
                        TTaskNotice tTaskNotice1 = new TTaskNotice();
                        tTaskNotice1.setAction(2);
                        tTaskNotice1.setMessage(taskParam.getComment());
                        tTaskNoticeService.update(tTaskNotice1, wrapper_);
                    }catch (Exception e){
                        log.warn("更新消息推送表失败",e);
                    }
                    return new Result(true,Constant.SUCCESS, "任务已拒绝！");
                }else{
                    //------------任务继续------------
                    String assignee = task.getAssignee();
                    String assignee_ = null;
                    for(String a : assigneeSet){
                        assignee_ = (assignee_==null?"":assignee_+",")+a+"_Y";
                    }
                    taskService.setAssignee(task.getId(),StringUtils.isBlank(assignee)?(assignee_):(assignee+","+assignee_));

                    tWorkDetail.setDetail("工号【" + taskParam.getAssignee() + "】通过了该任务【审批完成】，审批意见是【" + taskParam.getComment() + "】");
                    tWorkDetail.setOperateAction("审批");
                    workDetailService.insert(tWorkDetail);
                    Result res=new Result(true, Constant.SUCCESS,"办理成功！");
                    List<Task> l=new ArrayList();
                    l.add(task);
                    res.setObj(setButtons(TaskNodeResult.toTaskNodeResultList(l)));
                    try {
                        EntityWrapper wrapper_ = new EntityWrapper();
                        wrapper_.where("task_id={0}", task.getId()).andNew("emp_no={0}",taskParam.getAssignee());
                        TTaskNotice tTaskNotice1 = new TTaskNotice();
                        tTaskNotice1.setAction(1);
                        tTaskNotice1.setMessage(taskParam.getComment());
                        tTaskNoticeService.update(tTaskNotice1, wrapper_);
                    }catch (Exception e){
                        log.warn("更新消息推送表失败",e);
                    }
                    return res;
                }
            }
        } else if(TaskTypeEnum.CANDIDATEUSER.value.equals(tUserTask.getTaskType()) || TaskTypeEnum.ASSIGNEE.value.equals(tUserTask.getTaskType())){
            //候选人,普通审批
            if (taskParam.getPass() == TaskStatusEnum.COMPLETE_AGREE.status) {
                //设置原生工作流表哪些审批了
                String assignee_ = null;
                for(String a : assigneeSet){
                    assignee_ = (assignee_==null?"":assignee_+",")+a+"_Y";
                }
                taskService.setAssignee(task.getId(), assignee_);
                taskService.complete(task.getId(), map);

                ruTask.setStatus(TaskStatusEnum.AGREE.status);
                EntityWrapper truWrapper = new EntityWrapper();
                truWrapper.where("task_id={0}", task.getId()).andNew("assignee={0}", ruTask.getAssignee());

                tRuTaskService.update(ruTask, truWrapper);
                result.setSuccess(true);
                result.setCode(Constant.SUCCESS);
                tWorkDetail.setDetail("工号【" + taskParam.getAssignee() + "】通过了该任务【审批完成】，审批意见是【" + taskParam.getComment() + "】");
                tWorkDetail.setOperateAction("审批通过");
                workDetailService.insert(tWorkDetail);
                try {
                    EntityWrapper wrapper_ = new EntityWrapper();
                    wrapper_.where("task_id={0}", task.getId());
                    TTaskNotice tTaskNotice1 = new TTaskNotice();
                    tTaskNotice1.setAction(1);
                    tTaskNotice1.setMessage(taskParam.getComment());
                    tTaskNoticeService.update(tTaskNotice1, wrapper_);
                }catch (Exception e){
                    log.warn("更新消息推送表失败",e);
                }
            } else if (taskParam.getPass() == TaskStatusEnum.COMPLETE_REFUSE.status) {
                //拒绝任务
                String assignee_ = null;
                for(String a : assigneeSet){
                    assignee_ = (assignee_==null?"":assignee_+",")+a+"_N";
                }
                taskService.setAssignee(task.getId(), assignee_);
                deleteProcessInstance(task.getProcessInstanceId(), TaskStatusEnum.COMPLETE_REFUSE.desc);

                TRuTask tRuTask = new TRuTask();
                tRuTask.setStatus(TaskStatusEnum.REFUSE.status);
                EntityWrapper tRuWrapper = new EntityWrapper();
                tRuWrapper.where("task_id={0}", task.getId()).andNew("assignee={0}", ruTask.getAssignee());
                tRuTaskService.update(tRuTask, tRuWrapper);

                result.setMsg("拒绝成功");
                result.setCode(Constant.SUCCESS);
                result.setSuccess(true);
                result.setObj(new ArrayList<>());
                tWorkDetail.setDetail("工号【" + taskParam.getAssignee() + "】拒绝了该任务【审批完成】，审批意见是【" + taskParam.getComment() + "】");
                tWorkDetail.setOperateAction("审批拒绝");
                workDetailService.insert(tWorkDetail);
                // 新的需求 HTXQ2020-79
                updateAskTask(task);

                try {
                    EntityWrapper wrapper_ = new EntityWrapper();
                    wrapper_.where("task_id={0}", task.getId());
                    TTaskNotice tTaskNotice1 = new TTaskNotice();
                    tTaskNotice1.setAction(2);
                    tTaskNotice1.setMessage(taskParam.getComment());
                    tTaskNoticeService.update(tTaskNotice1, wrapper_);
                }catch (Exception e){
                    log.warn("更新消息推送表失败",e);
                }

                return result;
            }
        } else {
            if(!customApprover){
                //审批类型不正确
                result.setMsg("审批类型参数错误！");
                result.setCode(Constant.FAIL);
                result.setSuccess(false);

                return result;
            }
        }

        //修复异常节点
        if(1 == taskParam.getJumpType()){
            //跳转影响并行分支
            repairExceptionTaskNode(task);
        }else {
            //跳转不影响并行分支
            repairNextTaskNode(task,execution);
        }

        List<Task> resultList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        if(CollectionUtils.isEmpty(resultList)){
            finishProcessInstance(task.getProcessInstanceId(), ProcessStatusEnum.FINISHED_Y.status);
        }else{
            Set<String> taskIdSet = Sets.newHashSet();
            for(TRuTask tr : tRuTasks){
                taskIdSet.add(tr.getTaskId());
            }

            //设置审批人处理逻辑
            if (!Boolean.valueOf(map.get(ConstantUtils.SET_ASSIGNEE_FLAG).toString())) {
                //是否需要手动设置审批人
                boolean needSetNext = false;
                if(CommonEnum.OTHER.value.equals(tUserTask.getNeedSetNext())){
                    //需手动设置下步节点审批人，当前节点审批完成后，更新临时审批人状态为1
                    needSetNext = true;
                    EntityWrapper<AssigneeTemp> _wrapper = new EntityWrapper<>();
                    _wrapper.eq("proc_inst_id", task.getProcessInstanceId());
                    _wrapper.eq("task_def_key_before",tUserTask.getTaskDefKey());
                    AssigneeTemp assigneeTemp = new AssigneeTemp();
                    assigneeTemp.setDeleteFlag(1);
                    assigneeTempService.update(assigneeTemp, _wrapper);
                }

                EntityWrapper tUserWrapper;
                TUserTask ut;
                for (Task t : resultList) {
                    if(taskIdSet.contains(t.getId())){
                        continue;
                    }
                    tUserWrapper = new EntityWrapper();
                    tUserWrapper.where("proc_def_key={0}", processDefinition.getKey()).andNew("task_def_key={0}", t.getTaskDefinitionKey()).andNew("version_={0}", processDefinition.getVersion());
                    //查询当前任务节点信息
                    ut = tUserTaskService.selectOne(tUserWrapper);
                    boolean flag = setAssignee(t, ut, needSetNext, assigneeMap.get(t.getTaskDefinitionKey()));
                    if(!flag){
                        throw new WorkFlowException("设置审批人异常");
                    }
                }
            }
        }

        //设置流程实例当前任务节点KEY
        if(CollectionUtils.isNotEmpty(resultList)){
            String currentTaskKey = null;
            for(Task tk : resultList){
                currentTaskKey = currentTaskKey == null?tk.getTaskDefinitionKey():currentTaskKey+","+tk.getTaskDefinitionKey();
            }
            RuProcinst ruProcinst = new RuProcinst();
            ruProcinst.setCurrentTaskKey(currentTaskKey);
            EntityWrapper w = new EntityWrapper<>();
            w.eq("proc_inst_id", task.getProcessInstanceId());
            ruProcinstService.update(ruProcinst, w);
        }
        EntityWrapper wrapp=new EntityWrapper();
        wrapp.eq("proc_inst_id",task.getProcessInstanceId());
        RuProcinst ruProcinst=ruProcinstService.selectOne(wrapp);
        result.setObj(setButtons(TaskNodeResult.toTaskNodeResultList(resultList)));
        result.setSuccess(true);
        result.setCode(CodeConts.SUCCESS);
        if(ruProcinst==null){
            result.setEnd(false);
        }else {
            result.setEnd("1".equals(ruProcinst.getProcInstState()));
        }
        result.setMsg("任务已办理成功");
        return result;
    }
    //如果是审批拒绝任务结束的情况，需要调用此方法，更新问询的任务已
    private void updateAskTask(Task task){
        EntityWrapper askwrapper_ = new EntityWrapper();
        askwrapper_.where("proc_inst_id={0}", task.getProcessInstanceId()).andNew("is_ask_end={0}",0);
        TAskTask tAskTask=new TAskTask();
        tAskTask.setIsAskEnd(1);
        tAskTask.setAnswerComment("审批拒绝");
        tAskTaskService.update(tAskTask,askwrapper_);
    }

    /**
     * 校验审批人是否有权限审批
     * @param task 任务对象
     * @param assigneeSet 审批人工号集合
     * @param tRuTasks 节点审批信息
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/4 17:44
     */
    @Override
    public TRuTask validateTaskAssignee(Task task, Set<String> assigneeSet, List<TRuTask> tRuTasks){
        return validTaskAssignee(task, assigneeSet, tRuTasks);
    }

    /**
     * 处理由于跳转产生的异常节点任务，分两步处理：1添加缺失的节点；2删除多余节点；
     *
     * @author houjinrong@chtwm.com
     * date 2018/5/3 16:49
     */
    public void repairNextTaskNode(Task t,Execution execution) {
        EntityWrapper ew = new EntityWrapper();
        ew.where("status={0}", -2).andNew("proc_inst_id={0}", t.getProcessInstanceId());
        List<TRuTask> tRuTasks = tRuTaskService.selectList(ew);
        String notDelete = "";
        List<String> talist= getNextTaskDefinitionKeys(t,false);

        if(talist.size()==1){
            String s=talist.get(0);
            List <Task> tasks=taskService.createTaskQuery().processInstanceId(t.getProcessInstanceId()).taskDefinitionKey(s).orderByTaskCreateTime().desc().list();
            //
            List<String> list=findBeforeTask(s,t.getProcessInstanceId(),t.getProcessDefinitionId(),true);
            boolean isCreate=true;
            if(list!=null&&list.size()>0){
                for(String key:list){
                    Task task=taskService.createTaskQuery().taskDefinitionKey(key).processInstanceId(t.getProcessInstanceId()).singleResult();
                    if(task!=null){
                        isCreate=false;
                        break;
                    }
                }
            }
            if((tasks==null||tasks.size()==0)&&isCreate) {
                long count = historyService.createHistoricActivityInstanceQuery().processInstanceId(t.getProcessInstanceId()).activityId(s).finished().count();
                if (count >= 1) {
                    managementService.executeCommand(new CreateCmd(t.getExecutionId(), s));
                    notDelete += s;
                }
            }
        }

        //第二步 删除多余节点
        List <Execution> exlist=null;
        if(execution==null||StringUtils.isBlank(execution.getParentId())){

        }else{
            exlist = runtimeService.createExecutionQuery().parentId(execution.getParentId()).list();
        }

        //处理删除由于跳转/拿回产生冗余的数据
        if (CollectionUtils.isNotEmpty(exlist)) {

            if (tRuTasks != null&&tRuTasks.size()>0) {
                HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(tRuTasks.get(0).getTaskId()).singleResult();
                List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().executionId(taskInstance.getExecutionId()).orderByTaskCreateTime().asc().list();
                notDelete +=","+ list.get(0).getTaskDefinitionKey();

                //查询审批的那条任务之后生成的任务
                Task task=taskService.createTaskQuery().taskDefinitionKey(list.get(0).getTaskDefinitionKey()).processInstanceId(t.getProcessInstanceId()).active().singleResult();

                for (int i = 0; i < exlist.size(); i++) {
                    //查询当前任务中每个任务信息
                    Task tas = taskService.createTaskQuery().executionId(exlist.get(i).getId()).singleResult();
                    if(tas==null||task==null||tas.getExecutionId().equals(task.getExecutionId())){
                        continue;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String olde = sdf.format(tas.getCreateTime());

                    String newDate = (task == null ? "" : sdf.format(task.getCreateTime()));
                    if (notDelete.contains(tas.getTaskDefinitionKey()) ){
                        continue;
                    }else if(tRuTasks != null&&tRuTasks.size()>0 && olde.equals(newDate)) {
                        TaskEntity entity = (TaskEntity) taskService.createTaskQuery().taskId(tas.getId()).singleResult();

                        entity.setExecutionId(null);
                        taskService.saveTask(entity);
                        taskService.deleteTask(entity.getId(), true);

                        EntityWrapper ewe = new EntityWrapper();
                        ewe.where("task_id={0}", tRuTasks.get(0).getTaskId()).andNew("status={0}", -2);
                        tRuTaskService.delete(ewe);
                    }
                }
            }
        }
    }


    /**
     * 处理由于跳转产生的异常节点任务，影响分支
     * @author houjinrong@chtwm.com
     * date 2018/5/3 16:49
     */
    public void repairExceptionTaskNode(Task task) {
        //获取下一步节点KEY集合
        List<String> nextTaskDefKeyList = getNextTaskDefinitionKeys(task,false);
        List<Task> nextTaskList = Lists.newArrayList();

        //获取与当前节点下步节点的所有上步节点
        if(CollectionUtils.isNotEmpty(nextTaskDefKeyList)){
            List<String> beforeTaskDefKeyList = Lists.newArrayList();
            for(String taskDefKey : nextTaskDefKeyList){
                beforeTaskDefKeyList.addAll(findBeforeTask(taskDefKey,task.getProcessInstanceId(),task.getProcessDefinitionId(),true));
            }
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
            boolean isCreate = true;
            if(CollectionUtils.isNotEmpty(beforeTaskDefKeyList) && CollectionUtils.isNotEmpty(taskList)){
                for(Task t : taskList){
                    if(beforeTaskDefKeyList.contains(t.getTaskDefinitionKey())){
                        isCreate = false;
                    }
                    if(isCreate && nextTaskDefKeyList.contains(t.getTaskDefinitionKey())){
                        nextTaskList.add(t);
                    }
                }
            }
            if(isCreate && CollectionUtils.isEmpty(nextTaskList)) {
                for(String taskDefKey : nextTaskDefKeyList){
                    managementService.executeCommand(new CreateCmd(task.getExecutionId(), taskDefKey));
                }
            }else {
                boolean b1  = false;
                boolean b2 = false;

                for(Task t : taskList){
                    //判断下一步审批任务中包含正在审批的任务
                    if(!b1 && nextTaskDefKeyList.contains(t.getTaskDefinitionKey())){
                        b1 = true;
                    }
                    //判断上一步审批任务中包含正在处理的任务
                    if(!b2 && beforeTaskDefKeyList.contains(t.getTaskDefinitionKey())){
                        b2 = true;
                    }
                    if(b1 && b2){
                        break;
                    }
                }
                //当前任务如存在上个节点的信息，并且也存在下个节点的信息 则删除下个节点列表中在当前任务存在的
                if(b1 && b2){
                    List<ExecutionEntity> executionEntityList=new ArrayList<>();
                    for(Task t : taskList){
                        if(nextTaskDefKeyList.contains(t.getTaskDefinitionKey())){
                            TaskEntity entity = (TaskEntity)t;
                            ExecutionEntity enti= (ExecutionEntity) runtimeService.createExecutionQuery().executionId(t.getExecutionId()).singleResult();
                            executionEntityList.add(enti);

                            entity.setExecutionId(null);
                            taskService.saveTask(entity);
                            taskService.deleteTask(entity.getId(), true);
                            historyService.deleteHistoricTaskInstance(entity.getId());


                        }
                    }
                    if(executionEntityList.size()>0) {
                        managementService.executeCommand(new DestoryExecutionCmd(executionEntityList));
                    }
                }
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
     * 认领是需要将认领人放置到t_ru_task表的assignee_real字段
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
            return new Result(false, ResultEnum.TASK_NOT_EXIST.code, ResultEnum.TASK_NOT_EXIST.msg);
        }
        String assignee = tRuTask.getAssigneeReal();
        if (StringUtils.isNotBlank(assignee)) {
            assignee = assignee + "," + userId;
        } else {
            assignee = userId;
        }
        tRuTask = new TRuTask();
        tRuTask.setId(workId);
        tRuTask.setAssigneeReal(assignee);
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
            return new Result(false, ResultEnum.TASK_NOT_EXIST.code, ResultEnum.TASK_NOT_EXIST.msg);
        }
        String assignee = tRuTask.getAssigneeReal();
        if (StringUtils.isBlank(assignee)) {
            return new Result(false, ResultEnum.TASK_NOT_EXIST.code, ResultEnum.TASK_NOT_EXIST.msg);
        } else if (StringUtils.contains(assignee, userId)) {
            List<String> list = Arrays.asList(StringUtils.split(assignee,","));
            List<String> arraylist = Lists.newArrayList(list);
            arraylist.remove(userId);
            assignee = Joiner.on(",").join(arraylist);
        } else {
            return new Result(false, ResultEnum.ILLEGAL_REQUEST.code, ResultEnum.ILLEGAL_REQUEST.msg);
        }
        tRuTask = new TRuTask();
        tRuTask.setId(workId);
        tRuTask.setAssigneeReal(assignee);
        boolean updateFlag = tRuTaskService.updateById(tRuTask);
        if (updateFlag) {
            return new Result(true, ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg);
        } else {
            return new Result(false, ResultEnum.FAIL.code, ResultEnum.FAIL.msg);
        }
    }

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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result taskJumpOld(String userId, String taskId, String targetTaskDefKey) {
        log.info("跳转任务开始，入参：userId:{},taskId:{},targetTaskDefKey:{}",userId,taskId,targetTaskDefKey);

        //根据要跳转的任务ID获取其任务
        HistoricTaskInstance hisTask = historyService
                .createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult();
        TaskEntity taskEntity= (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
        Integer appkey= (Integer) runtimeService.getVariable(hisTask.getExecutionId(),"appKey");
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


//        taskEntity.setExecutionId(null);
//        taskService.saveTask(taskEntity);
        taskService.deleteTask(taskEntity.getId(), false);


        TRuTask tRuTask=new TRuTask();
        EntityWrapper en=new EntityWrapper();
        en.where("status={0}",-2).andNew("proc_inst_id={0}",instance.getId());
        tRuTaskService.delete(en);

        tRuTask.setStatus(-2);
        EntityWrapper entityWrapper1=new EntityWrapper();
        entityWrapper1.where("task_id={0}",taskId);
        tRuTaskService.update(tRuTask,entityWrapper1);

        EntityWrapper wrapper=new EntityWrapper();
        wrapper.where("app_key={0}",appkey).andNew("proc_inst_id={0}",hisTask.getProcessInstanceId());
        RuProcinst ruProcinst=ruProcinstService.selectOne(wrapper);
        if(ruProcinst==null){
            log.info("跳转失败，t_ru_procinst表中不存在流程实例id"+hisTask.getProcessInstanceId());
          return new Result(true,Constant.FAIL,"跳转失败，t_ru_procinst表中不存在流程实例id"+hisTask.getProcessInstanceId());
        }
        RuProcinst ruPr=new RuProcinst();

        if(StringUtils.isBlank(ruProcinst.getCurrentTaskKey())){
            ruPr.setCurrentTaskKey(targetTaskDefKey);
        }else{
            if(ruProcinst.getCurrentTaskKey().contains(hisTask.getTaskDefinitionKey())){
                ruPr.setCurrentTaskKey(ruProcinst.getCurrentTaskKey().replace(hisTask.getTaskDefinitionKey(),targetTaskDefKey));
            }else{
                ruPr.setCurrentTaskKey(ruProcinst.getCurrentTaskKey()+","+targetTaskDefKey);
            }
        }
        EntityWrapper entity=new EntityWrapper();
        entity.where("app_key={0}",appkey).andNew("proc_inst_id={0}",hisTask.getProcessInstanceId());
        ruProcinstService.update(ruPr,entity);
        boolean customApprover = (boolean) runtimeService.getVariable(instance.getProcessInstanceId(), ConstantUtils.SET_ASSIGNEE_FLAG);

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
                    boolean flag = setAssignee(task, tUserTask);
                }
            }
        }
       Task task= taskService.createTaskQuery().processInstanceId(hisTask.getProcessInstanceId()).taskDefinitionKey(targetTaskDefKey).singleResult();
        log.info("跳转成功");
        Result result=new Result(true,Constant.SUCCESS,"跳转成功");
        if(task!=null) {
            result.setObj(setButtons(TaskNodeResult.toTaskNodeResult(task)));
        }
        return result;
    }

    /**
     * 【任务跳转】 管理员权限不受限制，可以任意跳转到已完成任务节点
     * （跳转到指定节点，删除指定节点后所有节点任务。从指定节点开始重新流转，不影响于指定节点同级的节点任务重复审批）
     * @param userId           操作人ID
     * @param taskId           任务ID
     * @param targetTaskDefKey 跳转到的任务节点KEY
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/9 19:45
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result taskJump(String userId, String taskId, String targetTaskDefKey) {
        log.info("跳转任务开始，入参：userId:{},taskId:{},targetTaskDefKey:{}",userId,taskId,targetTaskDefKey);
        //根据要跳转的任务ID获取其任务
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if(hisTask == null){
            return new Result("任务ID【"+taskId+"】没有对应的任务");
        }

        //创建目标节点任务
        //取得流程定义
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(hisTask.getProcessDefinitionId());
        //获取历史任务的Activity
        ActivityImpl hisActivity = definition.findActivity(targetTaskDefKey);
        //实现跳转
        ExecutionEntity e = managementService.executeCommand(new TaskJumpCmd(hisTask.getProcessInstanceId(), hisTask.getExecutionId(), hisActivity.getId()));
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisTask.getProcessInstanceId()).orderByTaskCreateTime().desc().list();
        Set<String> deleteTaskSet = Sets.newHashSet();

        if(CollectionUtils.isNotEmpty(taskList)){
            List<String> nextTaskDefKeys = findNextTaskDefKeys(taskList.get(0), true);
            historyService.deleteHistoricTaskInstance(taskId);
            for(Task t : taskList){
                if(!t.getTaskDefinitionKey().equals(targetTaskDefKey) && nextTaskDefKeys.contains(t.getTaskDefinitionKey())) {
                    TaskEntity entity = (TaskEntity) taskService.createTaskQuery().taskId(t.getId()).singleResult();
                    entity.setExecutionId(null);
                    taskService.saveTask(entity);
                    taskService.deleteTask(entity.getId(), true);

                    historyService.deleteHistoricTaskInstance(entity.getId());
                    deleteTaskSet.add(t.getId());
                }
            }

            List<Execution> exeList = runtimeService.createExecutionQuery().processInstanceId(hisTask.getProcessInstanceId()).list();
            List<ExecutionEntity> executionEntityList = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(exeList)){
                for(Execution execution : exeList){
                    if(!execution.getId().equals(e.getId()) && StringUtils.isNotBlank(execution.getParentId()) && nextTaskDefKeys.contains(execution.getActivityId())){
                        ExecutionEntity executionEntity = (ExecutionEntity)execution;
                        executionEntityList.add(executionEntity);
                    }
                }
                managementService.executeCommand(new DestoryExecutionCmd(executionEntityList));
            }
        }

        //删除t_ru_task对应的数据记录
        EntityWrapper wrapper = new EntityWrapper();
        if(CollectionUtils.isNotEmpty(deleteTaskSet)){
            wrapper.in("task_id",deleteTaskSet);
            tRuTaskService.delete(wrapper);
        }

        Integer appKey = (Integer) runtimeService.getVariable(hisTask.getExecutionId(),"appKey");
        wrapper = new EntityWrapper();
        wrapper.where("app_key={0}",appKey).andNew("proc_inst_id={0}",hisTask.getProcessInstanceId());
        RuProcinst ruProcinst=ruProcinstService.selectOne(wrapper);
        if(ruProcinst==null){
            log.info("跳转失败，t_ru_procinst表中不存在流程实例id"+hisTask.getProcessInstanceId());
            return new Result(true,Constant.FAIL,"跳转失败，t_ru_procinst表中不存在流程实例id"+hisTask.getProcessInstanceId());
        }
        RuProcinst ruPr=new RuProcinst();

        if(StringUtils.isBlank(ruProcinst.getCurrentTaskKey())){
            ruPr.setCurrentTaskKey(targetTaskDefKey);
        }else{
            if(ruProcinst.getCurrentTaskKey().contains(hisTask.getTaskDefinitionKey())){
                ruPr.setCurrentTaskKey(ruProcinst.getCurrentTaskKey().replace(hisTask.getTaskDefinitionKey(),targetTaskDefKey));
            }else{
                ruPr.setCurrentTaskKey(ruProcinst.getCurrentTaskKey()+","+targetTaskDefKey);
            }
        }
        wrapper = new EntityWrapper();
        wrapper.where("app_key={0}",appKey).andNew("proc_inst_id={0}",hisTask.getProcessInstanceId());
        ruProcinstService.update(ruPr,wrapper);
        boolean customApprover = (boolean) runtimeService.getVariable(hisTask.getProcessInstanceId(), ConstantUtils.SET_ASSIGNEE_FLAG);

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
                    boolean flag = setAssignee(task, tUserTask);
                }
            }
        }
        Task task = taskService.createTaskQuery().processInstanceId(hisTask.getProcessInstanceId()).taskDefinitionKey(targetTaskDefKey).singleResult();
        log.info("跳转成功");
        Result result=new Result(true,Constant.SUCCESS,"跳转成功");
        if(task!=null) {
            result.setObj(setButtons(TaskNodeResult.toTaskNodeResult(task)));
        }
        return result;
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
        log.info("转办任务开始，入参userId:{},taskId:{},targetUserId:{}",userId,taskId,targetUserId);
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            log.info("任务不存在");
            return new Result(ResultEnum.TASK_NOT_EXIST.code, ResultEnum.TASK_NOT_EXIST.msg);
        }

        Emp user = empService.selectByCode(targetUserId);
        if(user == null){
            log.info("被转办人不存在");
            return new Result(false,Constant.FAIL, "被转办人不存在");
        }

        EntityWrapper<TRuTask> wrapper = new EntityWrapper();
        wrapper.where("task_id={0}", taskId);

        String oldUser = userId;

        if(userId.indexOf(":") > -1){
            String[] array = userId.split(":");
            userId = array[0];
            oldUser = array[1];
        }

        wrapper.and("assignee={0}", userId);
        TRuTask tRuTask = tRuTaskService.selectOne(wrapper);

        if(AssignTypeEnum.ROLE.code.equals(tRuTask.getAssigneeType())){
            log.info("审批类型为角色时不可转办");
            return new Result(false, Constant.FAIL,"审批类型为角色时不可转办");
        }

        //用户组权限判断
        if (!ConstantUtils.ADMIN_ID.equals(userId) && tRuTask == null) {
            log.info("您所在的用户组没有权限进行该操作");
            return new Result(false, Constant.FAIL,"您所在的用户组没有权限进行该操作");
        }

        if(StringUtils.contains(tRuTask.getAssigneeReal(), targetUserId)){
            log.info("办理人已存在，同一办理人只能办理一次");
            return new Result(false, Constant.FAIL,"办理人已存在，同一办理人只能办理一次");
        }
        if(userId.indexOf(":") < 0){
            tRuTask.setAssignee(targetUserId);
            tRuTask.setAssigneeName(user.getName());
        }

        tRuTask.setAssigneeReal(tRuTask.getAssigneeReal().replace(oldUser, targetUserId));

        tRuTaskService.updateById(tRuTask);
        TTaskNotice tTaskNotice1=new TTaskNotice();
//        tTaskNotice.setEmpNo(oldUser);
//        tTaskNotice.setTaskId(taskId);
        tTaskNotice1.setState(1);
        EntityWrapper entityWrapper1 = new EntityWrapper();
        entityWrapper1.where("task_id={0}", taskId).andNew("emp_no={0}",oldUser);
        tTaskNoticeService.update(tTaskNotice1,entityWrapper1);

        TTaskNotice tTaskNotice=new TTaskNotice();
        tTaskNotice.setAppKey(tRuTask.getAppKey());
        tTaskNotice.setTaskId(tRuTask.getTaskId());
        tTaskNotice.setProcInstId(tRuTask.getProcInstId());
        tTaskNotice.setCreateId(tRuTask.getAssignee());
        tTaskNotice.setCreateTime(new Date());
        tTaskNotice.setUpdateId(tRuTask.getAssignee());
        tTaskNotice.setUpdateTime(new Date());
        tTaskNotice.setState(0);
        tTaskNotice.setEmpNo(targetUserId);
        tTaskNotice.setUserType(tRuTask.getAssigneeType());
        tTaskNotice.setType(1);
        tTaskNotice.setEmpName(tRuTask.getAssigneeName());
        tTaskNotice.setUpdateName(tRuTask.getAssigneeName());
        tTaskNotice.setCreateName(tRuTask.getAssigneeName());
        tTaskNoticeService.insert(tTaskNotice);
        //TODO 转办任务 消息推送给 targetUserId
        return new Result(true,Constant.SUCCESS, "转办任务成功");
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
        log.info("催办任务开始：入参：userId{},taskId{}",userId,taskId);
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            log.info("任务不存在");
            return new Result(ResultEnum.TASK_NOT_EXIST.code, ResultEnum.TASK_NOT_EXIST.msg);
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
     * 意见征询
     *
     * @param userId            操作人ID
     * @param processInstanceId 任务流程实例ID
     * @param currentTaskDefKey 当前意见征询任务节点KEY
     * @param targetTaskDefKey  目标意见征询任务节点KEY
     * @param commentResult     意见
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result taskEnquire(String userId, String processInstanceId, String currentTaskDefKey, String targetTaskDefKey, String commentResult,String askedUserId,String assigneeAgent) {
        log.info("意见征询开始：入参：userId{},processInstanceId{},currentTaskDefKey{},targetTaskDefKey{},commentResult{},askedUserId{}",userId,processInstanceId,currentTaskDefKey,targetTaskDefKey,commentResult,askedUserId);
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(currentTaskDefKey).singleResult();
        if (task == null) {
            log.error("意见征询的任务不存在 processInstanceId:{},taskDefinitionKey:{}", processInstanceId, currentTaskDefKey);
            return new Result(ResultEnum.TASK_NOT_EXIST.code, ResultEnum.TASK_NOT_EXIST.msg);
        }
        //校验是否是上级节点
        List<String> parentNodes = getBeforeTaskDefinitionKeys(task, true);
        if (!parentNodes.contains(targetTaskDefKey)) {
            log.info("无权意见征询该节点");
            return new Result(false,Constant.FAIL, "无权意见征询该节点");
        }
        List<HistoricTaskInstance> taskInstanceList=historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskDefinitionKey(targetTaskDefKey).finished().orderByTaskCreateTime().desc().list();
        if(taskInstanceList==null||taskInstanceList.size()==0){
            log.info("该任务不存在或该节点不存在");
            return new Result(false,Constant.FAIL, "该任务不存在或该节点不存在");
        }else{
            HistoricTaskInstance historicTaskInstance=taskInstanceList.get(0);
            if(!historicTaskInstance.getAssignee().contains(askedUserId)){
                log.info("被意见征询节点该任务没有被用户"+askedUserId+"审批");
                return new Result(false,Constant.FAIL, "被意见征询节点该任务没有被用户"+askedUserId+"审批");
            }
        }
        //校验是否已有意见征询
        EntityWrapper<TAskTask> wrapper = new EntityWrapper<>();
        wrapper.where("proc_inst_id={0}", processInstanceId)
                .where("current_task_key={0}", currentTaskDefKey)
                .where("ask_task_key={0}", targetTaskDefKey)
                .where("asked_user_id={0}",askedUserId)
                .where("is_ask_end=0");
        List<TAskTask> list = tAskTaskService.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            log.info("已存在意见征询任务");
            return new Result(false,Constant.ASK_TASK_EXIT, "已存在意见征询任务");
        }

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
        askTask.setAskedUserId(askedUserId);

        List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskDefinitionKey(targetTaskDefKey).finished().orderByHistoricTaskInstanceEndTime().desc().list();
        if(CollectionUtils.isNotEmpty(hisTaskList)){
            askTask.setAskedTaskId(hisTaskList.get(0).getId());
        }
        boolean success = tAskTaskService.insert(askTask);
        if (!success) {
            log.info("意见征询失败");
            return new Result(false, Constant.FAIL,"意见征询失败");
        }

        //如果是代理人问询，添加记录
        if(StringUtils.isNotBlank(assigneeAgent)){
            TaskAgent taskAgent = new TaskAgent();
            taskAgent.setId(null);
            taskAgent.setAgentType(2);
            taskAgent.setAssignee(userId);
            taskAgent.setAssigneeAgent(assigneeAgent);
            taskAgent.setTaskId(task.getId());
            taskAgent.setCreateTime(new Date());

            taskAgentService.insert(taskAgent);
        }

        EntityWrapper<RuProcinst> wrapper_ = new EntityWrapper<>();
        wrapper_.where("proc_inst_id={0}", processInstanceId);

        RuProcinst ruProcinst = new RuProcinst();
        ruProcinst.setCurrentTaskStatus(2);
        success = ruProcinstService.update(ruProcinst, wrapper_);
        if(!success){
            log.info("意见征询后，修改任务状态为【意见征询中：1】失败");
            return new Result(false, Constant.FAIL,"意见征询后，修改任务状态为【意见征询中：1】失败");
        }
        ProcessInstance processInstance=runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult() ;
        TWorkDetail tWorkDetail = new TWorkDetail();
        tWorkDetail.setCreateTime(new Date());
        tWorkDetail.setDetail("工号为【" + userId + "】的员工进行了【意见征询】操作");
        tWorkDetail.setProcessInstanceId(processInstanceId);
        tWorkDetail.setOperator(userId);
        tWorkDetail.setTaskId(task.getId());
        tWorkDetail.setAprroveInfo(commentResult);
        List<HistoricTaskInstance> historicTaskInstances=historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).orderByTaskCreateTime().desc().list();
        tWorkDetail.setOperateAction("意见征询");
        tWorkDetail.setOperTaskKey(historicTaskInstances.get(0).getName());
        tWorkDetail.setBusinessKey(processInstance.getBusinessKey());
        workDetailService.insert(tWorkDetail);
        log.info("意见征询成功");
        // TODO 问询
        return new Result(true,Constant.SUCCESS, "意见征询成功");
    }

    /**
     * 意见征询确认
     *
     * @param userId        操作人ID
     * @param askId        流程实例id
     * @param answerComment 确认信息
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result taskConfirmEnquire(String userId, String askId, String answerComment) {
        log.info("确认意见征询开始，入参：userId{},askId{},answerComment{}",userId,askId,answerComment);
        EntityWrapper<TAskTask> wrapper = new EntityWrapper<>();
        //wrapper.where("`asked_user_id`={0}", userId)
        wrapper.where("id={0}", askId).where("is_ask_end={0}", 0);
        TAskTask tAskTask = tAskTaskService.selectOne(wrapper);
        if (tAskTask == null) {
            log.error("意见征询不存在或状态为已确认 askId:{}", askId);
            return new Result(false,Constant.FAIL, "意见征询确认失败");
        }
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(tAskTask.getProcInstId()).taskDefinitionKey(tAskTask.getCurrentTaskKey()).orderByTaskCreateTime().desc().list();
        if (CollectionUtils.isEmpty(list)) {
            log.error("确认意见征询的任务不存在 processInstanceId:{}", askId);
            return new Result(ResultEnum.TASK_NOT_EXIST.code, ResultEnum.TASK_NOT_EXIST.msg);
        }
        tAskTask.setUpdateTime(new Date());
        tAskTask.setAnswerComment(answerComment);
        tAskTask.setIsAskEnd(1);
        boolean success = tAskTaskService.updateById(tAskTask);
        if (!success) {
            return new Result(false,Constant.FAIL, "意见征询确认失败");
        }

        EntityWrapper<RuProcinst> wrapper_ = new EntityWrapper<>();
        wrapper_.where("proc_inst_id={0}", tAskTask.getProcInstId());

        RuProcinst ruProcinst = new RuProcinst();
        ruProcinst.setCurrentTaskStatus(1);
        success = ruProcinstService.update(ruProcinst, wrapper_);
        if(!success){
            log.info("意见征询后，修改任务状态为【意见征询中：1】失败");
            return new Result(false, Constant.FAIL,"意见征询后，修改任务状态为【意见征询中：1】失败");
        }

        ProcessInstance processInstance=runtimeService.createProcessInstanceQuery().processInstanceId(tAskTask.getProcInstId()).singleResult() ;
        TWorkDetail tWorkDetail = new TWorkDetail();
        tWorkDetail.setCreateTime(new Date());
        tWorkDetail.setDetail("工号为【" + userId + "】的员工进行了【意见征询】操作");
        tWorkDetail.setProcessInstanceId(tAskTask.getProcInstId());
        tWorkDetail.setOperator(userId);
        tWorkDetail.setTaskId(list.get(0).getId());
        tWorkDetail.setAprroveInfo(answerComment);

        tWorkDetail.setOperateAction("确认意见征询");
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(tAskTask.getAskedTaskId()).singleResult();
        if(historicTaskInstance!=null){
            tWorkDetail.setOperTaskKey(historicTaskInstance.getName());
        }else{
            tWorkDetail.setOperTaskKey(tAskTask.getAskTaskKey());
        }

        tWorkDetail.setBusinessKey(processInstance.getBusinessKey());
        workDetailService.insert(tWorkDetail);
        log.info("意见征询确认成功");
        // TODO 意见征询确认
        return new Result(true, Constant.SUCCESS,"意见征询确认成功");
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
    @Override
    public Result taskRollback(String userId, String taskId, String targetTaskDefKey) {
        if(StringUtils.isNotBlank(targetTaskDefKey)){
            Result result = taskJump(userId, taskId, targetTaskDefKey);
            if("跳转成功".equals(result.getMsg())){
                result.setMsg("退回成功");
            }
            return result;
        }else{
            List<String> taskDefKeysForRollback = getTaskDefKeysForRollback(taskId);
            if (CollectionUtils.isEmpty(taskDefKeysForRollback)) {
                return new Result(false, ResultEnum.TASK_ROLLBACK_FORBIDDEN.code, ResultEnum.TASK_ROLLBACK_FORBIDDEN.msg);
            }
            for (String taskDefKey : taskDefKeysForRollback) {
                taskJump(userId, taskId, taskDefKey);
            }
        }

        return new Result(true, ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg);
    }

    /**
     * 任务撤回-任务审批结束后
     *
     * @param userId 用户ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/27 17:03
     */
    @Override
    public Result taskRevoke(String userId, String taskId) {
        log.info("任务撤回");
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if(hisTask == null){
            return new Result(false, ResultEnum.TASK_NOT_EXIST.code, ResultEnum.TASK_NOT_EXIST.msg);
        }
        /*if(!hisTask.getAssignee().contains(userId)){
            return new Result(false,ResultEnum.TASK_ASSIGNEE_ILLEGAL.code,ResultEnum.TASK_ASSIGNEE_ILLEGAL.msg);
        }*/

        if(hisTask.getEndTime() == null){
            //当前任务未完成

        }else {
            //当前任务已未完成
            List<String> nextTaskDefKeys = findNextTaskDefKeys(hisTask, false);
            if(CollectionUtils.isEmpty(nextTaskDefKeys)){
                return new Result(false, ResultEnum.NEXT_NODE_NOT_EXIST.code, ResultEnum.NEXT_NODE_NOT_EXIST.msg);
            }
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisTask.getProcessInstanceId()).list();
            if(CollectionUtils.isEmpty(taskList)){
                return new Result(false, ResultEnum.TASK_ROLLBACK_NOT_EXIST.code, ResultEnum.TASK_ROLLBACK_NOT_EXIST.msg);
            }
            //校验流程图中下面节点中是否存在审批中的节点，不存在，说明此任务不可撤回
            for(Task task :taskList){
                if(!nextTaskDefKeys.contains(task.getTaskDefinitionKey())){
                    return  new Result("任务不可撤回");
                }
            }
            for(Task t : taskList){
                if(nextTaskDefKeys.contains(t.getTaskDefinitionKey())){
                    Result result = taskJump(userId, t.getId(), hisTask.getTaskDefinitionKey());
                    if(result.isSuccess()){
                        result.setMsg("任务【"+taskId+"】撤回成功");
                    }
                    return result;
                }
            }
        }

        return new Result("任务不可撤回");
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
                deleteProcessInstance(processInstanceId, "");
            } else {
                return new Result(false, ResultEnum.PERMISSION_DENY.code, ResultEnum.PERMISSION_DENY.msg);
            }
        } else {
            return new Result(false, ResultEnum.PROCINST_NOT_EXIST.code, ResultEnum.PROCINST_NOT_EXIST.msg);
        }

        return new Result(true, ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg);
    }

    /**
     * 挂起流程
     *
     * @param taskActionParam
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:03
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result processSuspend(TaskActionParam taskActionParam, boolean needLog) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(taskActionParam.getProcessInstanceId()).active().singleResult();
        if(processInstance == null){
            return new Result("流程实例不存在或流程已结束或已暂停");
        }
        runtimeService.suspendProcessInstanceById(taskActionParam.getProcessInstanceId());
        if(needLog){
            TWorkDetail tWorkDetail = new TWorkDetail();
            tWorkDetail.setOperator(taskActionParam.getUserId());
            tWorkDetail.setOperateAction(TaskActionEnum.SUSPEND.desc);
            tWorkDetail.setProcessInstanceId(taskActionParam.getProcessInstanceId());
            tWorkDetail.setCreateTime(new Date());
            tWorkDetail.setBusinessKey(processInstance.getBusinessKey());
            tWorkDetail.setDetail("工号【" + taskActionParam.getUserId() + "】挂起了流程【"+taskActionParam.getProcessInstanceId()+"】");
            tWorkDetail.setTaskId("");
            workDetailService.insert(tWorkDetail);
        }
        return new Result(true,Constant.SUCCESS, "挂起流程成功");
    }

    /**
     * 激活流程
     *
     * @param taskActionParam
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:03
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result processActivate(TaskActionParam taskActionParam, boolean needLog) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(taskActionParam.getProcessInstanceId()).suspended().singleResult();
        if(processInstance == null){
            return new Result("流程实例不存在或流程已结束或已激活");
        }
        runtimeService.activateProcessInstanceById(taskActionParam.getProcessInstanceId());
        if(needLog) {
            TWorkDetail tWorkDetail = new TWorkDetail();
            tWorkDetail.setOperator(taskActionParam.getUserId());
            tWorkDetail.setOperateAction(TaskActionEnum.ACTIVATE.desc);
            tWorkDetail.setProcessInstanceId(taskActionParam.getProcessInstanceId());
            tWorkDetail.setCreateTime(new Date());
            tWorkDetail.setBusinessKey(processInstance.getBusinessKey());
            tWorkDetail.setDetail("工号【" + taskActionParam.getUserId() + "】激活了流程【" + taskActionParam.getProcessInstanceId() + "】");
            tWorkDetail.setTaskId("");
            workDetailService.insert(tWorkDetail);
        }
        return new Result(true,Constant.SUCCESS, "激活流程成功");
    }

    /**
     * 意见征询意见查询接口
     *
     * @param userId 操作人ID
     * @param askId  意见征询id
     * @return
     */
    @Override
    public Result askComment(String userId, String askId) {
        EntityWrapper<TAskTask> wrapper = new EntityWrapper<>();
        wrapper.where("id={0}", askId);
        TAskTask askTask = tAskTaskService.selectOne(wrapper);
        if (askTask == null) {
            return new Result(false,Constant.SUCCESS, "意见征询不存在");
        }
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().processInstanceId(askTask.getProcInstId()).taskDefinitionKey(askTask.getAskTaskKey()).singleResult();
        if (task == null) {
            return new Result(ResultEnum.TASK_NOT_EXIST.code, ResultEnum.TASK_NOT_EXIST.msg);
        }
        Result result = new Result(true,Constant.SUCCESS, "查询成功");
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
     * @return 分页
     * @author houjinrong@chtwm.com
     * date 2018/4/20 15:35
     */
    @Override
    public void openTaskList(PageInfo pageInfo) {
        log.info("查询未办任务列表openTaskList");
        Page<TaskResult> page = new Page<TaskResult>(pageInfo.getNowpage(), pageInfo.getSize());
        List<TaskResult> list = workflowDao.queryOpenTask(page, pageInfo.getCondition());
        List<TaskAgentQueryParam> taskAgentList = (List<TaskAgentQueryParam>) pageInfo.getCondition().get("taskAgentList");
        List<String> assignees = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(taskAgentList)){
            for(TaskAgentQueryParam taskAgentQueryParam : taskAgentList){
                assignees.add(taskAgentQueryParam.getAssigneeAgent());
            }
        }
        for(TaskResult t : list){
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(t.getTaskId()).singleResult();
            t.setAssigneeBefore(getBeforeAssignee(hisTask));
            //设置委托人
            if(StringUtils.isBlank(t.getAssigneeDelegate())){
                setAssigneeDelegate(t, getTaskAssignee(hisTask, (Integer)pageInfo.getCondition().get("appKey")), assignees);
            }
            log.info("任务ID【"+t.getTaskId()+"】的对应的上步审批人为【"+t.getAssigneeBefore()+"】");
            if(StringUtils.isNotBlank(t.getAssigneeBefore())) {
                String[] assigneeBefore = t.getAssigneeBefore().split(",");
                Set<String> assigneeNameSet = Sets.newHashSet();
                for (String assign : assigneeBefore){
                    if(StringUtils.isNotBlank(assign)){
                        Emp rbacUser = empService.selectByCode(assign);
                        if(rbacUser != null){
                            log.info("【"+assign+"】：【"+rbacUser.getName()+"】");
                            assigneeNameSet.add(rbacUser.getName());
                        }else{
                            log.info("工号【"+assign+"】找不到对应的用户信息");
                            assigneeNameSet.add(assign);
                        }
                    }
                }

                t.setAssigneeBeforeName(StringUtils.join(assigneeNameSet, ","));
            }else{
                //发生跳转，问询等打断流程的操作，上一步操作人从操作记录表中获取
                TWorkDetail tWorkDetail = workDetailService.queryLastInfo(t.getProcessInstanceId());
                if(tWorkDetail != null){
                    Emp rbacUser = empService.selectByCode(tWorkDetail.getOperator());

                    if(rbacUser != null){
                        t.setAssigneeBefore(tWorkDetail.getOperator());
                        t.setAssigneeBeforeName(rbacUser.getName());
                    }
                }
            }
        }
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    /**
     * 已办任务列表
     *
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @Override
    public void closeTaskList(PageInfo pageInfo) {
        log.info("查询已办任务列表closeTaskList");
        Page<TaskResult> page = new Page<TaskResult>(pageInfo.getNowpage(), pageInfo.getSize());
        List<TaskResult> list = workflowDao.queryCloseTask(page, pageInfo.getCondition());
        for(TaskResult t : list){
            Set<String> assigneeNameSet = Sets.newHashSet();
            t.setAssigneeNext(getNextAssignee(t.getTaskId()));
            if(StringUtils.isNotBlank(t.getAssigneeNext())) {
                String[] getAssigneeNexts = t.getAssigneeNext().split(",");
                for (String assign:getAssigneeNexts){
                    Emp rbacUser = empService.selectByCode(assign);
                    if (rbacUser != null) {
                        assigneeNameSet.add(rbacUser.getName());
                    }
                }
                t.setAssigneeNextName(StringUtils.join(assigneeNameSet, ","));
            }
            Emp rbacUser=empService.selectByCode(t.getAssignee());
            if(rbacUser!=null){
                t.setAssigneeName(rbacUser.getName());
            }
        }
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    /**
     * 待处理任务（包括待认领和待办任务）
     *
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:01
     */
    @Override
    public void activeTaskList(PageInfo pageInfo) {
        Page<TaskResult> page = new Page<TaskResult>(pageInfo.getNowpage(), pageInfo.getSize());
        List<TaskResult> list = workflowDao.queryActiveTask(page, pageInfo.getCondition());
        for(TaskResult t : list){
            t.setAssigneeBefore(getBeforeAssignee(t.getTaskId()));
        }
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    /**
     * 待认领任务列表， 任务签收后变为待办任务，待办任务可取消签认领
     *
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 15:59
     */
    @Override
    public void claimTaskList(PageInfo pageInfo) {
        Page<TaskResult> page = new Page<TaskResult>(pageInfo.getNowpage(), pageInfo.getSize());
        List<TaskResult> list = workflowDao.queryClaimTask(page, pageInfo.getCondition());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    /**
     * 任务相关列表查询
     *
     * @param taskQueryParam
     * @param type
     * @return
     */
    @Override
    public PageInfo myTaskPage(TaskQueryParam taskQueryParam, String type) {
        StringBuffer sb = new StringBuffer();
        StringBuffer con = new StringBuffer();
        con.append(" WHERE 1=1 ");
        String re;
        String reC = "SELECT COUNT(*)";
        String orderBy;

        String assignee = taskQueryParam.getAssignee();
        String roleId = null;
        RbacDomainContext.getContext().setDomain(rbacKey);
        List<RbacRole> roleList = privilegeService.getAllRoleByUserId(taskQueryParam.getAppKey(), assignee);

        if(CollectionUtils.isNotEmpty(roleList)) {
            roleId = StringUtils.join(roleList, ",");
        }

        if (TaskListEnum.CLOSE.type.equals(type)) {
            orderBy = " ORDER BY art.START_TIME_ DESC ";
            re = "SELECT ahp.START_USER_ID_ AS OWNER_,ahp.NAME_ AS CATEGORY_,ahp.BUSINESS_KEY_ AS DESCRIPTION_,art.* ";
            sb.append(" FROM act_hi_taskinst AS art ");
        } else {
            orderBy = " ORDER BY art.CREATE_TIME_ DESC ";
            re = "SELECT trt.assignee_real AS ASSIGNEE_,ahp.START_USER_ID_ AS OWNER_,trt.STATUS AS PRIORITY_,ahp.NAME_ AS CATEGORY_,ahp.BUSINESS_KEY_ AS DESCRIPTION_,art.* ";
            sb.append(" FROM t_ru_task AS trt LEFT JOIN act_ru_task AS art ON trt.TASK_ID=art.ID_ ");
        }

        sb.append(" LEFT JOIN act_hi_procinst AS ahp ON art.PROC_INST_ID_=ahp.PROC_INST_ID_ ");

        if (taskQueryParam.getAppKey() != null || StringUtils.isNotBlank(taskQueryParam.getCreatorDept())) {
            sb.append(" LEFT JOIN t_ru_procinst AS tap ON art.PROC_INST_ID_=tap.PROC_INST_ID ");
            if(taskQueryParam.getAppKey() != null){
                con.append(" AND tap.APP_KEY = #{appKey}");
            }
            if(StringUtils.isNotBlank(taskQueryParam.getCreatorDept())){
                con.append(" AND tap.creator_dept = #{creatorDept} ");
            }
        }

        if(StringUtils.isNotBlank(taskQueryParam.getProcInstId())){
            con.append(" AND art.PROC_INST_ID_ = #{procInstId} ");
        }

        if (StringUtils.isNotBlank(taskQueryParam.getTitle())) {
            con.append(" AND ahp.NAME_ LIKE #{title} ");
        }

        if (StringUtils.isNotBlank(taskQueryParam.getCreator())) {
            con.append(" AND ahp.START_USER_ID_ = #{creator} ");
        }

        if (StringUtils.isNotBlank(taskQueryParam.getBusinessKey())) {
            con.append(" AND ahp.BUSINESS_KEY_ = #{businessKey} ");
        }

        if (StringUtils.isNotBlank(taskQueryParam.getTaskName())) {
            con.append(" AND art.NAME_ LIKE #{taskName} ");
        }

        if(StringUtils.isNotBlank(taskQueryParam.getTaskId())){
            con.append(" AND art.ID_ = #{taskId} ");
        }

        if(StringUtils.isNotBlank(taskQueryParam.getCreateTimeStart())){
            con.append(" AND art.CREATE_TIME_ >= #{createTimeStart} ");
        }

        if(StringUtils.isNotBlank(taskQueryParam.getCreateTimeEnd())){
            con.append(" AND art.CREATE_TIME_ <= #{createTimeEnd} ");
        }

        if (TaskListEnum.CLOSE.type.equals(type)) {
            if(ProcessStatusEnum.UNFINISHED.status == taskQueryParam.getProcInstState()){
                con.append(" ahp.DELETE_REASON_ IS NULL ");
            }else if(ProcessStatusEnum.UNFINISHED.status == taskQueryParam.getProcInstState()){
                con.append(" ahp.DELETE_REASON_ IS NOT NULL ");
            }
            con.append(" AND art.ASSIGNEE_ LIKE #{assignee} ");
            assignee = assignee + "_";
        } else if (TaskListEnum.CLAIM.type.equals(type)) {
            con.append(" AND art.SUSPENSION_STATE_=1 ");
            con.append(" AND trt.STATUS = " + TaskStatusEnum.BEFORESIGN.status);
            con.append(" AND (");
            con.append(" (trt.ASSIGNEE_TYPE =" + AssignTypeEnum.PERSON.code + " AND trt.ASSIGNEE = #{assignee}) ");

            if(StringUtils.isNotBlank(roleId)){
                con.append(" OR (trt.ASSIGNEE_TYPE =" + AssignTypeEnum.ROLE.code + " AND trt.ASSIGNEE IN (#{roleId}) AND #{assignee} NOT IN (trt.ASSIGNEE_REAL)) ");
            }
            con.append(")");
        } else if (TaskListEnum.ACTIVE.type.equals(type)) {
            con.append(" AND art.SUSPENSION_STATE_=1 ");
            con.append(" AND trt.STATUS IN (" + TaskStatusEnum.BEFORESIGN.status + "," + TaskStatusEnum.OPEN.status + ") ");
            con.append(" AND (");
            con.append(" (trt.ASSIGNEE_TYPE =" + AssignTypeEnum.PERSON.code + " AND trt.ASSIGNEE = #{assignee}) ");

            if(StringUtils.isNotBlank(roleId)){
                con.append(" OR (trt.ASSIGNEE_TYPE =" + AssignTypeEnum.ROLE.code + " AND trt.ASSIGNEE IN (#{roleId})) ");
            }
            con.append(")");
        } else {
            con.append(" AND art.SUSPENSION_STATE_=1 ");
            con.append(" AND trt.STATUS=" + TaskStatusEnum.OPEN.status);
            con.append(" AND trt.ASSIGNEE_REAL LIKE #{assignee} ");
        }

        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        String sql = sb.toString() + con.toString();
        if (TaskListEnum.CLOSE.type.equals(type)) {
            NativeHistoricTaskInstanceQuery query = historyService.createNativeHistoricTaskInstanceQuery()
                    .parameter("appKey", taskQueryParam.getAppKey())
                    .parameter("title", "%" + taskQueryParam.getTitle() + "%")
                    .parameter("creator", taskQueryParam.getCreator())
                    .parameter("taskName", "%" + taskQueryParam.getTaskName() + "%")
                    .parameter("assignee", "%" + assignee + "%")
                    //.parameter("departmentId", departmentId)
                    .parameter("roleId", roleId);
            List<HistoricTaskInstance> tasks = query.sql(re + sql + orderBy).listPage(pageInfo.getFrom(), pageInfo.getSize());
            pageInfo.setTotal((int) query.sql(reC + sql).count());
            pageInfo.setRows(transferHisTask(taskQueryParam.getAssignee(), tasks,false));
        } else {
            NativeTaskQuery query = taskService.createNativeTaskQuery()
                    .parameter("appKey", taskQueryParam.getAppKey())
                    .parameter("title", "%" + taskQueryParam.getTitle() + "%")
                    .parameter("creator", taskQueryParam.getCreator())
                    .parameter("taskName", "%" + taskQueryParam.getTaskName() + "%")
                    .parameter("assignee", assignee)
                    //.parameter("departmentId", departmentId)
                    .parameter("roleId", roleId);
            List<Task> tasks = query.sql(re + sql + orderBy).listPage(pageInfo.getFrom(), pageInfo.getSize());
            pageInfo.setRows(tasks);
            pageInfo.setTotal((int) query.sql(reC + sql).count());
            pageInfo.setRows(transferTask(taskQueryParam.getAssignee(), tasks,false));
        }

        return pageInfo;
    }

    /**
     * 任务相关列表查询
     *
     * @param taskQueryParam
     * @param type
     * @return
     */
    @Override
    public PageInfo allTaskPage(TaskQueryParam taskQueryParam, String type) {
        StringBuffer sb = new StringBuffer();
        StringBuffer con = new StringBuffer();
        con.append(" WHERE 1=1 ");
        String re;
        String reC = "SELECT COUNT(DISTINCT art.ID_)";
        String orderBy;

        if (TaskListEnum.CLOSE.type.equals(type)) {
            orderBy = " ORDER BY art.START_TIME_ DESC ";
            re = "SELECT DISTINCT art.ID_, art.ASSIGNEE_ AS ASSIGNEE_,ahp.START_USER_ID_ AS OWNER_,ahp.NAME_ AS CATEGORY_,ahp.BUSINESS_KEY_ AS DESCRIPTION_,art.* ";
            sb.append(" FROM act_hi_taskinst AS art LEFT JOIN t_ru_task AS trt ON trt.TASK_ID=art.ID_ ");
        } else {
            orderBy = " ORDER BY art.CREATE_TIME_ DESC ";
            re = "SELECT DISTINCT art.ID_ AS ID_, NULL,ahp.START_USER_ID_ AS OWNER_,trt.STATUS AS PRIORITY_,ahp.NAME_ AS CATEGORY_,ahp.BUSINESS_KEY_ AS DESCRIPTION_,art.* ";
            sb.append(" FROM act_ru_task AS art LEFT JOIN t_ru_task AS trt ON trt.TASK_ID=art.ID_ ");
        }

        if (taskQueryParam.getAppKey() != null) {
            sb.append(" LEFT JOIN t_ru_procinst AS tap ON art.PROC_INST_ID_=tap.PROC_INST_ID ");
            con.append(" AND tap.APP_KEY = #{appKey}");
        }

        sb.append(" LEFT JOIN act_hi_procinst AS ahp ON art.PROC_INST_ID_=ahp.PROC_INST_ID_ ");
        if (StringUtils.isNotBlank(taskQueryParam.getTitle())) {
            con.append(" AND ahp.NAME_ LIKE #{title} ");
        }

        if (StringUtils.isNotBlank(taskQueryParam.getCreator())) {
            con.append(" AND ahp.START_USER_ID_ = #{creator} ");
        }

        if (StringUtils.isNotBlank(taskQueryParam.getBusinessKey())) {
            con.append(" AND ahp.BUSINESS_KEY_ = #{businessKey} ");
        }

        if (StringUtils.isNotBlank(taskQueryParam.getTaskName())) {
            con.append(" AND art.NAME_ LIKE #{taskName} ");
        }

        if (TaskListEnum.CLOSE.type.equals(type)) {
            con.append(" AND art.END_TIME_ IS NOT NULL ");
        } else if (TaskListEnum.CLAIM.type.equals(type)) {
            con.append(" AND art.SUSPENSION_STATE_=1 ");
            con.append(" AND trt.STATUS = " + TaskStatusEnum.BEFORESIGN.status);
        } else if (TaskListEnum.ACTIVE.type.equals(type)) {
            con.append(" AND art.SUSPENSION_STATE_=1 ");
            con.append(" AND trt.STATUS IN (" + TaskStatusEnum.BEFORESIGN.status + "," + TaskStatusEnum.OPEN.status + ") ");
        } else {
            con.append(" AND art.SUSPENSION_STATE_=1 ");
            con.append(" AND trt.STATUS=" + TaskStatusEnum.OPEN.status);
        }

        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        String sql = sb.toString() + con.toString();
        if (TaskListEnum.CLOSE.type.equals(type)) {
            NativeHistoricTaskInstanceQuery query = historyService.createNativeHistoricTaskInstanceQuery()
                    .parameter("appKey", taskQueryParam.getAppKey())
                    .parameter("title", "%" + taskQueryParam.getTitle() + "%")
                    .parameter("creator", taskQueryParam.getCreator())
                    .parameter("taskName", "%" + taskQueryParam.getTaskName() + "%")
                    .parameter("businessKey", taskQueryParam.getBusinessKey());

            String dataSql = re + sql + orderBy;
            String countSql = reC + sql;
            List<HistoricTaskInstance> tasks = query.sql(dataSql).listPage(pageInfo.getFrom(), pageInfo.getSize());
            pageInfo.setTotal((int) query.sql(countSql).count());
            pageInfo.setRows(transferHisTask(taskQueryParam.getAssignee(), tasks, true));
        } else {
            NativeTaskQuery query = taskService.createNativeTaskQuery()
                    .parameter("appKey", taskQueryParam.getAppKey())
                    .parameter("title", "%" + taskQueryParam.getTitle() + "%")
                    .parameter("creator", taskQueryParam.getCreator())
                    .parameter("taskName", "%" + taskQueryParam.getTaskName() + "%")
                    .parameter("businessKey", taskQueryParam.getBusinessKey());

            String dataSql = re + sql + orderBy;
            String countSql = reC + sql;
            List<Task> tasks = query.sql(dataSql).listPage(pageInfo.getFrom(), pageInfo.getSize());
            pageInfo.setRows(tasks);
            pageInfo.setTotal((int) query.sql(countSql).count());
            pageInfo.setRows(transferTask(taskQueryParam.getAssignee(), tasks,true));
        }

        return pageInfo;
    }

    /**
     * 获取父级任务节点
     *
     * @param taskId 当前任务节点id
     * @param isAll  是否递归获取全部父节点
     * @return
     */
    @Override
    public Result getBeforeNodes(String taskId, String userId, boolean isAll,boolean needPerson) {
        HistoricTaskInstance task =historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (task == null) {
            log.warn("任务不存在 taskId {}", taskId);
            return new Result(ResultEnum.TASK_NOT_EXIST.code, ResultEnum.TASK_NOT_EXIST.msg);
        }

        try {
            Map<String, FlowNode> beforeTask = findBeforeTask(taskId, isAll);
            Iterator<Map.Entry<String, FlowNode>> iterator = beforeTask.entrySet().iterator();
            List<TaskVo> taskList = Lists.newArrayList();
            while(iterator.hasNext()){
                Map.Entry<String, FlowNode> next = iterator.next();
                FlowNode node = next.getValue();
                TaskVo taskVo = new TaskVo();
                taskVo.setTaskDefinitionKey(node.getId());
                taskVo.setTaskName(node.getName());

                List<HistoricTaskInstance> historicTaskInstances=historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(node.getId()).orderByTaskCreateTime().desc().list();
                if(historicTaskInstances!=null&&historicTaskInstances.size()>0){
                    taskVo.setFormKey(historicTaskInstances.get(0).getFormKey());
                }
                if(needPerson){
                    if(historicTaskInstances!=null&&historicTaskInstances.size()>0){
                        HistoricTaskInstance historicTaskInstance=historicTaskInstances.get(0);
                        String assigns=historicTaskInstance.getAssignee();
                        String [] arrayAssign=assigns.replace("_Y","").replace("_N","").split(",");
                        List<EmpVO> list=new ArrayList<>();
                        for(String assign:arrayAssign){
                            Emp emp=empService.selectByCode(assign);
                            EmpVO empVO=new EmpVO();
                            empVO.setCode(assign);
                            empVO.setName(emp.getName());
                            empVO.setDeptName(emp.getDeptName());
                            list.add(empVO);
                        }
                        taskVo.setEmps(list);
                    }

                }
                //如果历史表中存在任务，则
                if(historicTaskInstances!=null&&historicTaskInstances.size()>0){
                    taskList.add(taskVo);
                }

            }
            Result result = new Result(true,Constant.SUCCESS, "查询成功");
            result.setObj(taskList);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(ResultEnum.TASK_NOT_EXIST.code, ResultEnum.TASK_NOT_EXIST.msg);
    }

    /**
     * 我的流程
     *
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 15:59
     */
    @Override
    public void processInstanceList(PageInfo pageInfo){
        Page<ProcessInstanceResult> page = new Page<ProcessInstanceResult>(pageInfo.getNowpage(), pageInfo.getSize());
        List<ProcessInstanceResult> list = workflowDao.queryProcessInstance(page, pageInfo.getCondition());
        if(CollectionUtils.isNotEmpty(list)){
            for(ProcessInstanceResult inst : list){
                inst.setCurrentTaskNode(getCurrentTask(inst));
            }
        }
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    /**
     * 待处理任务总数（包括待认领和待办任务）
     *
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:01
     */
    @Override
    public Long activeTaskCount(Map<String,Object> paraMap){
        return workflowDao.activeTaskCount(paraMap);
    }

    /**
     * 任务详情
     * @param userId 用户ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/1 9:44
     */
    @Override
    public Result taskDetail(String userId, String taskId){
//        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        List<HistoricTaskInstance> list=historyService.createHistoricTaskInstanceQuery().taskId(taskId).orderByTaskCreateTime().desc().list();
        if(list == null||list.size()==0){
            return new Result("任务ID【"+taskId+"】对应的任务不能存在");
        }

        HistoricTaskInstance historicTaskInstance=list.get(0);
        TaskEntity task = (TaskEntity) taskService.newTask(historicTaskInstance.getId());
        task.setTaskDefinitionKey(historicTaskInstance.getTaskDefinitionKey());
        task.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());


        task.setFormKey(historicTaskInstance.getFormKey());
        task.setName(historicTaskInstance.getName());
        /*EntityWrapper<TRuTask> wrapper = new EntityWrapper<>();
        wrapper.eq("task_id", taskId);
        List<TRuTask> tRuTasks = tRuTaskService.selectList(wrapper);*/

        //权限校验
        /*if(validateTaskAssignee(task, userId,tRuTasks) == null){
            return new Result("用户【"+userId+"】无权查看任务【"+taskId+"】");
        }*/
        TaskNodeResult taskNodeResult=setButtons(TaskNodeResult.toTaskNodeResult(task));
        EntityWrapper entityWrapper=new EntityWrapper();
        entityWrapper.where("current_task_id={0}",taskId).andNew("is_ask_end={0}",0);
        TAskTask tAskTask=tAskTaskService.selectOne(entityWrapper);
        if(tAskTask!=null){
           if(tAskTask.getAskUserId().equals(userId)){
               taskNodeResult.setStatus(0);
           }else{
               taskNodeResult.setStatus(1);
           }
        }else{
            taskNodeResult.setStatus(1);
        }

        Result result = new Result();
        result.setSuccess(true);
        result.setCode(Constant.SUCCESS);
        result.setObj(taskNodeResult);
        return result;
    }

    /**
     * 在当前任务节点获取下一步审批人
     * @author houjinrong@chtwm.com
     * date 2018/6/6 19:14
     */
    @Override
    public List<TaskNodeVo> getNextAssigneeWhenRoleApprove(TaskInfo task){
        List<TaskNodeVo> result = Lists.newArrayList();

        Integer version = getVersion(task.getProcessDefinitionId());
        Integer appKey = getAppKey(task.getProcessInstanceId());
        List<String> nextTaskDefKeys = findNextTaskDefKeys(task, false);
        if(CollectionUtils.isEmpty(nextTaskDefKeys)){
            log.error("没有下一审批节点");
            return null;
        }

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

        EntityWrapper<TUserTask> wrapper = new EntityWrapper<>();
        wrapper.eq("proc_def_key",  processInstance.getProcessDefinitionKey());
        wrapper.eq("version_", version);
        wrapper.eq("assign_type", AssignTypeEnum.ROLE.code);
        wrapper.in("task_def_key", nextTaskDefKeys);

        List<TUserTask> userTasks = tUserTaskService.selectList(wrapper);
        String[] assigneeArray;

        for(TUserTask ut : userTasks){
            if(!AssignTypeEnum.ROLE.code.equals(ut.getAssignType())){
                log.info("审批人类型不是角色，方法不提供支持");
                return result;
            }
            TaskNodeVo taskNode = new TaskNodeVo();
            taskNode.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
            taskNode.setTaskDefinitionKey(ut.getTaskDefKey());
            taskNode.setTaskDefinitionName(ut.getTaskName());

            assigneeArray = ut.getCandidateIds().split(",");
            List<AssigneeVo> assigneeList = Lists.newArrayList();

            for(int k=0;k<assigneeArray.length;k++){
                log.info("查询下一步审批人开始：{},{},{}",rbacKey,Long.parseLong(assigneeArray[k]),appKey);
                RbacDomainContext.getContext().setDomain(rbacKey);
                List<RbacUser> users = privilegeService.getUsersByRoleId(appKey, null, Long.parseLong(assigneeArray[k]));
                log.info("查询结果：{}",users.toString());
                for(RbacUser user : users){
                    AssigneeVo assignee = new AssigneeVo();
                    assignee.setUserCode(user.getCode());
                    assignee.setUserName(user.getName());
                    assigneeList.add(assignee);
                }
            }
            taskNode.setAssignee(assigneeList);
            result.add(taskNode);
        }

        return result;
    }

    @Override
    public Map<String, Object> getVariables(String processInstanceId) {
        List<HistoricVariableInstance> list=historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
        Map<String,Object> map=new HashMap<>();
        for(HistoricVariableInstance historicVariableInstance:list){
            map.put(historicVariableInstance.getVariableName(),historicVariableInstance.getValue());
        }
        List<Task> tasks=taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if(tasks!=null&&tasks.size()>0){
            map.put("lastApprover",tasks.get(0).getAssignee());
        }else{
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();
            if(historicTaskInstances!=null&&historicTaskInstances.size()>0) {
                map.put("lastApprover", historicTaskInstances.get(0).getAssignee());
            }else{
                map.put("lastApprover","老数据，无法兼容");
            }
        }
        return map;
    }

    @Override
    public Comment getComments(String taskId, String userId) {

        List<Comment> list= taskService.getTaskComments(taskId,"1");
        List<Comment> list1=taskService.getTaskComments(taskId,"2");
        list.addAll(list1);
        if(list==null||list.size()==0){
            return null;
        }
        for(Comment comment:list){

            if(comment.getUserId().equals(userId)){
                return comment;
            }
        }
        return null;
    }

    @Deprecated
    public JSONArray getNextAssigneeWhenRoleApproveBackUp(Task task){
        JSONArray result = new JSONArray();
        Integer version = getVersion(task.getProcessDefinitionId());
        Integer appKey = getAppKey(task.getProcessInstanceId());
        List<String> nextTaskDefKeys = findNextTaskDefKeys(task, false);
        if(CollectionUtils.isEmpty(nextTaskDefKeys)){
            log.error("没有下一审批节点");
            return null;
        }
        EntityWrapper<TUserTask> wrapper = new EntityWrapper<>();
        wrapper.eq("version_", version);
        wrapper.in("task_def_key", nextTaskDefKeys);

        List<TUserTask> userTasks = tUserTaskService.selectList(wrapper);
        String[] assigneeArray;
        String[] assigneeNameArray;
        for(TUserTask ut : userTasks){
            if(!AssignTypeEnum.ROLE.code.equals(ut.getAssignType())){
                log.info("审批人类型不是角色，方法不提供支持");
                return result;
            }
            JSONObject json = new JSONObject();
            json.put("taskDefinitionKey", ut.getTaskDefKey());
            json.put("taskDefinitionName", ut.getTaskName());

            assigneeArray = ut.getCandidateIds().split(",");
            assigneeNameArray = ut.getCandidateName().split(",");
            JSONArray roleArray = new JSONArray();
            for(int k=0;k<assigneeArray.length;k++){
                JSONObject roleObject = new JSONObject();
                roleObject.put("roleCode", assigneeArray[k]);
                roleObject.put("roleName", assigneeNameArray[k]);

                JSONArray userArray = new JSONArray();
                RbacDomainContext.getContext().setDomain(rbacKey);
                List<RbacUser> users = privilegeService.getUsersByRoleId(appKey, null, Long.parseLong(assigneeArray[k]));
                for(RbacUser user : users){
                    JSONObject userObject = new JSONObject();
                    userObject.put("userCode", user.getCode());
                    userObject.put("userName", user.getName());
                    userArray.add(userObject);
                }

                roleObject.put("user", userArray);
                roleArray.add(roleObject);
            }
            json.put("assignee", roleArray);

            result.add(json);
        }

        return result;
    }

    /**
     * 获取用户名称
     * @param userId
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/25 10:44
     */
    @Override
    public String getUserName(String userId){
        if(userId == null){
            return userId;
        }
        Emp emp = empService.selectByCode(userId);
        if(emp == null){
            if(StringUtils.isNumeric(userId)) {
                RbacDomainContext.getContext().setDomain(rbacKey);
                RbacPrivilege privilegeById = privilegeService.getPrivilegeById(Long.parseLong(userId));
                if(privilegeById!=null){
                    return privilegeById.getPrivilegeName();
                }
            }
            return userId;
        }
        return emp.getName();
    }

    /**
     * 获取任务节点审批人信息
     * @param task 任务对象
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/26 10:12
     */
    @Override
    public List<AssigneeVo> getTaskAssignee(TaskInfo task, Integer appKey){
        if(appKey == null){
            appKey = getAppKey(task.getProcessInstanceId());
        }

        String assignee = task.getAssignee();
        Set<String> assigneeSet = Sets.newHashSet();
        if(StringUtils.isNotBlank(assignee)){
            assignee = assignee.replace("_Y", "").replace("_N", "");
            String[] split = assignee.split(",");
            for(String a : split){
                assigneeSet.add(a);
            }
        }

        List<AssigneeVo> assigneeVoList = Lists.newArrayList();

        //已完成历史任务
        if(task instanceof HistoricTaskInstance){
            if(((HistoricTaskInstance) task).getEndTime() != null){
                for(String assignee_ : assigneeSet){
                    AssigneeVo assigneeVo = new AssigneeVo();
                    assigneeVo.setUserCode(assignee_);
                    assigneeVo.setUserName(getUserName(assignee_));
                    assigneeVo.setIsComplete(1);
                    assigneeVoList.add(assigneeVo);
                }
                return assigneeVoList;
            }
        }

        EntityWrapper<TRuTask> wrapper = new EntityWrapper<>();
        wrapper.eq("proc_inst_id", task.getProcessInstanceId());
        wrapper.eq("task_def_key", task.getTaskDefinitionKey());

        List<TRuTask> tRuTasks = tRuTaskService.selectList(wrapper);
        Map<String, AssigneeVo> assigneeVoMap = Maps.newHashMap();
        for(TRuTask rt : tRuTasks){
            if(StringUtils.isNotBlank(rt.getAssigneeReal())){
                String[] array = rt.getAssigneeReal().split(",");
                for(String userCode : array){
                    if(assigneeVoMap.containsKey(userCode)){
                        continue;
                    }
                    AssigneeVo assigneeVo = new AssigneeVo();
                    assigneeVo.setUserCode(userCode);
                    assigneeVo.setUserName(getUserName(userCode));
                    if(assigneeSet.contains(assigneeVo.getUserCode())) {
                        assigneeVo.setIsComplete(1);
                    }else {
                        assigneeVo.setIsComplete(0);
                    }

                    assigneeVoMap.put(userCode, assigneeVo);
                }
            }else if(AssignTypeEnum.ROLE.code.equals(rt.getAssigneeType())){
                RbacDomainContext.getContext().setDomain(rbacKey);
                List<RbacUser> users = privilegeService.getUsersByRoleId(appKey, null, Long.parseLong(rt.getAssignee()));
                for(RbacUser u : users){
                    if(assigneeVoMap.containsKey(u.getCode())){
                        continue;
                    }
                    AssigneeVo assigneeVo = new AssigneeVo();
                    assigneeVo.setUserCode(u.getCode());
                    assigneeVo.setUserName(u.getName());
                    if(assigneeSet.contains(assigneeVo.getUserCode())) {
                        assigneeVo.setIsComplete(1);
                    }else {
                        assigneeVo.setIsComplete(0);
                    }

                    assigneeVoMap.put(u.getCode(), assigneeVo);
                }
            }
        }
        assigneeVoList.addAll(assigneeVoMap.values());
        return assigneeVoList;
    }

    /**
     * 代理人不为空时，生成加密串，防止爬虫，恶意非法请求
     * @param assignee 审批人
     * @param assigneeAgent 被代理人
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/3 15:53
     */
    @Override
    public String getAssigneeSecret(String assignee, String assigneeAgent){
        String md5Hex = DigestUtils.md5Hex(assignee + "(" + assigneeAgent + ")");
        log.info("MD5加密字符串为：{}", md5Hex);
        return md5Hex;
    }

    /**
     * 流程定义列表
     * @param appKey 应用系统KEY
     * @param nameOrKey 流程定义KEY/流程定义名称
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/15 17:39
     */
    @Override
    public PageInfo queryProcessDefinitionList(Integer appKey, String nameOrKey, Integer page, Integer rows){
        PageInfo pageInfo = new PageInfo(page, rows);
        NativeProcessDefinitionQuery query = repositoryService.createNativeProcessDefinitionQuery();
        String select = "SELECT arp.* ";
        String selectCount = "SELECT COUNT(*) ";
        StringBuffer sb = new StringBuffer();

        if(appKey != null){
            sb.append("FROM `act_re_procdef` AS arp, `t_app_model` AS tam WHERE tam.`app_key`=#{appKey} AND arp.`KEY_`=tam.`model_key` AND arp.`VERSION_` =(SELECT MAX(`VERSION_`) FROM `act_re_procdef` AS arp_ WHERE arp.`KEY_`=arp_.`KEY_`)");
            query.parameter("appKey", appKey);
        }else{
            sb.append("FROM `act_re_procdef` AS arp WHERE arp.`VERSION_` =(SELECT MAX(`VERSION_`) FROM `act_re_procdef` AS arp_ WHERE arp.`KEY_`=arp_.`KEY_`)");
        }

        if(StringUtils.isNotBlank(nameOrKey)){
            query.parameter("nameOrKey", nameOrKey);
            sb.append(" AND (arp.`KEY_` LIKE CONCAT('%',#{nameOrKey},'%') OR arp.`NAME_` LIKE CONCAT('%',#{nameOrKey},'%'))");
        }
        List<ProcessDefinition> processDefinitions = query.sql(select + sb.toString()).listPage(pageInfo.getFrom(), pageInfo.getSize());
        List<ProcessDefinitionVo> processDefinitionVos = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(processDefinitions)){
            for(ProcessDefinition processDefinition : processDefinitions){
                ProcessDefinitionVo processDefinitionVo = new ProcessDefinitionVo();
                BeanUtils.copy(processDefinition, processDefinitionVo);
                processDefinitionVos.add(processDefinitionVo);

                processDefinitionVo.setVersion(processDefinition.getVersion());
                Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
                processDefinitionVo.setDeploymentId(processDefinition.getDeploymentId());
                processDefinitionVo.setDeployTime(deployment.getDeploymentTime());
                //挂起状态(1.未挂起 2.已挂起)
                processDefinitionVo.setSuspended(processDefinition.isSuspended()==true?"2":"1");
            }
        }
        pageInfo.setRows(processDefinitionVos);
        pageInfo.setTotal((int) query.sql(selectCount + sb.toString()).count());
        return pageInfo;
    }

    /**
     * 判断是否第一个节点
     * @param task
     * @return
     */
    @Override
    public boolean isFirstNode(TaskInfo task){
        if(CollectionUtils.isNotEmpty(findBeforeTaskDefKeys(task, false))){
            return false;
        }

        return true;
    }

    /**
     * 通过业务主键查询流程实例
     * @param appKey 系统应用KEy
     * @param businessKey 业务主键
     * @param suspensionState 挂起状态：1-激活；2-挂起
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/24 11:36
     */
    @Override
    public RuProcinst queryProcessInstanceByBusinessKey(Integer appKey, String businessKey, Integer suspensionState){
        return workflowDao.queryProcessInstanceByBusinessKey(appKey, businessKey, suspensionState);
    }

    /**
     * 获取juel表达式中变量名称
     * @param expressionStr ${ a==1}${b==2   }${c>3}${d<4}${e!=9}
     * @return Set ["a","b","c","d","e"]
     * @author houjinrong@chtwm.com
     * date 2018/9/6 11:49
     */
    @Override
    public Set<String> getExpressionName(String expressionStr){
        //expressionStr 例子：${ a==1}${b==2   }${c>3}${d<4}${e!=9}
        Set<String> set = Sets.newHashSet();
        //(?<=\{)(.+?)(?=\}) 匹配{}中内容
        //(?=\$\{)(.+?)(?<=\}) 匹配${}中内容
        Pattern pattern = Pattern.compile("(?=\\$\\{)(.+?)(?<=\\})");

        TreeBuilder builder = new Builder();
        Matcher matcher = pattern.matcher(expressionStr);
        while(matcher.find()){
            String g = matcher.group().trim();

            Tree tree =builder.build(g);
            Iterable<IdentifierNode> node = tree.getIdentifierNodes();
            for(IdentifierNode iden :node){
                set.add(iden.getName());
            }
        }
        return set;
    }

    /**
     * 获取流程实例当前任务节点下步节点信息
     * @author houjinrong@chtwm.com
     * date 2018/6/6 19:14
     */
    @Override
    public List<TaskNodeVo> getNextNodeByTask(ProcessInstance processInstance, TaskInfo task){
        List<TaskNodeVo> result = Lists.newArrayList();
        Integer version = getVersion(task.getProcessDefinitionId());
        Integer appKey = getAppKey(task.getProcessInstanceId());
        List<String> nextTaskDefKeys = findNextTaskDefKeys(task, false);
        if(CollectionUtils.isEmpty(nextTaskDefKeys)){
            log.error("没有下一审批节点");
            return null;
        }

        EntityWrapper<TUserTask> wrapper = new EntityWrapper<>();
        wrapper.eq("proc_def_key",  processInstance.getProcessDefinitionKey());
        wrapper.eq("version_", version);
        wrapper.in("task_def_key", nextTaskDefKeys);

        List<TUserTask> userTasks = tUserTaskService.selectList(wrapper);
        String[] assigneeArray;

        for(TUserTask ut : userTasks){
            TaskNodeVo taskNode = new TaskNodeVo();
            taskNode.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
            taskNode.setTaskDefinitionKey(ut.getTaskDefKey());
            taskNode.setTaskDefinitionName(ut.getTaskName());

            //角色审批
            assigneeArray = ut.getCandidateIds().split(",");
            List<AssigneeVo> assigneeList = Lists.newArrayList();
            if(AssignTypeEnum.ROLE.code.equals(ut.getAssignType())){
                for(int k=0;k<assigneeArray.length;k++){
                    RbacDomainContext.getContext().setDomain(rbacKey);
                    List<RbacUser> users = privilegeService.getUsersByRoleId(appKey, null, Long.parseLong(assigneeArray[k]));
                    for(RbacUser user : users){
                        AssigneeVo assignee = new AssigneeVo();
                        assignee.setUserCode(user.getCode());
                        assignee.setUserName(user.getName());
                        assigneeList.add(assignee);
                    }
                }
            }else{
                for(int k=0;k<assigneeArray.length;k++){
                    Emp emp = empService.selectByCode(assigneeArray[k]);
                    AssigneeVo assignee = new AssigneeVo();
                    assignee.setUserCode(emp.getCode());
                    assignee.setUserName(emp.getName());
                    assigneeList.add(assignee);
                }
            }
            taskNode.setAssignee(assigneeList);
            result.add(taskNode);
        }

        return result;
    }
}