package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hengtian.common.enums.ProcessStatusEnum;
import com.hengtian.common.enums.ResultEnum;
import com.hengtian.common.enums.TaskStatusEnum;
import com.hengtian.common.result.Result;
import com.hengtian.common.result.TaskNodeResult;
import com.hengtian.flow.dao.WorkflowDao;
import com.hengtian.flow.model.RuProcinst;
import com.hengtian.flow.model.TTaskButton;
import com.hengtian.flow.model.TaskResult;
import com.hengtian.flow.service.RuProcinstService;
import com.hengtian.flow.service.TTaskButtonService;
import com.hengtian.flow.vo.TaskVo;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Predicate;

/**
 * 任务处理工具集合
 *
 * @author houjinrong@chtwm.com
 * date 2018/4/28 16:35
 */
public class ActivitiUtilServiceImpl extends ServiceImpl<WorkflowDao, TaskResult> {

    Logger logger = Logger.getLogger(getClass());

    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private TTaskButtonService tTaskButtonService;
    @Autowired
    private RuProcinstService ruProcinstService;

    public List<TaskNodeResult> setButtons(List<TaskNodeResult> list){
        if(list!=null&&list.size()>0) {
            String id=list.get(0).getProcessInstanceId();
            ProcessInstance processInstance=runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
            for (TaskNodeResult taskNodeResult : list) {
                EntityWrapper entityWrapper = new EntityWrapper();
                entityWrapper.where("proc_def_key={0}", processInstance.getProcessDefinitionKey()).andNew("task_def_key={0}", taskNodeResult.getTaskDefinedKey());
                List<TTaskButton> tTaskButtons = tTaskButtonService.selectList(entityWrapper);
                List<String> li = new ArrayList<>();
                for(TTaskButton tTaskButton:tTaskButtons){
                    li.add(tTaskButton.getButtonKey());
                }
                taskNodeResult.setButtonKeys(li);
            }
        }
        return  list;
    }
    /**
     * 获取当前历史任务节点
     * @param processInstaceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 15:09
     */
    protected List<String> getHisTaskDefKeys(String processInstaceId){
        List<HistoricTaskInstance> hisTask = historyService.createHistoricTaskInstanceQuery().list();
        if(CollectionUtils.isEmpty(hisTask)){
            return null;
        }

        //去重
        Set<String> taskDefKeySet = Sets.newHashSet();
        for(HistoricTaskInstance ht : hisTask){
            taskDefKeySet.add(ht.getTaskDefinitionKey());
        }
        List<String> taskDefKeyList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(taskDefKeySet)){
            taskDefKeyList.addAll(taskDefKeySet);
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
        //ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

        //List<ActivityImpl> activitiList = pde.getActivities();
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
    protected List<String> getNextTaskDefinitionKeys(TaskInfo task,boolean isAll){
        List<String > nextTaskDefinitionKeys = Lists.newArrayList();
        try {
            Map<String, FlowNode> nextTask = findNextTask(task,isAll);
            if(nextTask != null){
                for (String s : nextTask.keySet()) {
                    nextTaskDefinitionKeys.add(s);
                }
            }
        } catch (Exception e) {
            logger.error("获取下一个节点失败", e);
            return nextTaskDefinitionKeys;
        }

        return nextTaskDefinitionKeys;
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

    /**
     * 任务驳回
     *
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/18 16:01
     */
    public Result taskRollback1(String userId, String taskId) {
        try {
            Map<String, Object> variables;
            // 取得当前任务
            HistoricTaskInstance currTask = historyService
                    .createHistoricTaskInstanceQuery().taskId(taskId)
                    .singleResult();
            // 取得流程实例
            ProcessInstance instance = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(currTask.getProcessInstanceId())
                    .singleResult();
            if (instance == null) {
                //流程结束
                return new Result(ResultEnum.PROCINST_NOT_EXIST.code, ResultEnum.PROCINST_NOT_EXIST.msg);
            }
            variables = instance.getProcessVariables();
            // 取得流程定义
            ProcessDefinitionEntity definition = (ProcessDefinitionEntity) (processEngine.getRepositoryService().getProcessDefinition(currTask
                    .getProcessDefinitionId()));

            if (definition == null) {
                //log.error("流程定义未找到");
                return new Result(ResultEnum.PROCESS_NOT_EXIST.code, ResultEnum.PROCESS_NOT_EXIST.msg);
            }
            // 取得上一步活动
            ActivityImpl currActivity = ((ProcessDefinitionImpl) definition).findActivity(currTask.getTaskDefinitionKey());
            List<PvmTransition> nextTransitionList = currActivity.getIncomingTransitions();
            // 清除当前活动的出口
            List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
            List<PvmTransition> pvmTransitionList = currActivity.getOutgoingTransitions();
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
                historyService.deleteHistoricTaskInstance(task.getId());
            }
            // 恢复方向
            for (TransitionImpl transitionImpl : newTransitions) {
                currActivity.getOutgoingTransitions().remove(transitionImpl);
            }
            for (PvmTransition pvmTransition : oriPvmTransitionList) {
                pvmTransitionList.add(pvmTransition);
            }
        } catch (Exception e) {
            return new Result(ResultEnum.FAIL.code, ResultEnum.FAIL.msg);
        }

        return new Result(true, "撤回成功");
    }


    /**
     * 查询流程当前节点的下一步节点。用于流程提示时的提示。
     * @param task 任务实体类
     * @return
     * @throws Exception
     */
    public Map<String, FlowNode> findNextTask(TaskInfo task,boolean isAll){
        Map<String, FlowNode> nodeMap = Maps.newHashMap();
        //查询流程定义。
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<Process> processList = bpmnModel.getProcesses();
        Process process = processList.get(0);
        //当前节点流定义
        FlowNode sourceFlowElement = ( FlowNode) process.getFlowElement(task.getTaskDefinitionKey());
        //找到当前任务的流程变量
        List<HistoricVariableInstance> listVar=historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list() ;
        iteratorNextNodes(process, sourceFlowElement, nodeMap , listVar,isAll);
        return nodeMap;
    }







    protected List<String> findBeforeTaskDefKeys(TaskInfo task, boolean isAll){
        List<String> beforeTaskDefKeys = null;
        try {
            Map<String, FlowNode> beforeTask = findBeforeTask(task, isAll);
            if(beforeTask != null && beforeTask.size() > 0){
                beforeTaskDefKeys = Lists.newArrayList();
                beforeTaskDefKeys.addAll(beforeTask.keySet());
            }
        } catch (Exception e) {
            logger.error("获取前置节点失败", e);
            return beforeTaskDefKeys;
        }

        return beforeTaskDefKeys;
    }

    protected List<String> findBeforeTaskDefKeys(String taskId, boolean isAll){
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return findBeforeTaskDefKeys(hisTask, isAll);
    }

    /**
     * 获取上步节点
     * @param taskId 任务ID
     * @param isAll 是否查询之前所有节点
     *              true 之前所有节点，直到开始节点
     *              false 上一步节点
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/4 10:30
     */
    protected Map<String, FlowNode> findBeforeTask(String taskId, boolean isAll) throws Exception{
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return findBeforeTask(hisTask, isAll);
    }

    /**
     * 通过任务节点key查询上个节点的信息
     * @param taskKey   任务节点的key
     * @param processInstanceId   流程实例id
     * @param processDefinitionId 流程定义id
     * @param isAll
     * @return
     * @throws Exception
     */
    protected List <String> findBeforeTask(String taskKey,String processInstanceId ,String processDefinitionId ,boolean isAll) {
        Map<String, FlowNode> nodeMap = Maps.newHashMap();
        //查询流程定义。
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        List<Process> processList = bpmnModel.getProcesses();
        Process process = processList.get(0);
        //当前节点流定义
        FlowNode sourceFlowElement = ( FlowNode) process.getFlowElement(taskKey);
        //找到当前任务的流程变量
        List<HistoricVariableInstance> listVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list() ;
        for(SequenceFlow sf : sourceFlowElement.getIncomingFlows()){
            sourceFlowElement = (FlowNode) process.getFlowElement(sf.getTargetRef());
            iteratorBeforeNodes(process, sourceFlowElement, nodeMap, listVar, isAll);
        }
        List<String> beforeTaskDefKeys = null;

        if(nodeMap != null && nodeMap.size() > 0){
            beforeTaskDefKeys = Lists.newArrayList();
            beforeTaskDefKeys.addAll(nodeMap.keySet());
        }
        return beforeTaskDefKeys;
    }
    /**
     * 获取上步节点
     * @param task
     * @param isAll 是否查询之前所有节点
     *              true 之前所有节点，直到开始节点
     *              false 上一步节点
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/4 10:30
     */
    protected Map<String, FlowNode> findBeforeTask(TaskInfo task, boolean isAll) throws Exception{
        Map<String, FlowNode> nodeMap = Maps.newHashMap();
        //查询流程定义。
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<Process> processList = bpmnModel.getProcesses();
        Process process = processList.get(0);
        //当前节点流定义
        FlowNode sourceFlowElement = ( FlowNode) process.getFlowElement(task.getTaskDefinitionKey());
        //找到当前任务的流程变量
        List<HistoricVariableInstance> listVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list() ;
        for(SequenceFlow sf : sourceFlowElement.getIncomingFlows()){
            sourceFlowElement = (FlowNode) process.getFlowElement(sf.getTargetRef());
            iteratorBeforeNodes(process, sourceFlowElement, nodeMap, listVar, isAll);
        }

        return nodeMap;
    }

    /**
     * 迭代获取上步节点
     *
     * @param process
     * @param sourceFlowElement
     * @param nodeMap
     * @param listVar
     * @param isAll 是否查询之前所有节点
     *              true 之前所有节点，直到开始节点
     *              false 上一步节点
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/4 10:35
     */
    private void iteratorBeforeNodes(Process process, FlowNode sourceFlowElement, Map<String, FlowNode> nodeMap, List<HistoricVariableInstance> listVar, boolean isAll){
        for(SequenceFlow sf : sourceFlowElement.getIncomingFlows()){
            sourceFlowElement = (FlowNode) process.getFlowElement(sf.getSourceRef());

            if((filterExpression(sf.getConditionExpression(), listVar))){
                if(sourceFlowElement instanceof UserTask){
                    nodeMap.put(sourceFlowElement.getId(), sourceFlowElement);
                    if(isAll && sf.getSourceRef() != null){
                        iteratorBeforeNodes(process, sourceFlowElement, nodeMap, listVar, isAll);
                    }
                }else{
                    if(sf.getSourceRef() != null){
                        iteratorBeforeNodes(process, sourceFlowElement, nodeMap, listVar, isAll);
                    }
                }
            }
        }
    }

    /**
     * 查询流程当前节点的下一步节点。用于流程提示时的提示。
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    public Map<String, FlowNode> findNextTask(String taskId,boolean isAll) {
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return findNextTask(hisTask,isAll);
    }

    /**
     * 查询流程当前节点的下一步节点。用于流程提示时的提示。
     * @param process
     * @param sourceFlowElement
     * @param nodeMap
     * @param listVar
     * @throws Exception
     */
    private void iteratorNextNodes(Process process, FlowNode sourceFlowElement, Map<String, FlowNode> nodeMap, List<HistoricVariableInstance> listVar,Boolean isAll) {
        List<SequenceFlow> list = sourceFlowElement.getOutgoingFlows();
        for (SequenceFlow sf : list) {
            sourceFlowElement = (FlowNode) process.getFlowElement(sf.getTargetRef());
            if((filterExpression(sf.getConditionExpression(), listVar))){
                if (sourceFlowElement instanceof UserTask) {
                    if(isAll && sf.getSourceRef() != null){
                        iteratorBeforeNodes(process, sourceFlowElement, nodeMap, listVar, isAll);
                    }
                    nodeMap.put(sourceFlowElement.getId(), sourceFlowElement);
                }else if (sourceFlowElement instanceof ExclusiveGateway) {
                    iteratorNextNodes(process, sourceFlowElement, nodeMap,listVar,isAll);
                }else if (sourceFlowElement instanceof ParallelGateway){
                    iteratorNextNodes(process, sourceFlowElement, nodeMap,listVar,isAll);
                }
            }
        }
    }

    /**
     * 表达式校验，遍历节点时，根据表达式选择分支
     * @author houjinrong@chtwm.com
     * date 2018/5/10 14:30
     */
    private boolean filterExpression(String conditionExpression, List<HistoricVariableInstance> listVar){
        if(StringUtils.isNotEmpty(conditionExpression)) {
            ExpressionFactory factory = new ExpressionFactoryImpl();
            SimpleContext context = new SimpleContext();
            for (HistoricVariableInstance var : listVar) {
                context.setVariable(var.getVariableName(), factory.createValueExpression(var.getValue(), var.getValue().getClass()));
            }
            ValueExpression e = factory.createValueExpression(context, conditionExpression, boolean.class);

            return (Boolean) e.getValue(context);
        }

        return true;
    }

    /**
     * 将activiti任务对象转为本地定义的任务对象，解决无法转为json的问题
     * @author houjinrong@chtwm.com
     * date 2018/5/10 14:58
     */
    protected List<TaskVo> transferTask(String userId,List<Task> taskList, boolean isAll){
        List<TaskVo> taskVoList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(taskList)){
            for (Task t : taskList){
                TaskVo vo = new TaskVo();
                transferTaskInfo(userId, t, vo, isAll);
                taskVoList.add(vo);
            }
        }
        return taskVoList;
    }

    /**
     * 将activiti任务对象转为本地定义的任务对象，解决无法转为json的问题
     * @author houjinrong@chtwm.com
     * date 2018/5/10 14:58
     */
    protected List<TaskVo> transferHisTask(String userId,List<HistoricTaskInstance> taskList, boolean isAll){
        List<TaskVo> taskVoList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(taskList)){
            for (HistoricTaskInstance t : taskList){
                TaskVo vo = new TaskVo();
                transferTaskInfo(userId, t, vo, isAll);
                taskVoList.add(vo);
            }
        }
        return taskVoList;
    }

    private void transferTaskInfo(String userId, TaskInfo task, TaskVo vo, boolean isAll){
        vo.setId(task.getId());
        vo.setTaskName(task.getName());
        vo.setProcessOwner(task.getOwner());
        vo.setTaskCreateTime(task.getCreateTime());
        vo.setBusinessName(task.getCategory());
        vo.setBusinessKey(task.getDescription());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        vo.setTaskAssign(task.getAssignee());
        if(isAll){
            if(task instanceof HistoricTaskInstance){
                HistoricTaskInstance hisTask = (HistoricTaskInstance)task;
                if(TaskStatusEnum.COMPLETE_AGREE.desc.equals(hisTask.getDeleteReason())){
                    vo.setTaskState(TaskStatusEnum.AGREE.status+"");
                }else if(TaskStatusEnum.COMPLETE_REFUSE.desc.equals(hisTask.getDeleteReason())){
                    vo.setTaskState(TaskStatusEnum.REFUSE.status+"");
                }
                if(StringUtils.isNotBlank(task.getAssignee())){
                    vo.setTaskAssign(task.getAssignee().replaceAll("_Y", "").replaceAll("_N", ""));
                }
            }else{
                vo.setTaskState(task.getPriority()+"");
            }
        }else {
            if(task instanceof HistoricTaskInstance){
                vo.setTaskState(task.getPriority()+"");
            }else {
                String assignee = task.getAssignee();
                if(StringUtils.isNotBlank(assignee)){
                    List list = Arrays.asList(assignee.split(","));
                    if(list.contains(userId)){
                        vo.setTaskState(TaskStatusEnum.OPEN.status+"");
                    }
                }
            }
        }
    }

    /**
     * 删除流程
     * @param processInstanceId
     * @param deleteReason
     * @author houjinrong@chtwm.com
     */
    protected void deleteProcessInstance(String processInstanceId, String deleteReason){
        if(StringUtils.isBlank(processInstanceId)){
            return;
        }

        runtimeService.deleteProcessInstance(processInstanceId, "refused");

        EntityWrapper wrapper = new EntityWrapper();
        wrapper.where("proc_inst_id={0}",processInstanceId);

        RuProcinst ruProcinst = new RuProcinst();
        ruProcinst.setProcInstState(ProcessStatusEnum.UNFINISHED.status+"");

        ruProcinstService.update(ruProcinst, wrapper);
    }
}
