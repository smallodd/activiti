package com.activiti.service.impl;

import com.activiti.common.CodeConts;
import com.activiti.expection.WorkFlowException;
import com.activiti.service.ProcessCoreService;
import com.activiti.service.WorkTaskService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
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
    public List<Task> queryByAssign(String userId,int startPage,int pageSize) {
        return taskService.createTaskQuery().taskAssignee(userId).listPage(startPage,pageSize);
    }



    @Override
    public List<HistoricTaskInstance> queryHistoryList(String userId, int startPage, int pageSize) {

        return historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).listPage(startPage,pageSize);
    }

    @Override
    public void completeTask(String taskId,String nextUserId, String note,String authName) throws WorkFlowException{
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        Authentication.setAuthenticatedUserId(authName);
        taskService.addComment(taskId,task.getProcessInstanceId(),note);

        try {
            if(StringUtils.isNotBlank(nextUserId)) {
                Map<String, Object> map = new HashMap<>();
                map.put("userid",nextUserId);

                processCoreService.passProcess(taskId,map);


            }else {
               processCoreService.passProcess(taskId,null);
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
    public Boolean refuseTask(String taskId, String reason) {
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();

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
    public List<HistoricProcessInstance> getInvolvedUserTasks(String userid,int startCloum,int pageSzie,int status){
        HistoricProcessInstanceQuery query=historyService.createHistoricProcessInstanceQuery();

        query.orderByProcessInstanceStartTime().desc();
        return  query.involvedUser(userid).listPage(startCloum,pageSzie);
    }

    /**
     *查询任务所属节点
     * @param taskid  任务id
     * @return  返回图片流
     */
    public InputStream generateImage(String taskid){
        //1.创建核心引擎流程对象processEngine

        Task task = taskService.createTaskQuery().taskId(taskid).singleResult();
        //流程定义
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());

        //正在活动节点
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(task.getExecutionId());

        ProcessDiagramGenerator pdg = processEngineConfiguration.getProcessDiagramGenerator();
        //生成流图片
        InputStream inputStream = pdg.generateDiagram(bpmnModel, "PNG", activeActivityIds, activeActivityIds,
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

}
