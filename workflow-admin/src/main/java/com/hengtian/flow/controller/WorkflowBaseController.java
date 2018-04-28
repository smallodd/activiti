package com.hengtian.flow.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.enums.AssignType;
import com.hengtian.common.enums.TaskType;
import com.hengtian.common.result.TaskNodeResult;
import com.hengtian.common.param.TaskParam;
import com.hengtian.common.result.Constant;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.TRuTaskService;
import com.hengtian.flow.service.TUserTaskService;
import com.rbac.service.RoleService;
import com.user.service.emp.EmpService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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
     * 根据实例编号获取下一个任务节点实例集合
     *
     * @param procInstId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/17 16:43
     */
    protected List<TaskDefinition> getTaskDefinitionList(String procInstId) {
        List<TaskDefinition> taskDefinitionList = new ArrayList<TaskDefinition>();
        //流程标示
        String processDefinitionId = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        //执行实例
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        //当前实例的执行到哪个节点
        String activitiId = execution.getActivityId();
        //获得当前任务的所有节点
        List<ActivityImpl> activitiList = pde.getActivities();
        String id = null;
        for (ActivityImpl activityImpl : activitiList) {
            id = activityImpl.getId();
            if (activitiId.equals(id)) {
                logger.debug("当前任务：" + activityImpl.getProperty("name"));
                taskDefinitionList = nextTaskDefinition(activityImpl, activityImpl.getId());
            }
        }
        return taskDefinitionList;
    }

    /**
     * 获取所有下一节点
     *
     * @param activityImpl
     * @param activityId
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/17 16:51
     */
    public List<TaskDefinition> nextTaskDefinition(ActivityImpl activityImpl, String activityId) {
        //所有的任务实例
        List<TaskDefinition> taskDefinitionList = new ArrayList<TaskDefinition>();
        //逐个获取的任务实例
        List<TaskDefinition> nextTaskDefinition = new ArrayList<TaskDefinition>();
        TaskDefinition taskDefinition = null;
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior()).getTaskDefinition();
            taskDefinitionList.add(taskDefinition);
        } else {
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for (PvmTransition tr : outTransitions) {
                //获取线路的终点节点
                PvmActivity ac = tr.getDestination();
                //如果是互斥网关或者是并行网关
                if ("exclusiveGateway".equals(ac.getProperty("type")) || "parallelGateway".equals(ac.getProperty("type"))) {
                    outTransitionsTemp = ac.getOutgoingTransitions();
                    if (outTransitionsTemp.size() == 1) {
                        nextTaskDefinition = nextTaskDefinition((ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId);
                        taskDefinitionList.addAll(nextTaskDefinition);
                    } else if (outTransitionsTemp.size() > 1) {
                        for (PvmTransition tr1 : outTransitionsTemp) {
                            nextTaskDefinition = nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId);
                            taskDefinitionList.addAll(nextTaskDefinition);
                        }
                    }
                } else if ("userTask".equals(ac.getProperty("type"))) {
                    taskDefinition = ((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior()).getTaskDefinition();
                    taskDefinitionList.add(taskDefinition);
                } else {
                    logger.debug(ac.getProperty("type").toString());
                }
            }
        }
        return taskDefinitionList;
    }

    /**
     * 根据连线条件conditionText（类似${iscorrect==1}）获取下一个节点，此方法以后会用
     *
     * @param elString 是类似${iscorrect==1}的连线条件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/17 16:52
     */
    @SuppressWarnings("unused")
    protected List<TaskDefinition> nextTaskDefinition(ActivityImpl activityImpl, String activityId, String elString) {
        //所有的任务实例
        List<TaskDefinition> taskDefinitionList = new ArrayList<TaskDefinition>();
        //逐个获取的任务实例
        List<TaskDefinition> nextTaskDefinition = new ArrayList<TaskDefinition>();
        TaskDefinition taskDefinition = null;
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior()).getTaskDefinition();
            taskDefinitionList.add(taskDefinition);
        } else {
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for (PvmTransition tr : outTransitions) {
                //获取线路的终点节点
                PvmActivity ac = tr.getDestination();
                //如果是互斥网关或者是并行网关
                if ("exclusiveGateway".equals(ac.getProperty("type")) || "parallelGateway".equals(ac.getProperty("type"))) {
                    outTransitionsTemp = ac.getOutgoingTransitions();
                    if (outTransitionsTemp.size() == 1) {
                        nextTaskDefinition = nextTaskDefinition((ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId, elString);
                        taskDefinitionList.addAll(nextTaskDefinition);
                    } else if (outTransitionsTemp.size() > 1) {
                        for (PvmTransition tr1 : outTransitionsTemp) {
                            Object s = tr1.getProperty("conditionText");
                            if (elString.equals(StringUtils.trim(s.toString()))) {
                                nextTaskDefinition = nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, elString);
                                taskDefinitionList.addAll(nextTaskDefinition);
                            }
                        }
                    }
                } else if ("userTask".equals(ac.getProperty("type"))) {
                    taskDefinition = ((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior()).getTaskDefinition();
                    taskDefinitionList.add(taskDefinition);
                } else {
                    logger.debug(ac.getProperty("type").toString());
                }
            }
        }
        return taskDefinitionList;
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
