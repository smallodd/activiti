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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @author houjinrong@chtwm.com
 * 2018/2/11 15:08
 */
public class WorkTaskV2ServiceImpl implements WorkTaskV2Service {

    private Logger logger = LoggerFactory.getLogger(WorkTaskV2Service.class);

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
        logger.info("开启任务{}", "startTask");
        logger.info("入参 commonVo：{}paramMap：{}",commonVo.toString(),JSONObject.toJSONString(paramMap));

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
    public boolean setApprove(String processInstanceId,String userCodes) throws WorkFlowException{
        if(StringUtils.isBlank(processInstanceId) || StringUtils.isBlank(userCodes)){
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"参数【processInstanceId】【userCodes】都不可为空!");
        }
        String[] array = userCodes.split(",");
        Set<String> set = new HashSet<String>(Arrays.asList(array));
        if(array.length-set.size() != 0){
            throw new WorkFlowException("审批人设置重复");
        }
        Task task=taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if(task==null){
            throw new WorkFlowException(CodeConts.WORK_FLOW_TASK_IS_NULL,"任务为空设置失败!");
        }
        if(StringUtils.isNotBlank(task.getAssignee())){
            logger.info("任务："+processInstanceId+"已经设置过审批人，请不要重复设置，当前审批人为："+task.getAssignee());
            return false;
        }
        taskService.setAssignee(task.getId(),userCodes);
        Map result=new HashMap();
        for(String candidateId : userCodes.split(",")){
            result.put(task.getTaskDefinitionKey()+":"+candidateId,candidateId+":"+TaskStatus.UNFINISHED.value);
        }
        result.put(task.getTaskDefinitionKey()+":"+TaskVariable.TASKTYPE.value,TaskType.CANDIDATEUSER.value);
        result.put(task.getTaskDefinitionKey()+":"+TaskVariable.TASKUSER.value,userCodes);

        logger.info("返回值：{}", true);
        taskService.setVariablesLocal(task.getId(),result);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String completeTask( ApproveVo approveVo,Map<String,Object> paramMap) throws WorkFlowException{
        logger.info("办理任务{}", "completeTask");
        logger.info("入参 approveVo：{}paramMap：{}",approveVo,paramMap);

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

        Map map=taskService.getVariables(taskId);
        boolean dynamic = (boolean)map.get("dynamic");
        //动态处理审批
        if(dynamic){
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
                String userCount = (String)map.get(task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNT.value);
                if(StringUtils.isBlank(userCount)){
                    throw new WorkFlowException("","会签任务【"+taskId+"】数据不完整，缺少属性"+TaskVariable.USERCOUNT.value);
                }
                JSONObject userCountJson = JSONObject.parseObject(userCount);
                userCountNow = userCountJson.getInteger(TaskVariable.USERCOUNTNOW.value);
                int userCountTotal = userCountJson.getInteger(TaskVariable.USERCOUNTTOTAL.value);
                int userCountNeed = userCountJson.getInteger(TaskVariable.USERCOUNTNEED.value);
                int userCountRefuse = userCountJson.getInteger(TaskVariable.USERCOUNTREFUSE.value);

                String taskResult = TaskStatus.FINISHEDPASS.value;
                if(ConstantUtils.vacationStatus.NOT_PASSED.getValue().equals(commentResult)){
                    taskResult = TaskStatus.FINISHEDREFUSE.value;
                    userCountRefuse++;
                }

                Map<String,Object> variables = Maps.newHashMap();
                variables.put(task.getTaskDefinitionKey()+":"+currentUser,currentUser+":"+taskResult);
                userCountJson.put(TaskVariable.USERCOUNTNOW.value,++userCountNow);
                userCountJson.put(TaskVariable.USERCOUNTREFUSE.value,userCountRefuse);
                variables.put(task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNT.value,userCountJson.toJSONString());
                taskService.setVariablesLocal(taskId,variables);

                int userCountAgree = userCountNow - userCountRefuse;
                if(userCountAgree >= userCountNeed){
                    //------------任务完成-通过------------
                    commentResult = ConstantUtils.vacationStatus.PASSED.getValue();
                }else{
                    if(userCountTotal - userCountNow + userCountAgree < userCountNeed){
                        //------------任务完成-未通过------------
                        commentResult = ConstantUtils.vacationStatus.NOT_PASSED.getValue();
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
        if(ConstantUtils.vacationStatus.PASSED.getValue().equals(commentResult)){
            //do nothing
        }else if(ConstantUtils.vacationStatus.NOT_PASSED.getValue().equals(commentResult)){
            runtimeService.deleteProcessInstance(processInstanceId,"refuse");
            return processInstanceId;
        }else{
            throw  new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"参数不合法，commentResult 请传 2 审批通过 或 3 审批拒绝");
        }

        // 完成委派任务
        if(DelegationState.PENDING == task.getDelegationState()){
            this.taskService.resolveTask(taskId);
            return processInstanceId;
        }
        //完成任务
        taskService.complete(task.getId());
        initTaskVariable(task.getProcessInstanceId(),processInstance.getProcessDefinitionKey(),version,map);

        logger.info("返回值：任务实例ID{}", processInstanceId);
        return processInstanceId;
    }

    @Override
    public PageInfo<Task> queryTaskByAssign(String userId,int startPage,int pageSize,TaskQueryEntity taskQueryEntity) throws WorkFlowException {
        logger.info("----------------通过用户相关信息查询待审批任务开始----------------");
        logger.info("入参 userId：{}，startPage：{}，pageSize：{}，taskQueryEntity：{}",userId,startPage,pageSize,JSONObject.toJSONString(taskQueryEntity));
        PageInfo<Task> pageInfo = new PageInfo<>();
        long count = 0;

        if(StringUtils.isNotBlank(userId)){
            List<Task> taskList =createTaskQuqery(taskQueryEntity).taskVariableValueEquals(userId+":"+TaskStatus.UNFINISHED.value).taskAssigneeLike("%"+userId+"%").active().listPage((startPage-1)*pageSize,pageSize);
            pageInfo.setList(taskList);
            pageInfo.setTotal(count);
        }else{
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"userId为空");
        }
        logger.info("----------------通过用户相关信息查询待审批任务结束----------------");
        return pageInfo;
    }


    /**
     * 获取申请人提交的任务
     * @param userId  申请人信息
     * @param startPage  起始页数
     * @param pageSize    每页显示数
     * @param status      0 :审批中的任务
     *                    1 :审批完成的任务
     * @return
     */
    @Override
    public List<HistoricProcessInstance> getApplyTasks(String userId,int startPage,int pageSize,int status,TaskQueryEntity taskQueryEntity){
        logger.info("--------------------获取申请人提交的任务开始,入参 userId：{}，pageSzie：{}，status：{}，taskQueryEntity：{}----------------",userId,startPage,pageSize,status,JSONObject.toJSONString(taskQueryEntity));

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
        List<HistoricProcessInstance> list= query.startedBy(userId).listPage((startPage-1)*pageSize,pageSize);
        logger.info("----------------获取申请人提交的任务结束,返回值{}----------------",JSONObject.toJSONString(list));

        return list;
    }

    @Override
    public PageInfo<HistoricTaskInstance> selectMyComplete(String userId,int startPage,int pageSize,TaskQueryEntity taskQueryEntity){
        logger.info("----------------查询用户历史审批过的任务开始,入参 userId：{}，startPage：{}，pageSize：{}，taskQueryEntity：{}----------------",userId,startPage,pageSize,JSONObject.toJSONString(taskQueryEntity));

        PageInfo<HistoricTaskInstance> pageInfo=new PageInfo<HistoricTaskInstance>();
        HistoricTaskInstanceQuery query= createHistoricTaskInstanceQuery(taskQueryEntity);
        List<HistoricTaskInstance> list= query.taskAssigneeLike("%"+userId+"%").taskVariableValueEquals(TaskStatus.FINISHED.value+":"+userId,TaskStatus.FINISHED.value).orderByHistoricTaskInstanceEndTime().desc().listPage((startPage-1)*pageSize,pageSize);
        long count=query.taskAssigneeLike("%"+userId+"%").taskVariableValueEquals(TaskStatus.FINISHED.value+":"+userId,TaskStatus.FINISHED.value).count();
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        logger.info("----------------查询用户历史审批过的任务结束，返回值：{}----------------",JSONObject.toJSONString(pageInfo));
        return pageInfo;
    }

    @Override
    public PageInfo<HistoricTaskInstance> selectMyRefuse(String userId, int startPage, int pageSize,TaskQueryEntity taskQueryEntity) {
        logger.info("----------------查询用户审批拒绝的信息列表开始,入参 userId：{}，startPage：{}，pageSize：{}，taskQueryEntity：{}----------------",userId,startPage,pageSize,JSONObject.toJSONString(taskQueryEntity));

        PageInfo<HistoricTaskInstance> pageInfo=new PageInfo<HistoricTaskInstance>();
        HistoricTaskInstanceQuery query=createHistoricTaskInstanceQuery(taskQueryEntity);
        List<HistoricTaskInstance> list= query.taskAssigneeLike("%"+userId+"%").taskVariableValueEquals(userId+":"+TaskStatus.FINISHEDREFUSE.value).listPage((startPage-1)*pageSize,pageSize);
        query=createHistoricTaskInstanceQuery(taskQueryEntity);
        long count=query.taskAssigneeLike("%"+userId+"%").taskVariableValueEquals(userId+":"+TaskStatus.FINISHEDREFUSE.value).count();
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        logger.info("----------------查询用户审批拒绝的信息列表结束，返回值：{}----------------",JSONObject.toJSONString(pageInfo));
        return pageInfo;
    }


    @Override
    public boolean checkBusinessKeyIsInFlow(TaskQueryEntity taskQueryEntity, String businessKey) {
        logger.info("----------------查询业务主键是否再流程中开始,入参 taskQueryEntity：{}，businessKey：{}----------------",JSONObject.toJSONString(taskQueryEntity),businessKey);
        if((StringUtils.isBlank(taskQueryEntity.getBussinessType())||StringUtils.isBlank(taskQueryEntity.getModelKey()))){
            throw new RuntimeException("参数不合法,业务系统key和modelKey 必须都传:"+taskQueryEntity.toString());
        }

        List<Task> tasks=createTaskQuqery(taskQueryEntity).processInstanceBusinessKey(businessKey).orderByTaskCreateTime().desc().listPage(0,1);
        if(tasks!=null&&tasks.size()>0&&tasks.get(0)!=null){
            return  true;
        }

        logger.info("----------------查询业务主键是否再流程中结束----------------");
        return false;
    }


    @Override
    public List<Comment> selectCommentList(String processInstanceId){
        logger.info("----------------查询审批意见列表开始,入参 processInstanceId：{}----------------",processInstanceId);
        if(StringUtils.isBlank(processInstanceId)){
            return null;
        }
        List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
        logger.info("----------------查询审批意见列表结束,返回值：{}----------------",JSONObject.toJSONString(commentList));
        return commentList;
    }

    @Override
    public Map<String, Object> getVariables(String processInstanceId){
        logger.info("----------------通过流程实例ID获取属性值开始,入参 processInstanceId：{}----------------",processInstanceId);
        if(StringUtils.isBlank(processInstanceId)){
            return null;
        }
        List<HistoricVariableInstance> list=historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
        Map<String,Object> map=new HashMap<>();
        for(HistoricVariableInstance historicVariableInstance:list){
            map.put(historicVariableInstance.getVariableName(),historicVariableInstance.getValue());
        }
        logger.info("----------------通过流程实例ID获取属性值结束,返回值：{}----------------",JSONObject.toJSONString(map));
        return map;
    }

    /**
     * 流程任务跟踪标识
     * @param processInstanceId
     * @return
     */
    @Override
    public byte[] getTaskSchedule(String processInstanceId){
        logger.info("----------------获取流程跟踪图开始,入参 processInstanceId：{}----------------",processInstanceId);
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
            //InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,highLightedFlows,"宋体","宋体","宋体",null,1.0);
            //单独返回流程图，不高亮显示
            //InputStream imageStream = diagramGenerator.generatePngDiagram(bpmnModel);
            //输出资源内容到相应对象
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
        logger.info("----------------获取流程跟踪图开始结束----------------",processInstanceId);
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
        logger.info("----------------获取历史任务开始,入参 processInstanceId：{}，variableNames：{}----------------",processInstanceId,variableNames);
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

        logger.info("----------------获取历史任务结束,返回值{}----------------",JSONObject.toJSONString(hisTask));
        return hisTask;
    }

    /**
     * 获取应用列表
     * @return
     */
    @Override
    public List<App> getAppList(){
        logger.info("----------------获取应用列表开始----------------");
        EntityWrapper<App> wrapper = new EntityWrapper<App>();
        wrapper.where("status",1);
        List<App> appList = appService.selectList(wrapper);
        logger.info("----------------获取应用列表结束,返回值{}----------------",JSONObject.toJSONString(appList));
        return appList;
    }

    /**
     * 根据应用key获取应用所属的模型列表
     * @param appKey
     * @return
     */
    @Override
    public List<Model> getModelListByAppKey(String appKey){
        logger.info("----------------通过appKey获取模型列表开始，入参 appKey：{}----------------",appKey);
        if(StringUtils.isBlank(appKey)){
            return null;
        }
        String sql = "SELECT arm.* FROM `ACT_RE_MODEL` AS arm,`t_app_model` AS tam,`t_app` AS ta WHERE ta.KEY='"+appKey+"' AND ta.KEY=tam.APP_KEY AND arm.KEY_=tam.MODEL_KEY ";
        List<Model> modelList = repositoryService.createNativeModelQuery().sql(sql).list();
        logger.info("----------------通过appKey获取模型列表结束，返回值{}----------------",JSONObject.toJSONString(modelList));
        return modelList;
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

    @Override
    public boolean transferTask(String processInstanceId, String userId, String transferUserId) throws WorkFlowException{
        logger.info("----------------任务转办开始，入参 taskId：{}，userId：{}，transferUserId：{}----------------",processInstanceId,userId,transferUserId);
        if(StringUtils.isBlank(processInstanceId) || StringUtils.isBlank(userId) || StringUtils.isBlank(transferUserId)){
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"参数【processInstanceId】【String userId】【 String transferUserId】都不可为空");
        }
        boolean result = true;
        try {
            Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
            if(task == null){
                throw new ActivitiObjectNotFoundException("任务不存在！", this.getClass());
            }

            if(!StringUtils.contains(task.getAssignee(),userId)){
                throw new WorkFlowException("当前任务的审批人【"+userId+"】不存在");
            }

            String taskId = task.getId();
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
                variable.put(task.getTaskDefinitionKey() + ":" + userId, userId+":"+TaskStatus.TRANSFER.value);
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
            result = false;
        } catch (Exception e) {
            logger.error("委派任务失败，系统错误！",e);
            result = false;
        }
        logger.info("----------------任务转办结束,返回值{}----------------",result);
        return result;
    }

    @Override
    public String getLastApprover(String processInstanceId) {
        logger.info("----------------获取最后审批人开始,入参 processInstanceId：{}----------------",processInstanceId);
        String result = null;
        HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).variableName(processInstanceId + ":" + TaskVariable.LASTTASKUSER.value).singleResult();
        //Object lastApprover= runtimeService.getVariable(processInstanceId,processInstanceId+":"+ TaskVariable.LASTTASKUSER.value);
        if(historicVariableInstance==null){
            result = null;
        }else {
            result = (String)historicVariableInstance.getValue();
        }
        logger.info("----------------获取最后审批人结束,返回值{}----------------",result);
        return result;
    }

    /**
     * 任务跳转
     * @param processInstanceId 流程实例ID
     * @param taskDefinitionKey 跳转到的任务节点KEY
     * @return 流程实例ID
     * @author houjinrong@chtwm.com
     * date 2018/2/1 20:32
     */
    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public String taskJump(String processInstanceId, String taskDefinitionKey, String userCodes) throws WorkFlowException{
        logger.info("----------------任务跳转开始,入参 taskId：{}，taskDefinitionKey：{}，userCodes：{}----------------",processInstanceId,taskDefinitionKey,userCodes);
        if(StringUtils.isBlank(processInstanceId) || StringUtils.isBlank(taskDefinitionKey)){
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"非法的参数，未传入必须的参数");
        }

        TaskEntity currentTaskEntity = (TaskEntity) this.taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();

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

            String assign = currentTaskEntity.getAssignee();
            if(StringUtils.isNotBlank(assign)){
                taskService.setOwner(task.getId(), assign);
            }

            boolean dynamic = (boolean)runtimeService.getVariable(task.getProcessInstanceId(),"dynamic");
            if(dynamic && StringUtils.isNotBlank(userCodes)){
                //动态审批设置审批人
                setApprove(currentTaskEntity.getProcessInstanceId(), userCodes);
            }else{
                //非动态审批
                initTaskVariable(task.getProcessInstanceId(),processDefinition.getKey(),version,mailParam);
            }
            logger.info("----------------任务跳转结束，返回值：{}----------------",task.getProcessInstanceId());
            return task.getProcessInstanceId();
        }else{
            throw new ActivitiObjectNotFoundException("任务不存在！", this.getClass());
        }
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
            throw new WorkFlowException(CodeConts.WORK_FLOW_NO_APPROVER,"操作失败，请在工作流管理平台设置审批人后再创建任务");
        }

        for(Task task:tasks){
            if(StringUtils.isNotBlank(task.getAssignee())){
                continue;
            }
            for(TUserTask tUserTask:tUserTasks){
                if(StringUtils.isBlank(tUserTask.getCandidateIds())){
                    throw  new WorkFlowException(CodeConts.WORK_FLOW_NO_APPROVER,"操作失败，请在工作流管理平台将任务节点：'"+tUserTask.getTaskName()+"'设置审批人后再创建任务");
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

                        JSONObject json = new JSONObject();
                        json.put(TaskVariable.USERCOUNTTOTAL.value,tUserTask.getUserCountTotal());
                        json.put(TaskVariable.USERCOUNTNEED.value,tUserTask.getUserCountNeed());
                        json.put(TaskVariable.USERCOUNTNOW.value,0);
                        json.put(TaskVariable.USERCOUNTREFUSE.value,0);
                        variable.put(tUserTask.getTaskDefKey()+":"+TaskVariable.USERCOUNT.value,json.toJSONString());
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
            logger.info("删除流程实力失败",e);
            return false;
        }
    }

    /**
     * 流程驳回
     * @param processInstanceId 流程实例ID
     * @param rollBackType 0：恢复到开始任务节点；1：恢复到上个任务节点
     * @return true：成功；false：失败
     * @author houjinrong@chtwm.com
     * date 2018/2/7 15:35
     */
    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean rollBackProcess(String processInstanceId, int rollBackType) throws WorkFlowException{
        if(StringUtils.isBlank(processInstanceId)){
            throw new WorkFlowException("参数【processInstanceId】不能为空");
        }

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if(task == null){
            throw new WorkFlowException("任务流程异常，找不到被驳回的任务节点");
        }

        Object taskDefinitionKeys = runtimeService.getVariable(processInstanceId, ProcessVariable.PROCESSNODE.value + processInstanceId);
        if(taskDefinitionKeys == null){
            throw new WorkFlowException("流程实例ID【"+processInstanceId+"】对应的任务节点KEY不存在");
        }
        String taskDefinitionKey = taskDefinitionKeys.toString().split(",")[0];
        if(rollBackType == 0){
            runtimeService.suspendProcessInstanceById(processInstanceId);
        }else if(rollBackType == 1){
            //校验属性是否跟系统属性重复
            for(String s : taskDefinitionKeys.toString().split(",")){
                if(s.equals(task.getTaskDefinitionKey())){
                    break;
                }
                taskDefinitionKey = s;
            }
        }else{
            throw new WorkFlowException("参数不合法，有效参数type只能为0或1");
        }

        HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).variableName(taskDefinitionKey+":"+TaskVariable.TASKUSER.value).singleResult();

        String userCodes = (String)historicVariableInstance.getValue();
        taskJump(task.getProcessInstanceId(), taskDefinitionKey, userCodes);
        runtimeService.setVariable(processInstanceId, "rollBackType", rollBackType);
        return true;
    }

    /**
     * 恢复驳回的流程
     * @param processInstanceId 流程实例ID
     * @return 任务ID
     * @author houjinrong@chtwm.com
     * date 2018/2/7 15:36
     */
    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
    public boolean resumeProcess(String processInstanceId, String operator, Map<String,Object> variables) throws WorkFlowException{
        if(StringUtils.isBlank(processInstanceId) || StringUtils.isBlank(operator)){
            throw new WorkFlowException("参数非法，参数【processInstanceId】【operator】不能为空");
        }

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        if(task == null){
            throw new WorkFlowException("任务流程异常，找不到被驳回的任务节点");
        }

        //获取驳回流程标识，判断是否有权限操作
        Integer rollBackType = (Integer)runtimeService.getVariable(processInstanceId, "rollBackType");
        if(rollBackType == null){
            throw new WorkFlowException("流程实例processInstanceId没有需要恢复的流程");
        }

        if(rollBackType.equals(0)){
            String applyUserId = (String)runtimeService.getVariable(processInstanceId, "applyUserId");
            if(!operator.equals(applyUserId)){
                throw new WorkFlowException("【"+operator+"】没有权限进行该操作");
            }
        }if(rollBackType.equals(1)){
            if(!task.getAssignee().contains(operator)){
                throw new WorkFlowException("【"+operator+"】没有权限进行该操作");
            }
        }

        //删除驳回流程标识
        runtimeService.removeVariable(processInstanceId, "rollBackType");

        //校验属性是否跟系统属性重复
        if(variables != null && variables.size() > 0){
            validateVariables(variables);
        }

        //激活挂起的流程实例
        runtimeService.activateProcessInstanceById(processInstanceId);
        runtimeService.setVariables(processInstanceId,variables);

        return true;
    }

    /**
     *
     * @param processInstanceId 流程实例ID
     * @return 当前任务审批人，多个逗号隔开
     * @author houjinrong@chtwm.com
     * date 2018/3/5 17:25
     */
    @Override
    public String getCurrentAssign(String processInstanceId){
        if(StringUtils.isBlank(processInstanceId)){
            return null;
        }
        return taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getAssignee();
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
     * @param taskId 任务ID
     * @return 任务实例
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
     * 获取类中所有属性
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
