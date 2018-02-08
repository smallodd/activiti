package com.activiti.service.impl;

import com.activiti.cmd.DeleteActiveTaskCmd;
import com.activiti.cmd.StartActivityCmd;
import com.activiti.common.CodeConts;
import com.activiti.common.EmailUtil;
import com.activiti.entity.*;
import com.activiti.enums.ProcessVariable;
import com.activiti.enums.TaskStatus;
import com.activiti.enums.TaskType;
import com.activiti.enums.TaskVariable;
import com.activiti.expection.WorkFlowException;
import com.activiti.listener.ConstantUtils;
import com.activiti.model.App;
import com.activiti.model.AppModel;
import com.activiti.model.SysUser;
import com.activiti.model.TUserTask;
import com.activiti.service.*;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.common.util.ConfigUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.*;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by ma on 2017/7/18.
 */
public class WorkTaskV2ServiceImpl implements WorkTaskV2Service {

    private Logger logger=Logger.getLogger(WorkTaskV2ServiceImpl.class);
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
    @Autowired
    private ManagementService managementService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startTask(CommonVo commonVo,Map<String,Object> paramMap) throws WorkFlowException {

        logger.info("startTask开启任务开始，参数"+commonVo.toString());
        if(StringUtils.isBlank(commonVo.getApplyTitle())||StringUtils.isBlank(commonVo.getApplyUserId())||StringUtils.isBlank(commonVo.getApplyUserName())||StringUtils.isBlank(commonVo.getBusinessKey())||StringUtils.isBlank(commonVo.getBusinessType())||StringUtils.isBlank(commonVo.getModelKey())){
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"参数不合法，请检查参数是否正确,"+commonVo.toString());
        }
        String prodefinKey=commonVo.getModelKey();
        //通过系统key和model key查询系统是否有流程
        EntityWrapper<AppModel> wrapperApp=new EntityWrapper();

        wrapperApp.where("app_key={0}",commonVo.getBusinessType()).andNew("model_key={0}",prodefinKey);
        AppModel appModelResult=appModelService.selectOne(wrapperApp);

        if(appModelResult==null){
            throw new WorkFlowException(CodeConts.WORK_FLOW_NOT_RELATION,"系统键值：【"+commonVo.getBusinessType()+"】对应的modelkey:【"+commonVo.getModelKey()+"】关系不存在");
        }
        TaskQueryEntity taskQueryEntity=new TaskQueryEntity();
        taskQueryEntity.setModelKey(commonVo.getModelKey());
        taskQueryEntity.setBussinessType(commonVo.getBusinessType());
        boolean isInFlow= checkBusinessKeyIsInFlow(taskQueryEntity,commonVo.getBusinessKey());
        if(isInFlow){
            throw  new WorkFlowException(CodeConts.WORK_FLOW_BUSSINESS_IN_FLOW,"系统【"+commonVo.getBusinessType()+"】在模型【"+commonVo.getModelKey()+"】中的业务主键【"+commonVo.getBusinessKey()+"】还在流程中，请勿重复提交");
        }
        //modelKey与processKey值一致
        ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionKey(commonVo.getModelKey()).latestVersion().singleResult();
        int version=  processDefinition .getVersion();

        Map<String,Object> result=new HashMap<>();
        Map<String,Object> variables=new HashMap<String,Object>();
        try {
            variables= com.activiti.common.BeanUtils.toMap(commonVo);
        } catch (Exception e) {
            try {
                variables= org.apache.commons.beanutils.BeanUtils.describe(commonVo);
            } catch (Exception e1) {
                throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"参数不合法，请检查参数是否正确,"+commonVo.toString());
            }
        }
        result.putAll(variables);

        //校验属性是否跟系统属性重复
        if(variables != null){
            validateVariables(paramMap);
        }

        result.putAll(paramMap);
        result.put("proDefinedKey",prodefinKey);
        result.put("version",version);
        //设置当前申请人
        identityService.setAuthenticatedUserId(commonVo.getApplyUserId());
        //查询自定义任务列表当前流程定义下的审批人

        //启动一个流程
        ProcessInstance processInstance= runtimeService.startProcessInstanceByKey(prodefinKey,commonVo.getBusinessKey(),result);

        //把流程节点ID用逗号隔开存入属性表
        runtimeService.setVariable(processInstance.getProcessInstanceId(),ProcessVariable.PROCESSNODE.value+processInstance.getProcessInstanceId(), getProcessDefinitionNodeIds(processInstance.getProcessDefinitionId()));

        if(commonVo.isDynamic()){
            return processInstance.getProcessInstanceId();
        }
        if(commonVo.isSuperior()){
            //TODO 获取上级审批人预留
        }

        Map<String,String> mailParam = Maps.newHashMap();
        mailParam.put("applyUserName",commonVo.getApplyUserName());
        mailParam.put("applyTitle",commonVo.getApplyTitle());
        initTaskVariable(processInstance.getProcessInstanceId(),processDefinition.getKey(),processDefinition.getVersion(),mailParam);

        return processInstance.getProcessInstanceId();
    }

    @Override
    public boolean setApprove(String processId,String userCodes) throws WorkFlowException{
        Task task=taskService.createTaskQuery().processInstanceId(processId).singleResult();
        if(task==null){
            throw  new WorkFlowException(CodeConts.WORK_FLOW_TASK_IS_NULL,"任务为空设置失败!");
        }
        if(StringUtils.isNotBlank(task.getAssignee())){
            logger.info("任务："+processId+"已经设置过审批人，请不要重复设置，当前审批人为："+task.getAssignee());
            return false;
        }
        taskService.setAssignee(task.getId(),userCodes);
        Map result=new HashMap();
        for(String candidateId : userCodes.split(",")){
            result.put(task.getTaskDefinitionKey()+":"+candidateId,candidateId+":"+TaskStatus.UNFINISHED.value);
        }
        result.put(task.getTaskDefinitionKey()+":"+TaskVariable.TASKTYPE.value,TaskType.CANDIDATEUSER.value);
        result.put(task.getTaskDefinitionKey()+":"+TaskVariable.TASKUSER.value,userCodes);
        taskService.setVariablesLocal(task.getId(),result);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String completeTask( ApproveVo approveVo,Map<String,Object> paramMap) throws WorkFlowException{
        if(approveVo==null||StringUtils.isBlank(approveVo.getCommentContent())||StringUtils.isBlank(approveVo.getCommentResult())||StringUtils.isBlank(approveVo.getCurrentUser())||StringUtils.isBlank(approveVo.getProcessInstanceId())){
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"非法参数，当前参数为："+ JSONObject.toJSONString(approveVo)+";请查看dubbo接口说明");
        }

        //校验属性是否跟系统属性重复
        if(paramMap != null){
            validateVariables(paramMap);
        }

        String processInstanceId=approveVo.getProcessInstanceId();
        String currentUser=approveVo.getCurrentUser();
        String commentResult=approveVo.getCommentResult();
        String commentContent=approveVo.getCommentContent();
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssigneeLike("%"+currentUser+"%").singleResult();
        if(task==null){
            throw new WorkFlowException(CodeConts.WORK_FLOW_ERROR_TASK,"当前用户没有该任务，此任务可能已完成或非法请求");
        }
        String taskId = task.getId();
        if(paramMap!=null){
            taskService.setVariablesLocal(taskId,paramMap);
        }

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        //添加审批意见
        identityService.setAuthenticatedUserId(currentUser);
        taskService.addComment(task.getId(), processInstance.getProcessInstanceId(),commentResult, commentContent);

        Map<String, Object> variables = Maps.newHashMap();
        Map map=taskService.getVariables(taskId);
        //动态处理审批
        if(approveVo.isDynamic()){
            runtimeService.setVariable(processInstanceId,processInstanceId+":"+ TaskVariable.LASTTASKUSER.value,currentUser);
            taskService.setVariableLocal(taskId,TaskStatus.FINISHED.value+":"+currentUser,TaskStatus.FINISHED.value);

            if(ConstantUtils.vacationStatus.PASSED.getValue().equals(commentResult)){
                taskService.setVariableLocal(taskId,task.getTaskDefinitionKey()+":"+currentUser,currentUser+":"+TaskStatus.FINISHEDPASS.value);
                taskService.complete(taskId,map);

            }else if(ConstantUtils.vacationStatus.NOT_PASSED.getValue().equals(commentResult)){
                taskService.setVariableLocal(taskId,task.getTaskDefinitionKey()+":"+currentUser,currentUser+":"+TaskStatus.FINISHEDREFUSE.value);
                runtimeService.deleteProcessInstance(processInstanceId,"refused");

            }else{
                throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"非法参数,commentResult字段请传字符串2或3");
            }
            ProcessInstance result = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            return result==null?null:processInstanceId;
        }

        Object object = map.get("version");
        int version = 0;
        if(object != null){
            version = (int) object;
        }
        //会签处理
        int userCountNow = 0;

        runtimeService.setVariable(processInstanceId,processInstanceId+":"+ TaskVariable.LASTTASKUSER.value,currentUser);
        if(map != null){
            String taskTypeCurrent = map.get(task.getTaskDefinitionKey()+":"+TaskVariable.TASKTYPE.value) + "";
            if(TaskType.COUNTERSIGN.value.equals(taskTypeCurrent)){

                //会签人

                userCountNow = (int)map.get(task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTNOW.value);
                int userCountTotal = (int)map.get(task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTTOTAL.value);
                int userCountNeed = (int)map.get(task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTNEED.value);
                int userCountRefuse = (int)map.get(task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTREFUSE.value);
                String taskResult = TaskStatus.FINISHEDPASS.value;
                if(ConstantUtils.vacationStatus.NOT_PASSED.getValue().equals(commentResult)){
                    taskResult = TaskStatus.FINISHEDREFUSE.value;
                    userCountRefuse++;
                }
                taskService.setVariableLocal(taskId,task.getTaskDefinitionKey()+":"+currentUser,currentUser+":"+taskResult);
                taskService.setVariableLocal(taskId,task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTNOW.value,++userCountNow);
                taskService.setVariableLocal(taskId,task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTREFUSE.value,userCountRefuse);


                int userCountAgree = userCountNow - userCountRefuse;
                if(userCountAgree >= userCountNeed){
                    //------------任务完成-通过------------
                    commentResult = "2";
                }else{
                    if(userCountTotal - userCountNow + userCountAgree < userCountNeed){
                        //------------任务完成-未通过------------
                        commentResult = "3";
                    }else{
                        //------------任务继续------------
                        taskService.setVariableLocal(taskId,TaskStatus.FINISHED.value+":"+currentUser,TaskStatus.FINISHED.value);

                        return processInstanceId;
                    }
                }
            }else {
                //候选人
                String taskResult = TaskStatus.FINISHEDPASS.value;
                if(ConstantUtils.vacationStatus.NOT_PASSED.getValue().equals(commentResult)){
                    taskResult = TaskStatus.FINISHEDREFUSE.value;
                }
                taskService.setVariableLocal(taskId,task.getTaskDefinitionKey()+":"+currentUser,currentUser+":"+taskResult);

            }
        }
        taskService.setVariableLocal(taskId,TaskStatus.FINISHED.value+":"+currentUser,TaskStatus.FINISHED.value);
        if("2".equals(commentResult)){

        }else if("3".equals(commentResult)){
            runtimeService.deleteProcessInstance(processInstanceId,"refuse");
            return processInstanceId;
        }else{
            throw  new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"参数不合法，commentResult 请传 2 审批通过 或 3 审批拒绝");
        }

        // 完成委派任务
        if(DelegationState.PENDING == task.getDelegationState()){
            this.taskService.resolveTask(taskId, variables);
            return processInstanceId;
        }
        //完成任务
        taskService.complete(task.getId(), variables);
        initTaskVariable(task.getProcessInstanceId(),processInstance.getProcessDefinitionKey(),version,map);
        return processInstanceId;
    }

    @Override
    public PageInfo<Task> queryByAssign(String userId,int startPage,int pageSize,TaskQueryEntity taskQueryEntity) throws WorkFlowException {
        logger.info("------------------------通过用户相关信息查询待审批任务开始------------------------");

        PageInfo<Task> pageInfo = new PageInfo<>();
        long count = 0;

        if(StringUtils.isNotBlank(userId)){
            List<Task> taskList =createTaskQuqery(taskQueryEntity).taskVariableValueEquals(userId+":"+TaskStatus.UNFINISHED.value).taskAssigneeLike("%"+userId+"%").active().listPage((startPage-1)*pageSize,pageSize);
            pageInfo.setList(taskList);
            pageInfo.setTotal(count);
        }else{
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"");
        }
        logger.info("------------------------通过用户相关信息查询待审批任务结束------------------------");
        return pageInfo;
    }


    /**
     * 获取申请人提交的任务
     * @param userid  申请人信息
     * @param startPage  起始页数
     * @param pageSzie    每页显示数
     * @param status      0 :审批中的任务
     *                    1 :审批完成的任务
     * @return
     */
    @Override
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


    @Override
    public PageInfo<HistoricTaskInstance> selectMyComplete(String userId,int startPage,int pageSize,TaskQueryEntity taskQueryEntity){
        logger.info("-----------------------查询用户历史审批过的任务开始----------------");

        PageInfo<HistoricTaskInstance> pageInfo=new PageInfo<HistoricTaskInstance>();
        HistoricTaskInstanceQuery query= createHistoricTaskInstanceQuery(taskQueryEntity);
        List<HistoricTaskInstance> list= query.taskAssigneeLike("%"+userId+"%").taskVariableValueEquals(TaskStatus.FINISHED.value+":"+userId,TaskStatus.FINISHED.value).orderByHistoricTaskInstanceEndTime().desc().listPage((startPage-1)*pageSize,pageSize);
        long count=query.taskAssigneeLike("%"+userId+"%").taskVariableValueEquals(TaskStatus.FINISHED.value+":"+userId,TaskStatus.FINISHED.value).count();
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
        List<HistoricTaskInstance> list= query.taskAssigneeLike("%"+userId+"%").taskVariableValueEquals(userId+":"+TaskStatus.FINISHEDREFUSE.value).listPage((startPage-1)*pageSize,pageSize);
        long count=query.taskAssignee(userId).taskVariableValueEquals(userId+":"+TaskStatus.FINISHEDREFUSE.value).count();
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        logger.info("----------------------查询用户审批拒绝的信息列表结束----------------");
        return pageInfo;
    }

    //TODO

    @Override
    public boolean checkBusinessKeyIsInFlow(TaskQueryEntity taskQueryEntity, String businessKey) {
        if((StringUtils.isBlank(taskQueryEntity.getBussinessType())||StringUtils.isBlank(taskQueryEntity.getModelKey()))){
            throw new RuntimeException("参数不合法,业务系统key和modekey 必须都传:"+taskQueryEntity.toString());
        }

        List<Task> tasks=createTaskQuqery(taskQueryEntity).processInstanceBusinessKey(businessKey).orderByTaskCreateTime().desc().listPage(0,1);
        if(tasks!=null&&tasks.size()>0&&tasks.get(0)!=null){
            return  true;
        }
        return false;
    }


    @Override
    public  List<Comment> selectListComment(String processInstanceId){
        return taskService.getProcessInstanceComments(processInstanceId);
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

    @Override
    public void transferAssignee(String taskId, String userCode) {
        taskService.setAssignee(taskId, userCode);
    }


    private HistoricTaskInstanceQuery createHistoricTaskInstanceQuery(TaskQueryEntity taskQueryEntity){
        HistoricTaskInstanceQuery  historicTaskInstanceQuery= historyService.createHistoricTaskInstanceQuery();
        if(taskQueryEntity == null || StringUtils.isBlank(taskQueryEntity.getBussinessType())){
            throw new RuntimeException("参数不合法，业务系统key必须传值");
        }

        historicTaskInstanceQuery.processVariableValueEquals("businessType", taskQueryEntity.getBussinessType());

        if(StringUtils.isNotBlank(taskQueryEntity.getModelKey())) {

            historicTaskInstanceQuery.processDefinitionKey(taskQueryEntity.getModelKey());
        }else if((StringUtils.isNotBlank(taskQueryEntity.getBussinessType()))){
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
        query.processVariableValueEquals("businessType", taskQueryEntity.getBussinessType());

        if(StringUtils.isNotBlank(taskQueryEntity.getModelKey())) {
            Model model = repositoryService.createModelQuery().modelKey(taskQueryEntity.getModelKey()).singleResult();
            query.deploymentId(model.getDeploymentId());
        }else if(StringUtils.isNotBlank(taskQueryEntity.getBussinessType())){
            List<String> keys=getProcessKeyByBussnessType(taskQueryEntity.getBussinessType());
            query.processDefinitionKeyIn(keys);
        }
        return query;

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

            List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();


            //高亮环节id集合
            List<String> highLightedActivitis = new ArrayList<String>();



            //高亮线路id集合
            List<String> highLightedFlows = getHighLightedFlows(definitionEntity,highLightedActivitList);

            for(HistoricActivityInstance tempActivity : highLightedActivitList){
                String activityId = tempActivity.getActivityId();
                highLightedActivitis.add(activityId);
            }
//生成流图片  5.18.0
            InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "PNG", highLightedActivitis, highLightedFlows,
                    processEngineConfiguration.getLabelFontName(),
                    processEngineConfiguration.getActivityFontName(),
                    processEngineConfiguration.getProcessEngineConfiguration().getClassLoader(), 1.0);
            //中文显示的是口口口，设置字体就好了
            //5.22.0
           // InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,highLightedFlows,"宋体","宋体","宋体",null,1.0);
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
     * 根据流程实例ID查询历史任务信息
     * @param processInstanceId
     * @param variableNames
     * @return
     */
    @Override
    public HistoryTasksVo getTaskHistoryByProcessInstanceId(String processInstanceId, List<String> variableNames){
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

        List<HistoryTaskVo> taskList = new ArrayList<HistoryTaskVo>();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        int isFinished = (pi == null)?1:0;
        for (HistoricTaskInstance hti : list) {

            HistoryTaskVo ht = new HistoryTaskVo();

            ht.setTaskId(hti.getId());
            //审核人

            ht.setOperator(hti.getAssignee());
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
        String sql = "SELECT arm.* FROM `ACT_RE_MODEL` AS arm,`t_app_model` AS tam,`t_app` AS ta WHERE ta.KEY='"+appKey+"' AND ta.KEY=tam.APP_KEY AND arm.KEY_=tam.MODEL_KEY ";
        return repositoryService.createNativeModelQuery().sql(sql).list();
    }

    /**
     * 委派任务
     * @author houjinrong
     * @param userId 当前任务节点ID
     * @param taskId 被委派人工号
     * @return
     */
    @Override
    public boolean delegateTask(String userId, String taskId){
        try{
            taskService.delegateTask(taskId, userId);
        } catch (Exception e){
            logger.error("委派任务失败",e);
            return false;
        }
        return true;
    }

    /**
     * 转办任务
     * @author houjinrong
     * @param userId 当前任务节点ID
     * @param taskId 被转办人工号
     * @return
     */
    @Override
    public boolean transferTask(String taskId, String userId, String transferUserId){
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if(task == null){
                throw new ActivitiObjectNotFoundException("任务不存在！", this.getClass());
            }

            String taskType = taskService.getVariable(taskId, task.getTaskDefinitionKey()+":"+TaskVariable.TASKTYPE.value)+"";
            if(TaskType.COUNTERSIGN.value.equals(taskType) || TaskType.CANDIDATEUSER.value.equals(taskType)){
                //会签
                //修改会签人
                String candidateIds = task.getAssignee();
                if(StringUtils.contains(candidateIds, transferUserId)){
                    throw new ActivitiObjectNotFoundException("【"+transferUserId+"】已在当前任务中（同一任务节点同一个人最多可办理一次）", this.getClass());
                }
                candidateIds = candidateIds.replace(userId,transferUserId);
                taskService.setAssignee(task.getId(),candidateIds);
                //修改会签人相关属性值
                Map<String,Object> variable = Maps.newHashMap();
                variable.put(task.getTaskDefinitionKey() + ":" + userId, TaskStatus.TRANSFER.value);
                variable.put(task.getTaskDefinitionKey() + ":" + transferUserId, transferUserId+":"+TaskStatus.UNFINISHED.value);
                variable.put(task.getTaskDefinitionKey() + ":"+TaskVariable.TASKUSER.value, candidateIds);
                taskService.setVariablesLocal(taskId, variable);
            }else{
                Map<String,Object> variable = Maps.newHashMap();
                variable.put(task.getTaskDefinitionKey() + ":" + userId, TaskStatus.TRANSFER.value);
                variable.put(task.getTaskDefinitionKey() + ":" + transferUserId, transferUserId+":"+TaskStatus.UNFINISHED.value);
                variable.put(task.getTaskDefinitionKey() + ":"+TaskVariable.TASKUSER.value, transferUserId);
                taskService.setVariablesLocal(taskId, variable);

                String assign = task.getAssignee();
                taskService.setAssignee(taskId, transferUserId);
                if(StringUtils.isNotBlank(assign)) {
                    taskService.setOwner(taskId, assign);
                }
            }

        } catch (ActivitiObjectNotFoundException e){
            logger.error("转办任务失败，此任务不存在！",e);
            return false;
        } catch (Exception e) {
            logger.error("委派任务失败，系统错误！",e);
            return false;
        }
        return true;
    }

    @Override
    public String getLastApprover(String processId) {
       Object lastApprover= runtimeService.getVariable(processId,processId+":"+ TaskVariable.LASTTASKUSER.value);
       if(lastApprover==null){
           return  null;
       }else {
           return String.valueOf(lastApprover);
       }
    }

    @Override
    public Comment selectComment(String taskid, String userName) {
        List<Comment> list= taskService.getTaskComments(taskid,"2");
        List<Comment> list1=taskService.getTaskComments(taskid,"3");
        list.addAll(list1);
        if(list==null||list.size()==0){
            return null;
        }
        for(Comment comment:list){
            if(comment.getUserId().equals(userName)){
                return comment;
            }
        }
        return null;
    }

    /**
     * 任务跳转
     * @param taskId 当前任务ID
     * @param taskDefinitionKey 跳转到的任务节点KEY
     * @return 任务ID
     * @author houjinrong@chtwm.com
     * date 2018/2/1 20:32
     */
    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public String taskJump(String taskId, String taskDefinitionKey, String userCodes) throws WorkFlowException{
        if(StringUtils.isBlank(taskId) || StringUtils.isBlank(taskDefinitionKey)){
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"非法的参数，未传入必须的参数");
        }

        TaskEntity currentTaskEntity = (TaskEntity) this.taskService.createTaskQuery().taskId(taskId).singleResult();

        if(currentTaskEntity != null){
            ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl)this.repositoryService)
                    .getDeployedProcessDefinition(currentTaskEntity.getProcessDefinitionId());
            ActivityImpl activity = (ActivityImpl) pde.findActivity(taskDefinitionKey);

            Command<Void> deleteCmd = new DeleteActiveTaskCmd(currentTaskEntity, "jump", true);
            Command<Void> StartCmd = new StartActivityCmd(currentTaskEntity.getExecutionId(), activity);
            managementService.executeCommand(deleteCmd);
            managementService.executeCommand(StartCmd);
            ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey(pde.getKey()).singleResult();

            Task task = taskService.createTaskQuery().processInstanceId(currentTaskEntity.getProcessInstanceId()).singleResult();

            int version = (int)runtimeService.getVariable(task.getProcessInstanceId(),"version");

            Map<String,String> mailParam = Maps.newHashMap();
            mailParam.put("applyUserName",taskService.getVariable(task.getId(),"applyUserName")+"");
            mailParam.put("ApplyTitle",taskService.getVariable(task.getId(),"ApplyTitle")+"");
            initTaskVariable(task.getProcessInstanceId(),processDefinition.getKey(),version,mailParam);

            String assign = currentTaskEntity.getAssignee();
            if(StringUtils.isNotBlank(assign)){
                taskService.setOwner(task.getId(), assign);
            }

            //动态审批设置审批人
            if(StringUtils.isNotBlank(userCodes)){
                setApprove(currentTaskEntity.getProcessInstanceId(), userCodes);
            }
            return task.getId();
        }else{
            throw new ActivitiObjectNotFoundException("任务不存在！", this.getClass());
        }
    }

    /**
     * 初始化任务属性值
     * @param processInstanceId 流程实例ID
     * @param processDefinitionKey 流程定义KEY
     * @param version 版本号
     * @param mailParam
     * @throws WorkFlowException
     */
    private void initTaskVariable(String processInstanceId, String processDefinitionKey, int version, Map<String,String> mailParam) throws WorkFlowException{
        EntityWrapper<TUserTask> wrapper =new EntityWrapper<>();
        wrapper.where("proc_def_key= {0}",processDefinitionKey).andNew("version_={0}",version);
        List<TUserTask> tUserTasks=tUserTaskService.selectList(wrapper);
        //为任务设置审批人
        List<Task> tasks=taskService.createTaskQuery().processInstanceId(processInstanceId).list();

        if(tUserTasks==null){
            throw new WorkFlowException(CodeConts.WORK_FLOW_NO_APPROVER,"操作失败，请在工作流管理平台设置审批人后在创建任务");
        }

        for(Task task:tasks){
            if(org.apache.commons.lang3.StringUtils.isNotBlank(task.getAssignee())){
                continue;
            }
            for(TUserTask tUserTask:tUserTasks){
                if(org.apache.commons.lang3.StringUtils.isBlank(tUserTask.getCandidateIds())){
                    throw  new WorkFlowException(CodeConts.WORK_FLOW_NO_APPROVER,"操作失败，请在工作流管理平台将任务节点：'"+tUserTask.getTaskName()+"'设置审批人后在创建任务");
                }
                if(task.getTaskDefinitionKey().trim().equals(tUserTask.getTaskDefKey().trim())){
                    String candidateIds = tUserTask.getCandidateIds();

                    Map<String,Object> variable = Maps.newHashMap();
                    variable.put(tUserTask.getTaskDefKey()+":"+TaskVariable.TASKTYPE.value,tUserTask.getTaskType());
                    variable.put(tUserTask.getTaskDefKey()+":"+TaskVariable.TASKUSER.value,candidateIds);

                    if (TaskType.CANDIDATEGROUP.value.equals(tUserTask.getTaskType())) {
                        //组
                        taskService.addCandidateGroup(task.getId(), tUserTask.getCandidateIds());
                    } else if (TaskType.CANDIDATEUSER.value.equals(tUserTask.getTaskType())) {
                        //候选人
                        for(String candidateId : candidateIds.split(",")){
                            variable.put(tUserTask.getTaskDefKey()+":"+candidateId,candidateId+":"+TaskStatus.UNFINISHED.value);
                        }

                        variable.put(tUserTask.getTaskDefKey()+":"+TaskVariable.TASKTYPE.value,tUserTask.getTaskType());
                        variable.put(tUserTask.getTaskDefKey()+":"+TaskVariable.TASKUSER.value,candidateIds);
                    } else if(TaskType.COUNTERSIGN.value.equals(tUserTask.getTaskType())){
                        /**
                         * 为当前任务设置属性值
                         * 把审核人信息放入属性表，多个审核人（会签/候选）多条记录
                         */
                        for(String candidateId : candidateIds.split(",")){
                            variable.put(tUserTask.getTaskDefKey()+":"+candidateId,candidateId+":"+TaskStatus.UNFINISHED.value);
                        }
                        variable.put(tUserTask.getTaskDefKey()+":"+TaskVariable.USERCOUNTTOTAL.value,tUserTask.getUserCountTotal());
                        variable.put(tUserTask.getTaskDefKey()+":"+TaskVariable.USERCOUNTNEED.value,tUserTask.getUserCountNeed());
                        variable.put(tUserTask.getTaskDefKey()+":"+TaskVariable.USERCOUNTNOW.value,0);
                        variable.put(tUserTask.getTaskDefKey()+":"+TaskVariable.USERCOUNTREFUSE.value,0);
                    }else{
                        variable.put(tUserTask.getTaskDefKey()+":"+candidateIds,candidateIds+":"+TaskStatus.UNFINISHED.value);
                    }
                    taskService.setVariablesLocal(task.getId(),variable);
                    taskService.setAssignee(task.getId(), tUserTask.getCandidateIds());
                    break;
                }
                Boolean needMail = Boolean.valueOf(ConfigUtil.getValue("isSendMail"));
                if(needMail){
                    sendEmail(tUserTask.getCandidateIds(),mailParam.get("applyUserName"),mailParam.get("applyTitle"));
                }
            }
        }
    }

    /**
     * 删除一个流程实例
     * @param processInstanceId 流程实例ID
     * @param description 删除原因
     * @author houjinrong@chtwm.com
     * @return
     */
    @Override
    public boolean deleteProcessInstance(String processInstanceId, String description){
        try {
            runtimeService.deleteProcessInstance(processInstanceId,description);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 流程驳回
     * @param processInstanceId 流程实例ID
     * @return true：成功；false：失败
     * @author houjinrong@chtwm.com
     * date 2018/2/7 15:35
     */
    @Override
    public boolean rollBackWorkFlow(String processInstanceId){
        try {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            getImmutableField(CommonVo.class);
        } catch (Exception e) {
            logger.info("任务驳回失败",e);
            return false;
        }

        return true;
    }

    /**
     * 恢复驳回的流程
     * @param processInstanceId 流程实例ID
     * @param resumeType 0：恢复到开始任务节点；1：恢复到驳回前到达的任务节点
     * @return 任务ID
     * @author houjinrong@chtwm.com
     * date 2018/2/7 15:36
     */
    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public String resumeWorkFlow(String processInstanceId, int resumeType, Map<String,Object> variables, String userCodes) throws WorkFlowException{
        if(StringUtils.isBlank(processInstanceId)){
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"流程实例ID不能为空");
        }

        //校验属性是否跟系统属性重复
        if(variables != null){
            validateVariables(variables);
        }

        //激活挂起的流程实例
        runtimeService.activateProcessInstanceById(processInstanceId);

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if(task == null){
            throw new WorkFlowException(CodeConts.PROCESS_NOEXISTS,"任务流程异常，找不到被驳回的任务节点");
        }
        String taskDefinitionKey = task.getTaskDefinitionKey();
        if(resumeType == 0){
            Object taskDefinitionKeys = runtimeService.getVariable(processInstanceId, ProcessVariable.PROCESSNODE.value + processInstanceId);
            if(taskDefinitionKeys == null){
                throw new WorkFlowException(CodeConts.PROCESS_ERROR,"流程定义ID【"+processInstanceId+"】对应的节点不存在");
            }
            taskDefinitionKey = taskDefinitionKeys.toString().split(",")[0];
        }else if(resumeType == 1){
            //do nothing
        }else{
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"参数不合法，有效参数只能为0或1");
        }
        String taskId = taskJump(task.getId(), taskDefinitionKey, userCodes);
        if(variables != null && variables.size() > 0){
            runtimeService.setVariables(processInstanceId,variables);
        }

        return taskId;
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

            //通过部署id查询流程定义
            ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionKey(modelKey).latestVersion().singleResult();

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
     * 任务设置审核人侯发送邮件通知
     * @param assignee 审核人，多个用逗号隔开
     * @param applyUserName 应用用户名
     * @param title 标题
     */
    private void sendEmail(String assignee,Object applyUserName,Object title){
        String[] strs = assignee.split(",");
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
                            applyUserName + "提交了一个标题为【"+title+"】审批申请，请到<a href='http://core.chtwm.com/login.html'>综合业务平台系统</a>中进行审批!");
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;

                }
            }
        }
    }

    /**
     * 通过流程定义ID获取流程节点ID，多个逗号隔开
     * @param processDefinitionId 流程定义ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/2/7 16:59
     */
    private String getProcessDefinitionNodeIds(String processDefinitionId){
        String processDefinitionKeys = "";
        BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
        if(model != null) {
            Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
            for(FlowElement fe : flowElements) {
                if(fe instanceof UserTask){
                    processDefinitionKeys = "".equals(processDefinitionKeys)?fe.getId():processDefinitionKeys+","+fe.getId();
                }
            }
        }

        return processDefinitionKeys;
    }

    /**
     * 校验属性值
     * @param variables 属性值
     * @author houjinrong@chtwm.com
     * date 2018/2/8 13:49
     */
    private void validateVariables(Map<String,Object> variables) throws WorkFlowException{
        Set<String> fieldSet = getImmutableField(CommonVo.class);
        String repeatField = null;
        for(String field : fieldSet){
            if(variables.containsKey(field)){
                repeatField = repeatField == null?field:repeatField+","+field;
            }
        }
        if(repeatField != null){
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_REPART,"不可覆盖系统属性【"+repeatField+"】");
        }
    }

    /**
     * 获取勒种所有属性
     * @param clazz 类名
     * @return 属性名用逗号隔开
     * @author houjinrong@chtwm.com
     * date 2018/2/8 13:49
     */
    private <E> Set<String> getImmutableField(Class<E> clazz){
        Set<String> fieldSet = Sets.newHashSet();
        for (Field field : clazz.getDeclaredFields()) {
            fieldSet.add(field.getName());
        }
        return fieldSet;
    }
}
