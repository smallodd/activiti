package com.activiti.service.impl;

import com.activiti.common.CodeConts;
import com.activiti.expection.WorkFlowException;
import com.activiti.service.ProcessCoreService;
import com.activiti.service.WorkTaskService;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.ReturnPageInfo;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.MessageFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2017/7/18.
 */
public class WorkTaskServiceImpl implements WorkTaskService {

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
       long count= taskService.createTaskQuery().taskAssignee(userId).count();
        PageInfo<Task> pageInfo=new PageInfo<>();
        List<Task> list=taskService.createTaskQuery().taskAssignee(userId).listPage(startPage,pageSize);
        pageInfo.setList(list);
        pageInfo.setTotal(count);
        return pageInfo;
    }



    @Override
    public List<HistoricTaskInstance> queryHistoryList(String userId, int startPage, int pageSize) {

        return historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).listPage(startPage,pageSize);
    }

    @Override
    public void completeTask(String processInstanceId,String nextUserId, String note,String authName) throws WorkFlowException{
        Task task=taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        Authentication.setAuthenticatedUserId(authName);
        taskService.addComment(task.getId(),task.getProcessInstanceId(),note);

        try {
            if(StringUtils.isNotBlank(nextUserId)) {
                Map<String, Object> map = new HashMap<>();
                map.put("userid",nextUserId);

              taskService.complete(task.getId(),map);


            }else {
                taskService.complete(task.getId());
            }
        } catch (Exception e) {
            throw new WorkFlowException(CodeConts.WORK_FLOW_PARAM_ERROR,"参数不合法");
        }

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
    public Boolean refuseTask(String processId, String reason) {
        Task task=taskService.createTaskQuery().processInstanceId(processId).singleResult();

        runtimeService.deleteProcessInstance(task.getProcessInstanceId(),reason);
        return true;
    }

    /**
     * 获取申请人提交的任务
     * @param userid  申请人信息
     * @param startCloum  数据库起始行数
     * @param pageSzie    查询多少条数
     * @param status      0 :审批中的任务
     *                    1 ：审批完成的任务
     *
     * @return
     */
    public List<HistoricProcessInstance> getApplyTasks(String userid,int startCloum,int pageSzie,int status){
        HistoricProcessInstanceQuery query=historyService.createHistoricProcessInstanceQuery();
        if(status==0){
            query.unfinished();
        }else {
            query.finished();
        }
        query.orderByProcessInstanceStartTime().desc();
    List<HistoricProcessInstance> list= query.startedBy(userid).listPage(startCloum,pageSzie);


      return list;
    }

    /**
     * 获取参与审批用户的审批历史信息
     * @param userid   审批人用户唯一标识
     * @param startCloum   数据库开始行数
     * @param pageSzie     查询多少条数

     *
     *
     * @return
     */
    public List<HistoricProcessInstance> getInvolvedUserTasks(String userid,int startCloum,int pageSzie){
        HistoricProcessInstanceQuery query=historyService.createHistoricProcessInstanceQuery();

        query.orderByProcessInstanceStartTime().desc();
        return  query.involvedUser(userid).listPage(startCloum,pageSzie);
    }

    /**
     *查询任务所属节点
     * @param taskid  任务id
     * @return  返回图片流
     */
    public InputStream generateImage(String  taskid){


        HistoricTaskInstance task=historyService.createHistoricTaskInstanceQuery().taskId(taskid).singleResult();
        //流程定义
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());


        ProcessDiagramGenerator pdg = processEngineConfiguration.getProcessDiagramGenerator();

        List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();

        //获取历史流程实例
        HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

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

            //生成本地图片
            File file = new File("e:/test.png");
            FileUtils.copyInputStreamToFile(inputStream, file);
            return inputStream;
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
    public Comment selectComment(String processInstanceId){
        HistoricActivityInstance activityInstance=historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().desc().singleResult();
        Comment comment=taskService.getComment(activityInstance.getTaskId());
        return comment;
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
}
