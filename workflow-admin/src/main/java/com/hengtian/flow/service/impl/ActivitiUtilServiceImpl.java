package com.hengtian.flow.service.impl;

import com.google.common.collect.Lists;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Predicate;

/**
 * 任务处理工具集合
 *
 * @author houjinrong@chtwm.com
 * date 2018/4/28 16:35
 */
public class ActivitiUtilServiceImpl {

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
            getPvmTransitions((ActivityImpl) pt.getSource(), beforeTaskDefinition, isAll);
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
    private void getPvmTransitions(ActivityImpl actImpl, List<TaskDefinition> beforeTaskDefinition, boolean isAll){
        ActivityBehavior activityBehavior = actImpl.getActivityBehavior();
        for(PvmTransition pt : actImpl.getIncomingTransitions()){
            pt.getSource().getIncomingTransitions();
            if(activityBehavior instanceof UserTaskActivityBehavior){
                TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityBehavior).getTaskDefinition();
                beforeTaskDefinition.add(taskDefinition);
                if(isAll){
                    if(pt.getSource() != null){
                        getPvmTransitions((ActivityImpl) pt.getSource(), beforeTaskDefinition, isAll);
                    }
                }
            }else{
                if(pt.getSource() != null){
                    getPvmTransitions((ActivityImpl) pt.getSource(), beforeTaskDefinition, isAll);
                }
            }
        }
    }
}
