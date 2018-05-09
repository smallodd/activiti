package com.hengtian.flow.controller;

import com.google.common.collect.Lists;
import com.hengtian.common.base.BaseController;
import com.hengtian.flow.service.TRuTaskService;
import com.hengtian.flow.service.TUserTaskService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class WorkflowBaseController extends BaseController {

    Logger logger = Logger.getLogger(getClass());

    @Autowired
    ProcessEngineFactoryBean processEngine;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TRuTaskService tRuTaskService;
    @Autowired
    private TUserTaskService tUserTaskService;

    /**
     * 获取上一个节点信息
     * @param task 任务
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/25 18:07
     */
    protected List<TaskDefinition> beforeTaskDefinition(Task task){
        String processInstanceId = task.getProcessInstanceId();
        //流程标示
        String processDefinitionId = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

        List<ActivityImpl> activitiList = pde.getActivities();
        //执行实例
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String activityId = execution.getActivityId();

        ProcessDefinitionEntity entity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        ActivityImpl actImpl = entity.getProcessDefinition().findActivity(activityId);
        List<TaskDefinition> beforeTaskDefinition = Lists.newArrayList();
        for(PvmTransition pt : actImpl.getIncomingTransitions()){
            TaskDefinition taskDefinition = ((UserTaskActivityBehavior) ((ActivityImpl) pt).getActivityBehavior()).getTaskDefinition();
            beforeTaskDefinition.add(taskDefinition);
            /*PvmActivity inAct = pt.getSource();
            String type = (String)inAct.getProperty("type");
            if("exclusiveGateway".equals(type) || "parallelGateway".equals(type)){
                beforeTaskDefinition = nextTaskDefinition((ActivityImpl) pt.getDestination(), activityId);
                break;
            }*/
        }
        return beforeTaskDefinition;
    }

    /**
     * 获取需要高亮的线 (适配5.18以上版本；由于mysql5.6.4之后版本时间支持到毫秒，固旧方法比较开始时间的方法不在适合当前系统)
     *
     * @param processDefinitionEntity
     * @param historicActivityInstances
     * @return
     */
    protected List<String> getHighLightedFlows(
            ProcessDefinitionEntity processDefinitionEntity,
            List<HistoricActivityInstance> historicActivityInstances) {
        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
            HistoricActivityInstance hai = historicActivityInstances.get(i);
            ActivityImpl activityImpl = processDefinitionEntity.findActivity(hai.getActivityId());// 得到节点定义的详细信息
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点

            for (int j = i + 1; j < historicActivityInstances.size(); j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// 后续第一个节点
                if (hai.getEndTime() != null && activityImpl1.getStartTime().getTime()-hai.getEndTime().getTime() < 1000) {
                    // 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2 = processDefinitionEntity.findActivity(activityImpl1.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                }
            }
            List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();// 取出节点的所有出去的线
            for (PvmTransition pvmTransition : pvmTransitions) {
                // 对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition.getDestination();
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }
}
