package com.hengtian.flow.service.impl;

import com.google.common.collect.Lists;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 任务处理工具集合
 *
 * @author houjinrong@chtwm.com
 * date 2018/4/28 16:35
 */
public class ActivitiUtilServiceImpl {

    Logger logger = Logger.getLogger(getClass());

    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;

    /**
     * 获取当前历史任务节点
     * @param processInstaceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 15:09
     */
    private List<String> getHisTaskDefKeys(String processInstaceId){
        List<HistoricTaskInstance> hisTask = historyService.createHistoricTaskInstanceQuery().orderByTaskDefinitionKey().list();
        if(CollectionUtils.isEmpty(hisTask)){
            return null;
        }
        List<String> taskDefKeyList = Lists.newArrayList();
        for(HistoricTaskInstance ht : hisTask){
            taskDefKeyList.add(ht.getTaskDefinitionKey());
        }

        return taskDefKeyList;
    }

    /**
     * 获取可跳转到的任务节点
     * @param task 发起跳转节点 - 历史任务记录
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 15:05
     */
    protected List<String> getTaskDefKeysForJump(TaskInfo task){
        return getHisTaskDefKeys(task.getProcessInstanceId());
    }

    /**
     * 获取可跳转到的任务节点
     * @param taskId 发起跳转节点 - 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 15:05
     */
    protected List<String> getTaskDefKeysForJump(String taskId){
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if(historicTaskInstance == null){
            return null;
        }
        return getTaskDefKeysForJump(historicTaskInstance);
    }

    /**
     * 判断当前节点是否允许跳转到达
     * @param taskId 发起跳转节点任务ID
     * @param taskDefKey 跳转到达的任务节点KEY
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 16:40
     */
    protected boolean isAllowJump(String taskId, String taskDefKey){
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return isAllowJump(historicTaskInstance, taskDefKey);
    }

    /**
     * 判断当前节点是否允许跳转到达
     * @param task 发起跳转节点任务实例
     * @param taskDefKey 跳转到达的任务节点KEY
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 16:40
     */
    protected boolean isAllowJump(TaskInfo task, String taskDefKey){
        List<String> beforeTaskDefinitionKeys = getBeforeTaskDefinitionKeys(task, true);
        if(CollectionUtils.isEmpty(beforeTaskDefinitionKeys)){
            return false;
        }
        if(beforeTaskDefinitionKeys.contains(taskDefKey)){
            return true;
        }

        return false;
    }

    /**
     * 获取可撤回到的任务节点
     * @param task 任务实例
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 15:05
     */
    protected List<String> getTaskDefKeysForRollback(TaskInfo task){
        //历史任务
        List<String> hisTaskList = getTaskDefKeysForJump(task);
        if(CollectionUtils.isEmpty(hisTaskList)){
            return null;
        }

        //上一节点
        List<String> beforeTaskDefinitionKeys = getBeforeTaskDefinitionKeys(task, false);
        if(CollectionUtils.isEmpty(beforeTaskDefinitionKeys)){
            return null;
        }
        beforeTaskDefinitionKeys.removeIf(new Predicate<String>() {
            @Override
            public boolean test(String taskDefKey) {
                if(hisTaskList.contains(taskDefKey)){
                    return true;
                }
                return false;
            }
        });
        return beforeTaskDefinitionKeys;
    }

    /**
     * 获取可驳回 / 撤回到的任务节点
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 15:05
     */
    protected List<String> getTaskDefKeysForRollback(String taskId){
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if(historicTaskInstance == null){
            return null;
        }

        return getTaskDefKeysForRollback(historicTaskInstance);
    }

    /**
     * 判断当前节点是否可以撤回或驳回
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 16:33
     */
    protected boolean isAllowRollback(String taskId){
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if(historicTaskInstance == null){
            return false;
        }

        return isAllowRollback(historicTaskInstance);
    }

    /**
     * 断当前节点是否可以撤回或驳回
     * @param task 任务实例
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 16:34
     */
    protected boolean isAllowRollback(TaskInfo task){
        if(CollectionUtils.isEmpty(getTaskDefKeysForRollback(task))){
            return false;
        }
        return true;
    }

    /**
     * 获取上一级节点
     * @return 任务节点KEY集合
     * @author houjinrong@chtwm.com
     * date 2018/4/27 17:05
     */
    protected List<String> getBeforeTaskDefinitionKeys(TaskInfo task, boolean isAll){
        List<TaskDefinition> beforeTaskDefinitions = getBeforeTaskDefinitions(task, isAll);
        if(CollectionUtils.isEmpty(beforeTaskDefinitions)){
            return null;
        }
        List<String> beforeTaskDefinitionKeys = Lists.newArrayList();
        for(TaskDefinition def : beforeTaskDefinitions){
            beforeTaskDefinitionKeys.add(def.getKey());
        }
        return beforeTaskDefinitionKeys;
    }

    /**
     * 获取上一节点
     * @return 任务节点定义集合
     * @author houjinrong@chtwm.com
     * date 2018/4/27 17:05
     */
    protected List<TaskDefinition> getBeforeTaskDefinitions(TaskInfo task, boolean isAll){
        String processInstanceId = task.getProcessInstanceId();
        //流程标示
        String processDefinitionId = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

        List<ActivityImpl> activitiList = pde.getActivities();
        //执行实例
        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();

        ProcessDefinitionEntity entity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        ActivityImpl actImpl = entity.getProcessDefinition().findActivity(activityId);

        List<TaskDefinition> beforeTaskDefinition = Lists.newArrayList();
        for(PvmTransition pt : actImpl.getIncomingTransitions()){
            beforeTaskDefinition((ActivityImpl) pt.getSource(), beforeTaskDefinition, isAll);
        }
        return beforeTaskDefinition;
    }

    /**
     * 递归获取上一步任务节点
     * @param actImpl 流程节点的定义
     * @param beforeTaskDefinition 存放获取到的节点集合
     * @param isAll 标识是否获取当前节点之前的所有节点
     *            isAll为true  获取当前节点之前的所有节点
     *            isAll为false 获取当前节点上一步的所有节点
     *
     * @author houjinrong@chtwm.com
     * date 2018/4/28 9:32
     */
    private void beforeTaskDefinition(ActivityImpl actImpl, List<TaskDefinition> beforeTaskDefinition, boolean isAll){
        ActivityBehavior activityBehavior = actImpl.getActivityBehavior();
        for(PvmTransition pt : actImpl.getIncomingTransitions()){
            if(activityBehavior instanceof UserTaskActivityBehavior){
                TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityBehavior).getTaskDefinition();
                beforeTaskDefinition.add(taskDefinition);
                if(isAll){
                    if(pt.getSource() != null){
                        beforeTaskDefinition((ActivityImpl) pt.getSource(), beforeTaskDefinition, isAll);
                    }
                }
            }else{
                if(pt.getSource() != null){
                    beforeTaskDefinition((ActivityImpl) pt.getSource(), beforeTaskDefinition, isAll);
                }
            }
        }
    }

    /**
     * 获取上一节点
     * @return 任务节点定义集合
     * @author houjinrong@chtwm.com
     * date 2018/4/27 17:05
     */
    protected List<String> getNextTaskDefinitionKeys(TaskInfo task, boolean isAll){
        List<TaskDefinition> nextTaskDefinitions = getNextTaskDefinitions(task, isAll);
        if(CollectionUtils.isEmpty(nextTaskDefinitions)){
            return null;
        }
        List<String> nextTaskDefinitionKeys = Lists.newArrayList();
        for(TaskDefinition def : nextTaskDefinitions){
            nextTaskDefinitionKeys.add(def.getKey());
        }
        return nextTaskDefinitionKeys;
    }

    /**
     * 递归获取上一步任务节点
     * @param task 任务实体类
     * @param isAll 标识是否获取当前节点之后的所有节点
     *            isAll为true  获取当前节点之前的所有节点
     *            isAll为false 获取当前节点上一步的所有节点
     *
     * @author houjinrong@chtwm.com
     * date 2018/5/2 16:28
     */
    private List<TaskDefinition> getNextTaskDefinitions(TaskInfo task, boolean isAll){
        //执行实例
        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();

        ProcessDefinitionEntity entity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
        ActivityImpl actImpl = entity.getProcessDefinition().findActivity(activityId);

        List<TaskDefinition> nextTaskDefinition = Lists.newArrayList();
        for(PvmTransition pt : actImpl.getOutgoingTransitions()){
            nextTaskDefinition((ActivityImpl) pt.getDestination(), nextTaskDefinition, isAll);
        }

        return nextTaskDefinition;
    }

    /**
     * 递归获取上一步任务节点
     * @param actImpl 流程节点的定义
     * @param nextTaskDefinition 存放获取到的节点集合
     * @param isAll 标识是否获取当前节点之后的所有节点
     *            isAll为true  获取当前节点之前的所有节点
     *            isAll为false 获取当前节点上一步的所有节点
     *
     * @author houjinrong@chtwm.com
     * date 2018/4/28 9:32
     */
    private void nextTaskDefinition(ActivityImpl actImpl, List<TaskDefinition> nextTaskDefinition, boolean isAll){
        ActivityBehavior activityBehavior = actImpl.getActivityBehavior();
        for(PvmTransition pt : actImpl.getOutgoingTransitions()){
            if(activityBehavior instanceof UserTaskActivityBehavior){
                TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityBehavior).getTaskDefinition();
                nextTaskDefinition.add(taskDefinition);
                if(isAll){
                    if(pt.getDestination() != null){
                        nextTaskDefinition((ActivityImpl) pt.getDestination(), nextTaskDefinition, isAll);
                    }
                }
            }else{
                if(pt.getDestination() != null){
                    nextTaskDefinition((ActivityImpl) pt.getDestination(), nextTaskDefinition, isAll);
                }
            }
        }
    }

    /**
     * 根据实例编号获取下一个任务节点实例集合
     *
     * @param procInstId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/17 16:43
     */
    protected List<TaskDefinition> getNextTaskDefinitions(String procInstId) {
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
}
