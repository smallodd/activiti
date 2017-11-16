package com.activiti.service.impl;

import com.activiti.common.EmailUtil;
import com.activiti.entity.CommonVo;
import com.activiti.entity.HistoryTaskVo;
import com.activiti.entity.HistoryTasksVo;
import com.activiti.entity.TaskQueryEntity;
import com.activiti.expection.WorkFlowException;
import com.activiti.model.App;
import com.activiti.model.AppModel;
import com.activiti.model.SysUser;
import com.activiti.model.TUserTask;
import com.activiti.service.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.common.util.ConfigUtil;
import com.github.pagehelper.PageInfo;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by ma on 2017/7/18.
 */
public class WorkTaskServiceImpl implements WorkTaskService {
    private Logger logger=Logger.getLogger(WorkTaskServiceImpl.class);
    @Resource
    TaskService taskService;
    @Resource
    HistoryService historyService;
    @Resource
    RepositoryService repositoryService;
    @Resource
    RuntimeService runtimeService;
    @Resource
    IdentityService identityService;
    @Resource
    TUserTaskService tUserTaskService;
    @Resource
    AppModelService appModelService;
    @Resource
    AppService appService;
    @Autowired
    ProcessEngineConfiguration processEngineConfiguration;
    @Autowired
    ProcessEngineFactoryBean processEngine;
    @Autowired
    SysUserService sysUserService;


    public String startTask(CommonVo commonVo,Map<String,Object> paramMap) {

        logger.info("startTask开启任务开始，参数"+commonVo.toString());
        if(StringUtils.isBlank(commonVo.getApplyTitle())||StringUtils.isBlank(commonVo.getApplyUserId())||StringUtils.isBlank(commonVo.getApplyUserName())||StringUtils.isBlank(commonVo.getBusinessKey())||StringUtils.isBlank(commonVo.getBusinessType())||StringUtils.isBlank(commonVo.getModelKey())){
            throw new IllegalArgumentException("参数不合法，请检查参数是否正确,"+commonVo.toString());
        }
        //通过系统key和model key查询系统是否有流程
        EntityWrapper<AppModel> wrapperApp=new EntityWrapper();

        wrapperApp.where("app_key={0}",commonVo.getBusinessType()).andNew("model_key={0}",commonVo.getModelKey());
        AppModel appModelResult=appModelService.selectOne(wrapperApp);

        if(appModelResult==null){
            throw new WorkFlowException("系统键值："+commonVo.getBusinessKey()+"对应的modelkey:"+commonVo.getModelKey());
        }

       String prodefinKey= getProdefineKey(commonVo.getModelKey());
        //commonVo.setApplyTitle(commonVo.getApplyUserName()+"于 "+ com.activiti.common.DateUtils.formatDateToString(new Date())+" 的业务主键为:"+commonVo.getBusinessKey());
        Map<String,Object> resutl=new HashMap<>();
        Map<String,Object> variables=new HashMap<String,Object>();
        try {
            variables= com.activiti.common.BeanUtils.toMap(commonVo);
        } catch (Exception e) {
            try {
                variables= org.apache.commons.beanutils.BeanUtils.describe(commonVo);
            } catch (Exception e1) {
                throw new IllegalArgumentException("参数不合法，请检查参数是否正确,"+commonVo.toString());
            }
        }
        resutl.putAll(variables);
        Set<String> set=paramMap.keySet();
        for(String key: set){
            if(resutl.containsKey(key)){
                throw new RuntimeException("请不要设置重复的属性和commonVo中有的属性");
            }
        }
        resutl.putAll(paramMap);
        resutl.put("proDefinedKey",prodefinKey);
        //设置当前申请人
        identityService.setAuthenticatedUserId(commonVo.getApplyUserId());
        //查询自定义任务列表当前流程定义下的审批人
        EntityWrapper<TUserTask> wrapper =new EntityWrapper<TUserTask>();
        wrapper.where("proc_def_key= {0}",prodefinKey);
        List<TUserTask> tUserTasks=tUserTaskService.selectList(wrapper);
        //启动一个流程
        ProcessInstance processInstance= runtimeService.startProcessInstanceByKey(prodefinKey,commonVo.getBusinessKey(),resutl);

        //为任务设置审批人
        List<Task> tasks=taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).list();

        if(tUserTasks==null||tasks.size()==0){
            throw new RuntimeException("操作失败，请在工作流管理平台设置审批人后在创建任务");
        }
            for(Task task:tasks){
                for(TUserTask tUserTask:tUserTasks){
                    if(StringUtils.isBlank(tUserTask.getCandidateIds())){
                        throw  new RuntimeException("操作失败，请在工作流管理平台将任务节点：'"+tUserTask.getTaskName()+"'设置审批人后在创建任务");
                    }
                    if(task.getTaskDefinitionKey().trim().equals(tUserTask.getTaskDefKey().trim())){
                        if ("candidateGroup".equals(tUserTask.getTaskType())) {
                            taskService.addCandidateGroup(task.getId(), tUserTask.getCandidateIds());
                        } else if ("candidateUser".equals(tUserTask.getTaskType())) {
                            taskService.addCandidateUser(task.getId(), tUserTask.getCandidateIds());
                        } else {

                            taskService.setAssignee(task.getId(), tUserTask.getCandidateIds());
                        }
                    }
                    Boolean flag=Boolean.valueOf(ConfigUtil.getValue("isSendMail"));
                    if(flag) {
                        String[] strs = tUserTask.getCandidateIds().split(",");
                        for (String str : strs) {
                            SysUser sysUser = sysUserService.selectById(str);
                            if (StringUtils.isNotBlank(sysUser.getUserEmail())) {
                                EmailUtil emailUtil = EmailUtil.getEmailUtil();
                                try {
                                    emailUtil.sendEmail(
                                            ConfigUtil.getValue("email.send.account"),
                                            "System emmail",
                                            sysUser.getUserEmail(),
                                            "您有一个待审批邮件待处理",
                                            commonVo.getApplyUserName() + "填写一个审批申请，标题为：" + commonVo.getApplyTitle() + ",请到<a href='http://core.chtwm.com/login.html'>综合业务平台系统</a>中进行审批!");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

        return processInstance.getProcessInstanceId();
    }

    @Override
    public String completeTask(String processInstanceId,String currentUser, String commentContent,String commentResult) throws WorkFlowException{

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee(currentUser).singleResult();
        if(task==null){
            throw new WorkFlowException("当前用户没有该任务，此任务可能已完成或用户主键传的不正确");
        }
        String taskId = task.getId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        identityService.setAuthenticatedUserId(currentUser);
        taskService.addComment(task.getId(), processInstance.getId(),commentResult, commentContent);
        //完成任务
        Map<String, Object> variables = new HashMap<String, Object>();
        if("2".equals(commentResult)){
            variables.put("isPass", true);
            //存请假结果的变量
            runtimeService.setVariable(processInstanceId, "vacationResult", "pass");
        }else if("3".equals(commentResult)){
            variables.put("isPass", false);
            //存请假结果的变量
            runtimeService.setVariable(processInstanceId, "vacationResult", "notPass");
            runtimeService.deleteProcessInstance(processInstanceId,"refuse");
            return processInstanceId;
        }else{
            throw  new WorkFlowException("参数不合法，commentResult 请传 2 审批通过 或 3 审批拒绝");
        }

        // 完成委派任务
        if(DelegationState.PENDING == task.getDelegationState()){
            this.taskService.resolveTask(taskId, variables);
            return processInstanceId;
        }
        Map map=taskService.getVariables(taskId);

        taskService.complete(task.getId(), variables);



        List<Task> tasks=taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        for(Task task1:tasks) {
            EntityWrapper<TUserTask> wrapper = new EntityWrapper<>();
            wrapper.where("task_def_key={0}", task1.getTaskDefinitionKey()).andNew("proc_def_key={0}", map.get("proDefinedKey").toString());
            TUserTask tUser=tUserTaskService.selectOne(wrapper);
            if ("candidateGroup".equals(tUser.getTaskType())) {
                taskService.addCandidateGroup(task1.getId(), tUser.getCandidateIds());
            } else if ("candidateUser".equals(tUser.getTaskType())) {
                taskService.addCandidateUser(task1.getId(), tUser.getCandidateIds());
            } {

                taskService.setAssignee(task1.getId(), tUser.getCandidateIds());
            }
            Boolean flag=Boolean.valueOf(ConfigUtil.getValue("isSendMail"));
            if(flag) {
                String[] strs = task.getAssignee().split(",");
                for (String str : strs) {
                    SysUser sysUser = sysUserService.selectById(str);
                    if (StringUtils.isNotBlank(sysUser.getUserEmail())) {
                        EmailUtil emailUtil = EmailUtil.getEmailUtil();
                        try {
                            emailUtil.sendEmail(
                                    ConfigUtil.getValue("email.send.account"),
                                    "System emmail",
                                    sysUser.getUserEmail(),
                                    "您有一个待审批邮件待处理",
                                    map.get("applyUserName") + "填写一个审批申请，标题为：" + map.get("applyTitle") + ",请到<a href='http://core.chtwm.com/login.html'>综合业务平台系统</a>中进行审批!");
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;

                        }
                    }
                }
            }

        }
        return processInstanceId;


    }


    @Override
    public PageInfo<Task> queryByAssign(String userId,int startPage,int pageSize,TaskQueryEntity taskQueryEntity) {
        logger.info("------------------------通过用户相关信息查询待审批任务开始------------------------");
        TaskQuery  query= createTaskQuqery(taskQueryEntity);

        long count=0;
        if(StringUtils.isNotBlank(userId)){
            query=query.taskAssignee(userId);
            count=query.count();
        }else{
            count=query.count();
        }
        PageInfo<Task> pageInfo=new PageInfo<>();
        List<Task> list=query.orderByTaskCreateTime().desc().listPage((startPage-1)*pageSize,(startPage-1)*pageSize+pageSize);
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        logger.info("------------------------通过用户相关信息查询待审批任务结束------------------------");
        return pageInfo;
    }

    @Override
    public List<HistoricTaskInstance> queryHistoryList(String userId, int startPage, int pageSize,TaskQueryEntity  taskQueryEntity,int type) {


       HistoricTaskInstanceQuery query= createHistoricTaskInstanceQuery(taskQueryEntity);
       if(type==1){
           query.finished();
       }else if(type==0){
           query.unfinished();
       }
        return query.taskAssignee(userId).listPage((startPage-1)*pageSize,(startPage-1)*pageSize+pageSize);
    }


    /**
     * 获取申请人提交的任务
     * @param userid  申请人信息
     * @param startPage  起始页数
     * @param pageSzie    每页显示数
     * @param status      0 :审批中的任务
     *                    1 ：审批完成的任务
     *
     * @return
     */
    public List<HistoricProcessInstance> getApplyTasks(String userid,int startPage,int pageSzie,int status,TaskQueryEntity taskQueryEntity){

        logger.info("--------------------获取申请人提交的任务开始----------------");



        HistoricProcessInstanceQuery query=historyService.createHistoricProcessInstanceQuery();
        if(StringUtils.isBlank(taskQueryEntity.getBussinessType())){
            throw new RuntimeException("参数不合法，业务系统key必须传值");
        }
        query.variableValueEquals("businessType",taskQueryEntity.getBussinessType());
        if(StringUtils.isNotBlank(taskQueryEntity.getModelKey())) {
            Model model = repositoryService.createModelQuery().modelKey(taskQueryEntity.getModelKey()).singleResult();
            query .deploymentId(model.getDeploymentId());
        }else{
            List<String> keys=getProcessKeyByBussnessType(taskQueryEntity.getBussinessType());
            query.processDefinitionKeyIn(keys);
        }
        if(status==0){
            query.unfinished();
        }else {
            query.finished();
        }
        query.orderByProcessInstanceStartTime().desc();
        List<HistoricProcessInstance> list= query.startedBy(userid).listPage((startPage-1)*pageSzie,pageSzie);
        logger.info("--------------------获取申请人提交的任务结束----------------");

        return list;
    }

    /**
     * 获取用户涉及的的审批历史信息
     * @param userid   审批人用户唯一标识
     * @param startPage    起始页数
     * @param pageSzie     每页显示数

     *
     *
     * @return
     */
    public List<HistoricProcessInstance> getInvolvedUserCompleteTasks(String userid,int startPage,int pageSzie,TaskQueryEntity taskQueryEntity){
        logger.info("---------------------获取参与审批用户的审批历史信息开始--------------");


        HistoricProcessInstanceQuery query=historyService.createHistoricProcessInstanceQuery();
        if(StringUtils.isBlank(taskQueryEntity.getBussinessType())){
            throw new RuntimeException("参数不合法，业务系统key必须传值");
        }
        query.variableValueEquals("businessType",taskQueryEntity.getBussinessType());
        if(StringUtils.isNotBlank(taskQueryEntity.getModelKey())) {
            Model model = repositoryService.createModelQuery().modelKey(taskQueryEntity.getModelKey()).singleResult();
            query .deploymentId(model.getDeploymentId());
        }else{
            List<String> keys=getProcessKeyByBussnessType(taskQueryEntity.getBussinessType());
            query.processDefinitionKeyIn(keys);
        }
        query.orderByProcessInstanceStartTime().desc();
        logger.info("---------------------获取参与审批用户的审批历史信息结束--------------");
        return  query.involvedUser(userid).listPage((startPage-1)*pageSzie,pageSzie);
    }

    public PageInfo<HistoricTaskInstance> selectMyComplete(String userId,int startPage,int pageSize,TaskQueryEntity taskQueryEntity){

        logger.info("-----------------------查询用户历史审批过的任务开始----------------");


        PageInfo<HistoricTaskInstance> pageInfo=new PageInfo<HistoricTaskInstance>();
        HistoricTaskInstanceQuery query= createHistoricTaskInstanceQuery(taskQueryEntity);
        List<HistoricTaskInstance> list= query.taskAssignee(userId).finished().orderByHistoricTaskInstanceEndTime().desc().listPage((startPage-1)*pageSize,pageSize);
        long count=query.taskAssigneeLike("%"+userId+"%").finished().count();
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        logger.info("-----------------------查询用户历史审批过的任务结束----------------");
        return pageInfo;
    }

    @Override
    public PageInfo<HistoricTaskInstance> selectMyRefuse(String userId, int startPage, int pageSize,TaskQueryEntity taskQueryEntity) {
        logger.info("----------------------查询用户审批拒绝的信息列表开始----------------");


        PageInfo<HistoricTaskInstance> pageInfo=new PageInfo<HistoricTaskInstance>();
        HistoricTaskInstanceQuery query=createHistoricTaskInstanceQuery(taskQueryEntity);
        List<HistoricTaskInstance> list= query.taskAssignee(userId).taskDeleteReason("refused").listPage((startPage-1)*pageSize,(startPage-1)*pageSize+pageSize);
        long count=query.taskAssigneeLike("%"+userId+"%").taskDeleteReason("refused").count();
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        logger.info("----------------------查询用户审批拒绝的信息列表结束----------------");
        return pageInfo;
    }

    //TODO

    @Override
    public boolean checekBunessKeyIsInFlow(TaskQueryEntity taskQueryEntity,String bussineeKey) {
        if(StringUtils.isBlank(taskQueryEntity.getBussinessType())||StringUtils.isBlank(taskQueryEntity.getModelKey())){
            throw new RuntimeException("参数不合法,业务系统key和modekey 必须都传:"+taskQueryEntity.toString());
        }


        Task task=createTaskQuqery(taskQueryEntity).processInstanceBusinessKey(bussineeKey).singleResult();
        if(task!=null){
            return  true;
        }
        return false;
    }



    @Override
    public List<HistoricTaskInstance> selectTaskHistory(String processId) {

        return historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).orderByTaskCreateTime().desc().list();
    }

    @Override
    public  List<Comment> selectListComment(String processInstanceId){
        return taskService.getProcessInstanceComments(processInstanceId);

    }
    public HistoricTaskInstance selectHistoryTask(String taskHistoryId){
        return historyService.createHistoricTaskInstanceQuery().taskId(taskHistoryId).singleResult();
    }
    @Override
    public Map<String, Object> getVariables(String processId) {
        List<HistoricVariableInstance> list=historyService.createHistoricVariableInstanceQuery().processInstanceId(processId).list();
        Map<String,Object> map=new HashMap<>();
        for(HistoricVariableInstance historicVariableInstance:list){
            map.put(historicVariableInstance.getVariableName(),historicVariableInstance.getValue());
        }
        return map;
    }
/*
    */
/**
     * 获取当前流程的下一个节点
     * @param
     * @return
     */

    private String getNextNode(String procInstanceId){
        // 1、首先是根据流程ID获取当前任务：
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstanceId).list();
        String nextId = "";
        for (Task task : tasks) {

            // 2、然后根据当前任务获取当前流程的流程定义，然后根据流程定义获得所有的节点：
            ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                    .getDeployedProcessDefinition(task.getProcessDefinitionId());
            List<ActivityImpl> activitiList = def.getActivities(); // rs是指RepositoryService的实例
            // 3、根据任务获取当前流程执行ID，执行实例以及当前流程节点的ID：
            String excId = task.getExecutionId();

            ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId)
                    .singleResult();
            String activitiId = execution.getActivityId();
            // 4、然后循环activitiList
            // 并判断出当前流程所处节点，然后得到当前节点实例，根据节点实例获取所有从当前节点出发的路径，然后根据路径获得下一个节点实例：
            for (ActivityImpl activityImpl : activitiList) {
                String id = activityImpl.getId();
                ActivityBehavior activityBehavior = activityImpl.getActivityBehavior();
                UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
                TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();

                if (activitiId.equals(id)) {
                    logger.debug("当前任务：" + activityImpl.getProperty("name")); // 输出某个节点的某种属性
                    List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();// 获取从某个节点出来的所有线路
                    for (PvmTransition tr : outTransitions) {
                        PvmActivity ac = tr.getDestination(); // 获取线路的终点节点
                        logger.debug("下一步任务任务：" + ac.getProperty("name"));

                        nextId = ac.getId();
                    }
                    break;
                }
            }
        }
        return nextId;
    }

    @Override
    public PageInfo<Task> selectAllWaitApprove(int startPage, int pageSize,String bussinessType) {
        logger.info("-------------查询所有待审批的任务开始--------------");
        List<String> keys=getProcessKeyByBussnessType(bussinessType);
        PageInfo<Task> pageInfo=new PageInfo<>();

        List<Task> list= taskService.createTaskQuery().processDefinitionKeyIn(keys).listPage((startPage-1)*pageSize,(startPage-1)*pageSize+pageSize);
        long count =taskService.createTaskQuery().processDefinitionKeyIn(keys).count();
        pageInfo.setTotal(count);
        pageInfo.setList(list);
        logger.info("-------------查询所有待审批的任务结束--------------");
        return  pageInfo;
    }

    @Override
    public PageInfo<HistoricProcessInstance> selectAllPassApprove(int startPage, int pageSize,String bussinessType) {

        logger.info("-------------查询所有通过的任务开始--------------");
        List<String> keys=getProcessKeyByBussnessType(bussinessType);
        int startColum=(startPage-1)*pageSize;
        List<HistoricProcessInstance> list=historyService.createHistoricProcessInstanceQuery().processDefinitionKeyIn(keys).finished().notDeleted().listPage(startColum,startColum+pageSize);
        long count=historyService.createHistoricProcessInstanceQuery().processDefinitionKeyIn(keys).finished().notDeleted().count();
        PageInfo pageInfo=new PageInfo();
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        logger.info("-------------查询所有通过的任务结束--------------");
        return pageInfo;
    }

    @Override
    public PageInfo<HistoricProcessInstance> selectAllRefuseApprove(int startPage, int pageSize,String bussinessType) {
        int startColum=(startPage-1)*pageSize;
        List<String> keys=getProcessKeyByBussnessType(bussinessType);
        List<HistoricProcessInstance> list=historyService.createHistoricProcessInstanceQuery().processDefinitionKeyIn(keys).finished().deleted().listPage(startColum,startColum+pageSize);
        long count=historyService.createHistoricProcessInstanceQuery().processDefinitionKeyIn(keys).finished().deleted().count();
        PageInfo pageInfo=new PageInfo();
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        return pageInfo;
    }

    @Override
    public boolean checkIsPass(String processId) {

        HistoricProcessInstance processInstance=historyService.createHistoricProcessInstanceQuery().notDeleted().processInstanceId(processId).finished().singleResult();
        if(processInstance!=null){
            return  true;
        }
        return false;
    }



    @Override
    public Task queryTaskByProcessId(String processId) {
        return taskService.createTaskQuery().processInstanceId(processId).singleResult();
    }

    @Override
    public HistoricProcessInstance queryProcessInstance(String processId) {

        return historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
    }

    @Override
    public void transferAssignee(String taskId, String userCode) {
        taskService.setAssignee(taskId, userCode);
    }

    @Override
    public void jointProcess(String taskId, List<String> userCodes)
            throws Exception {
        for (String userCode : userCodes) {
            TaskEntity task = (TaskEntity) taskService.newTask(new StrongUuidGenerator()
                    .getNextId());
            task.setAssignee(userCode);
            task.setName(findTaskById(taskId).getName() + "-会签");
            task.setProcessDefinitionId(findProcessDefinitionEntityByTaskId(
                    taskId).getId());
            task.setProcessInstanceId(findProcessInstanceByTaskId(taskId)
                    .getId());
            task.setParentTaskId(taskId);
            task.setDescription("jointProcess");
            taskService.saveTask(task);
        }
    }

    private HistoricTaskInstanceQuery createHistoricTaskInstanceQuery(TaskQueryEntity taskQueryEntity){
        HistoricTaskInstanceQuery  historicTaskInstanceQuery= historyService.createHistoricTaskInstanceQuery();
        if(StringUtils.isBlank(taskQueryEntity.getBussinessType())){
            throw new RuntimeException("参数不合法，业务系统key必须传值");
        }
        historicTaskInstanceQuery.processVariableValueEquals("businessType",taskQueryEntity.getBussinessType());
        if(StringUtils.isNotBlank(taskQueryEntity.getModelKey())) {
            Model model = repositoryService.createModelQuery().modelKey(taskQueryEntity.getModelKey()).singleResult();
            historicTaskInstanceQuery .deploymentId(model.getDeploymentId());
        }else{
            List<String> keys=getProcessKeyByBussnessType(taskQueryEntity.getBussinessType());
            historicTaskInstanceQuery.processDefinitionKeyIn(keys);
        }
        return historicTaskInstanceQuery;
    }
    /**
     * 创建任务查询query
     * @param taskQueryEntity
     * @return
     */
    private TaskQuery createTaskQuqery(TaskQueryEntity taskQueryEntity){
        TaskQuery  query= taskService.createTaskQuery();
        if(StringUtils.isBlank(taskQueryEntity.getBussinessType())){
            throw new RuntimeException("参数不合法，业务系统key必须传值");
        }
        query.processVariableValueEquals("businessType",taskQueryEntity.getBussinessType());
        if(StringUtils.isNotBlank(taskQueryEntity.getModelKey())) {
            Model model = repositoryService.createModelQuery().modelKey(taskQueryEntity.getModelKey()).singleResult();
            query .deploymentId(model.getDeploymentId());
        }else{
            List<String> keys=getProcessKeyByBussnessType(taskQueryEntity.getBussinessType());
            query.processDefinitionKeyIn(keys);
        }
        return query;

    }
    /**
     * 通过业务系统类型获取业务系统下的所有流程定义key
     * @param bussnessType
     * @return
     */
    private  List<String> getProcessKeyByBussnessType(String bussnessType){
        EntityWrapper<AppModel> wrapper=new EntityWrapper<>();
        wrapper.where("app_key={0}",bussnessType);
        List<AppModel> listAppModel=appModelService.selectList(wrapper);
        List<String> keys=new ArrayList<>();
        for(AppModel appModel:listAppModel){
            //通过model key查询model

            keys.add(getProdefineKey(appModel.getModelKey()));
        }
        return keys;
    }

    /**
     * 通过模型key获取流程定义key
     * @param modelKey
     * @return
     */
    private String getProdefineKey(String modelKey){
        try {
            Model model=repositoryService.createModelQuery().modelKey(modelKey).singleResult();
            //通过部署id查询流程定义
            ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().deploymentId(model.getDeploymentId()).latestVersion().singleResult();

            String prodefinKey= processDefinition.getKey();
            return  prodefinKey;
        }catch (Exception e){
            logger.info("获取流程定义key失败，模型键是："+modelKey);
            return "";
        }

    }
    /**
     * 根据任务ID获得任务实例
     *
     * @param taskId
     *            任务ID
     * @return
     * @throws Exception
     */
    private TaskEntity findTaskById(String taskId) throws Exception {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(
                taskId).singleResult();
        if (task == null) {
            throw new Exception("任务实例未找到!");
        }
        return task;
    }

    /**
     * 根据任务ID获取流程定义
     *
     * @param taskId
     *            任务ID
     * @return
     * @throws Exception
     */
    private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
            String taskId) throws Exception {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(findTaskById(taskId)
                        .getProcessDefinitionId());

        if (processDefinition == null) {
            throw new Exception("流程定义未找到!");
        }

        return processDefinition;
    }

    /**
     * 根据任务ID获取对应的流程实例
     *
     * @param taskId
     *            任务ID
     * @return
     * @throws Exception
     */
    private ProcessInstance findProcessInstanceByTaskId(String taskId)
            throws Exception {
        // 找到流程实例
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery().processInstanceId(
                        findTaskById(taskId).getProcessInstanceId())
                .singleResult();
        if (processInstance == null) {
            throw new Exception("流程实例未找到!");
        }
        return processInstance;
    }



    /**
     * 获取下一个用户任务信息
     * @param  taskId     任务Id信息
     * @return  下一个用户任务用户组信息
     * @throws Exception
     */
    private TaskDefinition getNextTaskInfo(String taskId) throws Exception {

        ProcessDefinitionEntity processDefinitionEntity = null;

        String id = null;

        TaskDefinition task = null;

        //获取流程实例Id信息
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();

        //获取流程发布Id信息
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();

        processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(definitionId);

        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        //当前流程节点Id信息
        String activitiId = execution.getActivityId();

        //获取流程所有节点信息
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();

        //遍历所有节点信息
        for(ActivityImpl activityImpl : activitiList){
            id = activityImpl.getId();
            if (activitiId.equals(id)) {
                //获取下一个节点信息
                task = nextTaskDefinition(activityImpl, activityImpl.getId(), null, processInstanceId);
                break;
            }
        }
        return task;
    }

    /**
     * 下一个任务节点信息,
     *
     * 如果下一个节点为用户任务则直接返回,
     *
     * 如果下一个节点为排他网关, 获取排他网关Id信息, 根据排他网关Id信息和execution获取流程实例排他网关Id为key的变量值,
     * 根据变量值分别执行排他网关后线路中的el表达式, 并找到el表达式通过的线路后的用户任务
     * @param  activityImpl     流程节点信息
     * @param  activityId             当前流程节点Id信息
     * @param  elString               排他网关顺序流线段判断条件
     * @param  processInstanceId      流程实例Id信息
     * @return
     */
    private TaskDefinition nextTaskDefinition(ActivityImpl activityImpl, String activityId, String elString, String processInstanceId){

        PvmActivity ac = null;

        Object s = null;

        // 如果遍历节点为用户任务并且节点不是当前节点信息
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            // 获取该节点下一个节点信息
            TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior())
                    .getTaskDefinition();
            return taskDefinition;
        } else {
            // 获取节点所有流向线路信息
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for (PvmTransition tr : outTransitions) {
                ac = tr.getDestination(); // 获取线路的终点节点
                // 如果流向线路为排他网关
                if ("exclusiveGateway".equals(ac.getProperty("type"))) {
                    outTransitionsTemp = ac.getOutgoingTransitions();

                    // 如果网关路线判断条件为空信息
                    if (StringUtils.isEmpty(elString)) {
                        // 获取流程启动时设置的网关判断条件信息
                        elString = getGatewayCondition(ac.getId(), processInstanceId);
                    }

                    // 如果排他网关只有一条线路信息
                    if (outTransitionsTemp.size() == 1) {
                        return nextTaskDefinition((ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId,
                                elString, processInstanceId);
                    } else if (outTransitionsTemp.size() > 1) { // 如果排他网关有多条线路信息
                        for (PvmTransition tr1 : outTransitionsTemp) {
                            s = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
                            // 判断el表达式是否成立
                            if (isCondition(ac.getId(), StringUtils.trim(s.toString()), elString)) {
                                return nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, elString,
                                        processInstanceId);
                            }
                        }
                    }
                } else if ("userTask".equals(ac.getProperty("type"))) {
                    return ((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior()).getTaskDefinition();
                } else {
                }
            }
            return null;
        }
    }
    /**
     * 查询流程启动时设置排他网关判断条件信息
     * @param  gatewayId          排他网关Id信息, 流程启动时设置网关路线判断条件key为网关Id信息
     * @param  processInstanceId  流程实例Id信息
     * @return
     */
    public String getGatewayCondition(String gatewayId, String processInstanceId) {
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult();
        Object object= runtimeService.getVariable(execution.getId(), gatewayId);
        return object==null? "":object.toString();
    }

    /**
     * 根据key和value判断el表达式是否通过信息
     * @param  key    el表达式key信息
     * @param  el     el表达式信息
     * @param  value  el表达式传入值信息
     * @return
     */
    public boolean isCondition(String key, String el, String value) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        context.setVariable(key, factory.createValueExpression(value, String.class));
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);
        return (Boolean) e.getValue(context);
    }

    /**
     * 流程任务跟踪标识
     * @param processInstanceId
     * @return
     */
    @Override
    public byte[] getTaskSchedule(String processInstanceId){
        if(StringUtils.isBlank(processInstanceId)){
            return null;
        }

        try{
            //获取历史流程实例
            HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            //获取流程图
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
            processEngineConfiguration = processEngine.getProcessEngineConfiguration();
            Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);

            ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
            ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

            List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();


            //高亮环节id集合
            List<String> highLightedActivitis = new ArrayList<String>();

            //任务跳转时处理
            EntityWrapper<TUserTask> wrapper =new EntityWrapper<TUserTask>();
            wrapper.where("proc_def_key = {0}", definitionEntity.getKey());
            wrapper.orderBy("order_num",true);
            List<TUserTask> userTaskList= tUserTaskService.selectList(wrapper);
            Map<String,Integer> taskMap = new HashMap<String,Integer>();

            if(CollectionUtils.isNotEmpty(userTaskList)){
                Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
                for (TUserTask u:userTaskList) {
                    taskMap.put(u.getTaskDefKey(),1);
                    if(u.getTaskDefKey().equals(task.getTaskDefinitionKey())){
                        break;
                    }
                }
                Iterator<HistoricActivityInstance> it = highLightedActivitList.iterator();
                while(it.hasNext()){
                    HistoricActivityInstance hai = it.next();
                    if(!taskMap.containsKey(hai.getActivityId()) && "userTask".equals(hai.getActivityType())){
                        it.remove();
                    }
                }
            }

            //高亮线路id集合
            List<String> highLightedFlows = getHighLightedFlows(definitionEntity,highLightedActivitList);

            for(HistoricActivityInstance tempActivity : highLightedActivitList){
                String activityId = tempActivity.getActivityId();
                highLightedActivitis.add(activityId);
            }

            //中文显示的是口口口，设置字体就好了
            InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,highLightedFlows,"宋体","宋体","宋体",null,1.0);
            //单独返回流程图，不高亮显示
            //InputStream imageStream = diagramGenerator.generatePngDiagram(bpmnModel);
            // 输出资源内容到相应对象
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while((len = imageStream.read(buffer)) != -1){
                outStream.write(buffer, 0, len);
            }
            outStream.close();
            imageStream.close();
            return outStream.toByteArray();
        } catch(IOException e){
            logger.error("获取流程任务跟踪标识图失败",e);
        }

        return null;
    }

    /**
     * 获取需要高亮的线
     * @param processDefinitionEntity
     * @param historicActivityInstances
     * @return
     */
    private List<String> getHighLightedFlows(
            ProcessDefinitionEntity processDefinitionEntity,
            List<HistoricActivityInstance> historicActivityInstances) {

        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
            ActivityImpl activityImpl = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i)
                            .getActivityId());// 得到节点定义的详细信息
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
            ActivityImpl sameActivityImpl1 = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i + 1)
                            .getActivityId());
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances
                        .get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances
                        .get(j + 1);// 后续第二个节点
                if (activityImpl1.getStartTime().equals(
                        activityImpl2.getStartTime())) {
                    // 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2 = processDefinitionEntity
                            .findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    // 有不相同跳出循环
                    break;
                }
            }
            List<PvmTransition> pvmTransitions = activityImpl
                    .getOutgoingTransitions();// 取出节点的所有出去的线
            for (PvmTransition pvmTransition : pvmTransitions) {
                // 对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
                        .getDestination();
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }

    /**
     * 根据任务ID查询历史任务信息
     * @param processInstanceId
     * @param variableNames
     * @return
     */
    @Override
    public HistoryTasksVo getTaskHistoryBytaskId(String processInstanceId, List<String> variableNames){
        if(StringUtils.isBlank(processInstanceId)){
            return null;
        }

        HistoryTasksVo hisTask = new HistoryTasksVo();

        logger.info("获取历史任务");

        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();

        //获取全部评论
        List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);
        Map<String,List<String>> commentMap = new HashMap<String,List<String>>();
        for(Comment c : comments){
            if(commentMap.containsKey(c.getTaskId())){
                if(StringUtils.isNotBlank(c.getFullMessage())){
                    commentMap.get(c.getTaskId()).add(c.getFullMessage());
                }
            }else{
                if(StringUtils.isNotBlank(c.getFullMessage())){
                    List<String> commentList = new ArrayList<String>();
                    commentList.add(c.getFullMessage());
                    commentMap.put(c.getTaskId(),commentList);
                }
            }
        }
        SysUser user = new SysUser();//用户临时存储对象
        EntityWrapper<SysUser> wrapper = new EntityWrapper<SysUser>(user);
        List<HistoryTaskVo> taskList = new ArrayList<HistoryTaskVo>();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        int isFinished = (pi == null)?1:0;
        for (HistoricTaskInstance hti : list) {

            HistoryTaskVo ht = new HistoryTaskVo();

            ht.setTaskId(hti.getId());
            //审核人
            user.setId(hti.getAssignee());
            wrapper.isNotNull("id");
            user = sysUserService.selectOne(wrapper);
            ht.setOperator(user.getLoginName());
            ht.setIsLastApprove(isFinished);
            if(1==isFinished){
                isFinished = 0;
            }
            ht.setStartTime(hti.getStartTime());
            ht.setEndTime(hti.getEndTime());
            //审核意见
            ht.setComment(commentMap.get(hti.getId()));

            taskList.add(ht);
        }
        hisTask.setTaskList(taskList);
        logger.info("获取属性值");
        Map<String,Object> variableMap = null;
        if(variableNames != null && variableNames.size() > 0){
            variableMap = new HashMap<String,Object>();
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
            for(HistoricVariableInstance hvi : variables){
                if(variableNames.contains(hvi.getVariableName())){
                    variableMap.put(hvi.getVariableName(),hvi.getValue());
                }
            }
        }

        hisTask.setVariables(variableMap);
        return hisTask;
    }

    /**
     * 获取应用列表
     * @return
     */
    @Override
    public List<App> getAppList(){
        EntityWrapper<App> wrapper = new EntityWrapper<App>();
        wrapper.where("status",1);
        return appService.selectList(wrapper);
    }

    /**
     * 根据应用key获取应用所属的模型列表
     * @param appKey
     * @return
     */
    @Override
    public List<Model> getModelListByAppKey(String appKey){
        if(StringUtils.isBlank(appKey)){
            return null;
        }
        String sql = "SELECT arm.* FROM `act_re_model` AS arm,`t_app_model` AS tam,`t_app` AS ta WHERE ta.KEY='"+appKey+"' AND ta.KEY=tam.APP_KEY AND arm.KEY_=tam.MODEL_KEY ";
        return repositoryService.createNativeModelQuery().sql(sql).list();
    }
}
