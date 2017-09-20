package com.activiti.service.impl;

import com.activiti.expection.WorkFlowException;
import com.activiti.service.ProcessCoreService;
import com.activiti.service.WorkTaskService;
import com.github.pagehelper.PageInfo;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.activiti.common.CodeConts.WORK_FLOW_IS_NOT_FINISH;

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

    @Autowired
    ProcessCoreService processCoreService;

    @Autowired
    ProcessEngineConfiguration processEngineConfiguration;
    @Override
    public PageInfo<Task> queryByAssign(String userId,int startPage,int pageSize) {
        logger.info("------------------------通过用户相关信息查询待审批任务开始------------------------");
        TaskQuery  query= taskService.createTaskQuery();
        long count=0;
        if(StringUtils.isNotBlank(userId)){
            query=query.taskAssigneeLike("%"+userId+"%");
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
    public List<HistoricTaskInstance> queryHistoryList(String userId, int startPage, int pageSize) {

        return historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).listPage((startPage-1)*pageSize,(startPage-1)*pageSize+pageSize);
    }

    @Override
    public Boolean completeTask(String processInstanceId,String nextUserId, String note,String authName) throws WorkFlowException{
        logger.info("--------------------审批任务开始-----------------------");
        Task task=taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        //添加审批人
        Authentication.setAuthenticatedUserId(authName);
        //添加审批意见
        taskService.addComment(task.getId(),task.getProcessInstanceId(),note);


        if(StringUtils.isNotBlank(nextUserId)&&StringUtils.isNotBlank(this.getNextNode(processInstanceId))) {
            Map<String, Object> map = new HashMap<>();
            map.put("userCode",nextUserId);

            taskService.complete(task.getId(),map);


        }else {

            taskService.complete(task.getId());

        }
        logger.info("--------------------审批任务结束-----------------------");
        return true;


    }
    @Deprecated
    public boolean rollBack(String taskId,String note){

        try {
            Map<String, Object> variables;
            // 取得当前任务.当前任务节点
            HistoricTaskInstance currTask = historyService
                    .createHistoricTaskInstanceQuery().taskId(taskId)
                    .singleResult();
            // 取得流程实例，流程实例
            ProcessInstance instance = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(currTask.getProcessInstanceId())
                    .singleResult();
            if (instance == null) {
                return  false;
            }
            variables = instance.getProcessVariables();
            // 取得流程定义
            ProcessDefinitionEntity definition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                    .getDeployedProcessDefinition(currTask
                            .getProcessDefinitionId());
            if (definition == null) {
                return false;
            }
            // 取得上一步活动
            ActivityImpl currActivity = ((ProcessDefinitionImpl) definition)
                    .findActivity(currTask.getTaskDefinitionKey());

            //也就是节点间的连线
            List<PvmTransition> nextTransitionList = currActivity
                    .getIncomingTransitions();
            // 清除当前活动的出口
            List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
            //新建一个节点连线关系集合

            List<PvmTransition> pvmTransitionList = currActivity
                    .getOutgoingTransitions();
            //
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
                taskService.addComment(task.getId(),task.getProcessInstanceId(),note);
                historyService.deleteHistoricTaskInstance(task.getId());
            }
            // 恢复方向
            for (TransitionImpl transitionImpl : newTransitions) {
                currActivity.getOutgoingTransitions().remove(transitionImpl);
            }
            for (PvmTransition pvmTransition : oriPvmTransitionList) {
                pvmTransitionList.add(pvmTransition);
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public Boolean refuseTask(String processId, String reason) throws WorkFlowException{
        logger.info("---------------------------拒绝任务开始---------------------------");
        Task task=taskService.createTaskQuery().processInstanceId(processId).singleResult();
        taskService.addComment(task.getId(),processId,reason);
        runtimeService.deleteProcessInstance(task.getProcessInstanceId(),"refuse");
        logger.info("---------------------------拒绝任务结束---------------------------");
        return true;
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
    public List<HistoricProcessInstance> getApplyTasks(String userid,int startPage,int pageSzie,int status){
        logger.info("--------------------获取申请人提交的任务开始----------------");
        HistoricProcessInstanceQuery query=historyService.createHistoricProcessInstanceQuery();
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
     * 获取参与审批用户的审批历史信息
     * @param userid   审批人用户唯一标识
     * @param startPage    起始页数
     * @param pageSzie     每页显示数

     *
     *
     * @return
     */
    public List<HistoricProcessInstance> getInvolvedUserCompleteTasks(String userid,int startPage,int pageSzie){
        logger.info("---------------------获取参与审批用户的审批历史信息开始--------------");
        HistoricProcessInstanceQuery query=historyService.createHistoricProcessInstanceQuery();

        query.orderByProcessInstanceStartTime().desc();
        logger.info("---------------------获取参与审批用户的审批历史信息结束--------------");
        return  query.involvedUser(userid).listPage((startPage-1)*pageSzie,pageSzie);
    }

    public PageInfo<HistoricTaskInstance> selectMyComplete(String userId,int startPage,int pageSize){
        logger.info("-----------------------查询用户历史审批过的任务开始----------------");
        PageInfo<HistoricTaskInstance> pageInfo=new PageInfo<HistoricTaskInstance>();
        List<HistoricTaskInstance> list= historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished().orderByTaskDueDate().desc().listPage((startPage-1)*pageSize,pageSize);
        long count=historyService.createHistoricTaskInstanceQuery().taskAssigneeLike("%"+userId+"%").finished().count();
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        logger.info("-----------------------查询用户历史审批过的任务结束----------------");
        return pageInfo;
    }

    @Override
    public PageInfo<HistoricTaskInstance> selectMyRefuse(String userId, int startPage, int pageSize) {
        logger.info("----------------------查询用户审批拒绝的信息列表开始----------------");
        PageInfo<HistoricTaskInstance> pageInfo=new PageInfo<HistoricTaskInstance>();
        List<HistoricTaskInstance> list= historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).taskDeleteReason("refused").listPage((startPage-1)*pageSize,(startPage-1)*pageSize+pageSize);
        long count=historyService.createHistoricTaskInstanceQuery().taskAssigneeLike("%"+userId+"%").taskDeleteReason("refused").count();
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        logger.info("----------------------查询用户审批拒绝的信息列表结束----------------");
        return pageInfo;
    }

    /**
     *查询任务所属节点
     * @param processId  任务id
     * @return  返回图片流
     */
    public byte[] generateImage(String  processId){

        logger.info("-------------------------查询任务所属节点开始-------------------");
        //获取历史流程实例
        HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
        //流程定义
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());


        ProcessDiagramGenerator pdg = processEngineConfiguration.getProcessDiagramGenerator();

        List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).list();



        ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());


        //高亮环节id集合
        List<String> highLightedActivitis = new ArrayList<String>();
        //高亮线路id集合
        List<String> highLightedFlows = getHighLightedFlows(definitionEntity,highLightedActivitList);
        for(HistoricActivityInstance tempActivity : highLightedActivitList){
            String activityId = tempActivity.getActivityId();
            highLightedActivitis.add(activityId);
        }

        //生成流图片
        InputStream inputStream = pdg.generateDiagram(bpmnModel, "PNG", highLightedActivitis, highLightedFlows,
                processEngineConfiguration.getLabelFontName(),
                processEngineConfiguration.getActivityFontName(),
                processEngineConfiguration.getProcessEngineConfiguration().getClassLoader(), 1.0);
        try {
            logger.info("-------------------------查询任务所属节点结束-------------------");
            //生成本地图片
          /*  File file = new File("e:/test.png");
            FileUtils.copyInputStreamToFile(inputStream, file);*/
            return IOUtils.toByteArray(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("生成流程图异常！", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    public boolean checekBunessKeyIsInFlow(String bussinessKey) {
        Task task=taskService.createTaskQuery().processInstanceBusinessKey(bussinessKey).singleResult();
        if(task!=null){
            return  true;
        }
        return false;
    }

    private  List<String> getHighLightedFlows( ProcessDefinitionEntity processDefinitionEntity,
                                               List<HistoricActivityInstance> historicActivityInstances){
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
    @Override
    public Comment selectComment(String taskid){
      List<Comment> list= taskService.getTaskComments(taskid);
       if(list==null||list.size()==0){
           return null;
       }
        return list.get(0);
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
    /**
     * 获取当前流程的下一个节点
     * @param procInstanceId
     * @return
     */
    public String getNextNode(String procInstanceId){
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
    public PageInfo<Task> selectAllWaitApprove(int startPage, int pageSize) {
        logger.info("-------------查询所有待审批的任务开始--------------");
        PageInfo<Task> pageInfo=new PageInfo<>();

        List<Task> list= taskService.createTaskQuery().listPage((startPage-1)*pageSize,(startPage-1)*pageSize+pageSize);
        long count =taskService.createTaskQuery().count();
        pageInfo.setTotal(count);
        pageInfo.setList(list);
        logger.info("-------------查询所有待审批的任务结束--------------");
        return  pageInfo;
    }

    @Override
    public PageInfo<HistoricProcessInstance> selectAllPassApprove(int startPage, int pageSize) {
        logger.info("-------------查询所有通过的任务开始--------------");
        int startColum=(startPage-1)*pageSize;
        List<HistoricProcessInstance> list=historyService.createHistoricProcessInstanceQuery().finished().notDeleted().listPage(startColum,startColum+pageSize);
        long count=historyService.createHistoricProcessInstanceQuery().finished().notDeleted().count();
        PageInfo pageInfo=new PageInfo();
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        logger.info("-------------查询所有通过的任务结束--------------");
        return pageInfo;
    }

    @Override
    public PageInfo<HistoricProcessInstance> selectAllRefuseApprove(int startPage, int pageSize) {
        int startColum=(startPage-1)*pageSize;
        List<HistoricProcessInstance> list=historyService.createHistoricProcessInstanceQuery().finished().deleted().listPage(startColum,startColum+pageSize);
        long count=historyService.createHistoricProcessInstanceQuery().finished().deleted().count();
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
    public String getLastApprover(String processId) {
        Task task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        if (task == null){
                HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).orderByTaskCreateTime().desc().finished().list().get(0);
            return taskInstance.getAssignee();
         }else{
            HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).orderByTaskCreateTime().desc().unfinished().list().get(0);
            return taskInstance.getAssignee();
        }
    }

    @Override
    public void jointProcess(String taskId, List<String> list) {
        processCoreService.jointProcess(taskId,list);
    }

    @Override
    public Task queryTaskByProcessId(String processId) {
        return taskService.createTaskQuery().processInstanceId(processId).singleResult();
    }

    @Override
    public HistoricProcessInstance queryProcessInstance(String processId) {

        return historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
    }


}
