package com.hengtian.flow.controller;

import com.hengtian.common.base.BaseController;
import com.hengtian.common.param.TaskNodeResult;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.TRuTaskService;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * 设置审批人接口
     *
     * @param task
     * @param tUserTask
     */
    protected Boolean setApprover(Task task, TUserTask tUserTask) {
        try {
            Map<String, Object> map = taskService.getVariables(task.getId());
            String approvers = tUserTask.getCandidateIds();
            taskService.setAssignee(task.getId(), approvers);
            TRuTask tRuTask = new TRuTask();
            String[] approverList = approvers.split(",");
            List<TRuTask> tRuTaskList = new ArrayList<>();
            for (int i = 0; i < approverList.length; i++) {
                //生成扩展任务信息
                String approver = approverList[i];
                tRuTask.setApprover(approver);
                tRuTask.setApproverType(tUserTask.getAssignType());
                tRuTask.setOwer(task.getOwner());
                tRuTask.setTaskId(task.getId());
                tRuTask.setTaskType(tUserTask.getTaskType());
                tRuTask.setIsFinished(0);
                tRuTask.setExpireTime(task.getDueDate());
                tRuTask.setAppKey(map.get("appKey").toString());
                tRuTaskList.add(tRuTask);

            }
            tRuTaskService.insertBatch(tRuTaskList);
            return true;
        } catch (Exception e) {
            logger.error(e);
            return false;
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
    protected Boolean checkBusinessKeyIsInFlow(String processDefiniKey, String bussinessKey, String appKey) {
        TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey(processDefiniKey).processInstanceBusinessKey(bussinessKey);
        taskQuery.processVariableValueLike("appKey", appKey);
        Task task = taskQuery.singleResult();

        if (task != null) {
            return true;
        }
        return false;
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
    protected List<TaskDefinition> nextTaskDefinition(ActivityImpl activityImpl, String activityId) {
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
     * 将任务列表转换成返回出参任务列表
     *
     * @param list
     * @return
     */
    public static List<TaskNodeResult> toTaskNodeResultList(List<Task> list) {
        List<TaskNodeResult> nodeResults = new ArrayList<>();
        TaskNodeResult taskNodeResult;
        for (Task task : list) {
            taskNodeResult = toTaskNodeResult(task);
            nodeResults.add(taskNodeResult);
        }
        return nodeResults;
    }

    /**
     * 转换成出参任务
     *
     * @param task
     * @return
     */
    public static TaskNodeResult toTaskNodeResult(Task task) {

        TaskNodeResult taskNodeResult = new TaskNodeResult();

        taskNodeResult.setTaskId(task.getId());
        taskNodeResult.setTaskDefinedKey(task.getTaskDefinitionKey());
        taskNodeResult.setFormKey(task.getFormKey());
        taskNodeResult.setName(task.getName());
        return taskNodeResult;
    }
}
