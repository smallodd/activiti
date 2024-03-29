package com.hengtian.flow.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.common.common.CodeConts;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hengtian.common.enums.AssignTypeEnum;
import com.hengtian.common.enums.ProcessStatusEnum;
import com.hengtian.common.enums.ResultEnum;
import com.hengtian.common.enums.TaskStatusEnum;
import com.hengtian.common.param.TaskParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import com.hengtian.common.result.TaskNodeResult;
import com.hengtian.common.workflow.exception.WorkFlowException;
import com.hengtian.flow.dao.WorkflowDao;
import com.hengtian.flow.model.AssigneeTemp;
import com.hengtian.flow.model.ProcessInstanceResult;
import com.hengtian.flow.model.RuProcinst;
import com.hengtian.flow.model.TButton;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.model.TaskResult;
import com.hengtian.flow.service.AssigneeTempService;
import com.hengtian.flow.service.RuProcinstService;
import com.hengtian.flow.service.TRuTaskService;
import com.hengtian.flow.service.TTaskButtonService;
import com.hengtian.flow.service.TUserTaskService;
import com.hengtian.flow.vo.AssigneeVo;
import com.hengtian.flow.vo.TaskNodeVo;
import com.hengtian.flow.vo.TaskVo;
import com.rbac.dubbo.RbacDomainContext;
import com.rbac.entity.RbacRole;
import com.rbac.entity.RbacUser;
import com.rbac.service.PrivilegeService;
import com.user.entity.emp.Emp;
import com.user.service.emp.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.PropertyNotFoundException;
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
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 任务处理工具集合
 *
 * @author houjinrong@chtwm.com
 * date 2018/4/28 16:35
 */

@Slf4j
public class ActivitiUtilServiceImpl extends ServiceImpl<WorkflowDao, TaskResult> {


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
    @Autowired
    private TRuTaskService tRuTaskService;
    @Reference(loadbalance = "rbac")
    private PrivilegeService privilegeService;
    @Autowired
    private TUserTaskService tUserTaskService;

    @Autowired
    private AssigneeTempService assigneeTempService;
    @Reference(registry = "chtwm")
    EmpService empService;
    @Value("${rbac.key}")
    String rbacKey;

    public List<TaskNodeResult> setButtons(List<TaskNodeResult> list) {
        if (list != null && list.size() > 0) {

            for (TaskNodeResult taskNodeResult : list) {

               setButtons(taskNodeResult);
            }
        }
        return list;
    }

    public TaskNodeResult setButtons(TaskNodeResult taskNodeResult) {
        String id = taskNodeResult.getProcessInstanceId();
        //ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();

        List<TButton> tButtons = tTaskButtonService.selectTaskButtons(historicProcessInstance.getProcessDefinitionKey(), taskNodeResult.getTaskDefinedKey());
        //TaskFormData taskFormData=formService.getTaskFormData(taskNodeResult.getTaskId());
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskNodeResult.getTaskId()).singleResult();

        if(historicTaskInstance!=null){
            //taskNodeResult.setFormKey(taskFormData.getFormKey());
            taskNodeResult.setFormKey(historicTaskInstance.getFormKey());
        }
        taskNodeResult.setButtonKeys(tButtons);

        //查询流程定义信息
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(historicProcessInstance.getProcessDefinitionId());
        //判断是否需要设置下一个节点审批人
        EntityWrapper<TUserTask> wrapper = new EntityWrapper<>();
        wrapper.eq("task_def_key", taskNodeResult.getTaskDefinedKey());
        wrapper.eq("version_", processDefinition==null?null:processDefinition.getVersion());
        wrapper.eq("proc_def_key",processDefinition.getKey());
        TUserTask tUserTask = tUserTaskService.selectOne(wrapper);

        taskNodeResult.setNeedSetNext(tUserTask.getNeedSetNext());

        return taskNodeResult;
    }

    /**
     * 获取当前历史任务节点
     *
     * @param processInstaceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 15:09
     */
    protected List<String> getHisTaskDefKeys(String processInstaceId) {
        List<HistoricTaskInstance> hisTask = historyService.createHistoricTaskInstanceQuery().list();
        if (CollectionUtils.isEmpty(hisTask)) {
            return null;
        }

        //去重
        Set<String> taskDefKeySet = Sets.newHashSet();
        for (HistoricTaskInstance ht : hisTask) {
            taskDefKeySet.add(ht.getTaskDefinitionKey());
        }
        List<String> taskDefKeyList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(taskDefKeySet)) {
            taskDefKeyList.addAll(taskDefKeySet);
        }
        return taskDefKeyList;
    }

    /**
     * 获取可跳转到的任务节点
     *
     * @param task 发起跳转节点 - 历史任务记录
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 15:05
     */
    protected List<String> getTaskDefKeysForJump(TaskInfo task) {
        return getHisTaskDefKeys(task.getProcessInstanceId());
    }

    /**
     * 获取可跳转到的任务节点
     *
     * @param taskId 发起跳转节点 - 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 15:05
     */
    protected List<String> getTaskDefKeysForJump(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (historicTaskInstance == null) {
            return null;
        }
        return getTaskDefKeysForJump(historicTaskInstance);
    }

    /**
     * 判断当前节点是否允许跳转到达
     *
     * @param taskId     发起跳转节点任务ID
     * @param taskDefKey 跳转到达的任务节点KEY
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 16:40
     */
    protected boolean isAllowJump(String taskId, String taskDefKey) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return isAllowJump(historicTaskInstance, taskDefKey);
    }

    /**
     * 判断当前节点是否允许跳转到达
     *
     * @param task       发起跳转节点任务实例
     * @param taskDefKey 跳转到达的任务节点KEY
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 16:40
     */
    protected boolean isAllowJump(TaskInfo task, String taskDefKey) {
        List<String> beforeTaskDefinitionKeys = getBeforeTaskDefinitionKeys(task, true);
        if (CollectionUtils.isEmpty(beforeTaskDefinitionKeys)) {
            return false;
        }
        if (beforeTaskDefinitionKeys.contains(taskDefKey)) {
            return true;
        }

        return false;
    }

    /**
     * 获取可撤回到的任务节点
     *
     * @param task 任务实例
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 15:05
     */
    protected List<String> getTaskDefKeysForRollback(TaskInfo task) {
        //历史任务
        List<String> hisTaskList = getTaskDefKeysForJump(task);
        if (CollectionUtils.isEmpty(hisTaskList)) {
            return null;
        }

        //上一节点
        List<String> beforeTaskDefinitionKeys = getBeforeTaskDefinitionKeys(task, false);
        if (CollectionUtils.isEmpty(beforeTaskDefinitionKeys)) {
            return null;
        }
        beforeTaskDefinitionKeys.removeIf(new Predicate<String>() {
            @Override
            public boolean test(String taskDefKey) {
                if (!hisTaskList.contains(taskDefKey)) {
                    return true;
                }
                return false;
            }
        });
        return beforeTaskDefinitionKeys;
    }

    /**
     * 获取可驳回 / 撤回到的任务节点
     *
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 15:05
     */
    protected List<String> getTaskDefKeysForRollback(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (historicTaskInstance == null) {
            return null;
        }

        return getTaskDefKeysForRollback(historicTaskInstance);
    }

    /**
     * 判断当前节点是否可以撤回或驳回
     *
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 16:33
     */
    protected boolean isAllowRollback(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (historicTaskInstance == null) {
            return false;
        }

        return isAllowRollback(historicTaskInstance);
    }

    /**
     * 判断当前节点是否可以撤回或驳回
     *
     * @param task 任务实例
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/28 16:34
     */
    protected boolean isAllowRollback(TaskInfo task) {
        if (CollectionUtils.isEmpty(getTaskDefKeysForRollback(task))) {
            return false;
        }
        return true;
    }

    /**
     * 获取上一级节点
     *
     * @return 任务节点KEY集合
     * @author houjinrong@chtwm.com
     * date 2018/4/27 17:05
     */
    protected List<String> getBeforeTaskDefinitionKeys(TaskInfo task, boolean isAll) {
        List<String> beforeTaskDefKeys = null;
        try {
            Map<String, FlowNode> nodeMap = findBeforeTask(task, isAll);
            if (nodeMap != null && nodeMap.size() > 0) {
                beforeTaskDefKeys = Lists.newArrayList();
                beforeTaskDefKeys.addAll(nodeMap.keySet());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return beforeTaskDefKeys;
    }

    /**
     * 获取上一节点
     *
     * @return 任务节点定义集合
     * @author houjinrong@chtwm.com
     * date 2018/4/27 17:05
     */
    protected List<TaskDefinition> getBeforeTaskDefinitions(TaskInfo task, boolean isAll) {
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
        for (PvmTransition pt : actImpl.getIncomingTransitions()) {
            beforeTaskDefinition((ActivityImpl) pt.getSource(), beforeTaskDefinition, isAll);
        }

        return beforeTaskDefinition;
    }

    /**
     * 递归获取上一步任务节点
     *
     * @param actImpl              流程节点的定义
     * @param beforeTaskDefinition 存放获取到的节点集合
     * @param isAll                标识是否获取当前节点之前的所有节点
     *                             isAll为true  获取当前节点之前的所有节点
     *                             isAll为false 获取当前节点上一步的所有节点
     * @author houjinrong@chtwm.com
     * date 2018/4/28 9:32
     */
    private void beforeTaskDefinition(ActivityImpl actImpl, List<TaskDefinition> beforeTaskDefinition, boolean isAll) {
        ActivityBehavior activityBehavior = actImpl.getActivityBehavior();
        for (PvmTransition pt : actImpl.getIncomingTransitions()) {
            if (activityBehavior instanceof UserTaskActivityBehavior) {
                TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityBehavior).getTaskDefinition();
                if (beforeTaskDefinition.contains(beforeTaskDefinition)) {
                    return;
                }
                beforeTaskDefinition.add(taskDefinition);
                if (isAll) {
                    if (pt.getSource() != null) {
                        beforeTaskDefinition((ActivityImpl) pt.getSource(), beforeTaskDefinition, isAll);
                    }
                }
            } else {
                if (pt.getSource() != null) {
                    beforeTaskDefinition((ActivityImpl) pt.getSource(), beforeTaskDefinition, isAll);
                }
            }
        }
    }

    /**
     * 获取上一节点
     *
     * @return 任务节点定义集合
     * @author houjinrong@chtwm.com
     * date 2018/4/27 17:05
     */
    protected List<String> getNextTaskDefinitionKeys(TaskInfo task, boolean isAll) {
        List<String> nextTaskDefinitionKeys = Lists.newArrayList();
        try {
            Map<String, FlowNode> nextTask = findNextTask(task, isAll);
            if (nextTask != null) {
                for (String s : nextTask.keySet()) {
                    nextTaskDefinitionKeys.add(s);
                }
            }
        } catch (Exception e) {
            log.error("获取下一个节点失败", e);
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
        String activityId = execution.getActivityId();
        //获得当前任务的所有节点
        List<ActivityImpl> activityList = pde.getActivities();
        String id = null;
        for (ActivityImpl activityImpl : activityList) {
            id = activityImpl.getId();
            if (activityId.equals(id)) {
                log.debug("当前任务：" + activityImpl.getProperty("name"));
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
                    log.debug(ac.getProperty("type").toString());
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
                    log.debug(ac.getProperty("type").toString());
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


    public List<String> findBeforeTaskDefKeys(TaskInfo task, boolean isAll) {
        List<String> beforeTaskDefKeys = null;
        try {
            Map<String, FlowNode> beforeTask = findBeforeTask(task, isAll);
            if (beforeTask != null && beforeTask.size() > 0) {
                beforeTaskDefKeys = Lists.newArrayList();
                beforeTaskDefKeys.addAll(beforeTask.keySet());
            }
            log.info("查询已经审批过的节点key{}",JSONObject.toJSONString(beforeTaskDefKeys));
        } catch (Exception e) {
            log.error("获取前置节点失败", e);
            return beforeTaskDefKeys;
        }

        return beforeTaskDefKeys;
    }

    protected List<String> findBeforeTaskDefKeys(String taskId, boolean isAll) {
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return findBeforeTaskDefKeys(hisTask, isAll);
    }

    /**
     * 获取上步节点
     *
     * @param taskId 任务ID
     * @param isAll  是否查询之前所有节点
     *               true 之前所有节点，直到开始节点
     *               false 上一步节点
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/4 10:30
     */
    protected Map<String, FlowNode> findBeforeTask(String taskId, boolean isAll) throws RuntimeException {
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return findBeforeTask(hisTask, isAll);
    }

    /**
     * 通过任务节点key查询上个节点的信息
     *
     * @param taskKey             任务节点的key
     * @param processInstanceId   流程实例id
     * @param processDefinitionId 流程定义id
     * @param isAll
     * @return
     * @throws Exception
     */
    protected List<String> findBeforeTask(String taskKey, String processInstanceId, String processDefinitionId, boolean isAll) {
        Map<String, FlowNode> nodeMap = Maps.newHashMap();
        //查询流程定义。
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        List<Process> processList = bpmnModel.getProcesses();
        Process process = processList.get(0);
        //当前节点流定义
        FlowNode sourceFlowElement = (FlowNode) process.getFlowElement(taskKey);
        //找到当前任务的流程变量
        List<HistoricVariableInstance> listVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
        for (SequenceFlow sf : sourceFlowElement.getIncomingFlows()) {
            sourceFlowElement = (FlowNode) process.getFlowElement(sf.getTargetRef());
            iteratorBeforeNodes(process, sourceFlowElement, nodeMap, listVar, isAll,sourceFlowElement);
        }
        List<String> beforeTaskDefKeys = null;

        if (nodeMap != null && nodeMap.size() > 0) {
            beforeTaskDefKeys = Lists.newArrayList();
            beforeTaskDefKeys.addAll(nodeMap.keySet());
        }
        return beforeTaskDefKeys;
    }

    /**
     * 获取上步节点
     *
     * @param task
     * @param isAll 是否查询之前所有节点
     *              true 之前所有节点，直到开始节点
     *              false 上一步节点
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/4 10:30
     */
    protected Map<String, FlowNode> findBeforeTask(TaskInfo task, boolean isAll) throws RuntimeException {
        Map<String, FlowNode> nodeMap = Maps.newHashMap();
        //查询流程定义。
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<Process> processList = bpmnModel.getProcesses();
        Process process = processList.get(0);
        //当前节点流定义
        FlowNode sourceFlowElement = (FlowNode) process.getFlowElement(task.getTaskDefinitionKey());
        //找到当前任务的流程变量
        List<HistoricVariableInstance> listVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
        for (SequenceFlow sf : sourceFlowElement.getIncomingFlows()) {
            sourceFlowElement = (FlowNode) process.getFlowElement(sf.getTargetRef());
            iteratorBeforeNodes(process, sourceFlowElement, nodeMap, listVar, isAll,sourceFlowElement);
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
     * @param isAll             是否查询之前所有节点
     *                          true 之前所有节点，直到开始节点
     *                          false 上一步节点
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/4 10:35
     */
    private void iteratorBeforeNodes(Process process, FlowNode sourceFlowElement, Map<String, FlowNode> nodeMap, List<HistoricVariableInstance> listVar, boolean isAll,FlowNode currentSource) {
        for (SequenceFlow sf : sourceFlowElement.getIncomingFlows()) {
            sourceFlowElement = (FlowNode) process.getFlowElement(sf.getSourceRef());

            if ((filterExpression(sf.getConditionExpression(), listVar))) {
                if (sourceFlowElement instanceof UserTask) {
                    if(nodeMap.containsKey(sourceFlowElement.getId())||sourceFlowElement.getId().equals(currentSource.getId())){
                        continue;
                    }
                    nodeMap.put(sourceFlowElement.getId(), sourceFlowElement);
                    if (isAll && sf.getSourceRef() != null) {
                        iteratorBeforeNodes(process, sourceFlowElement, nodeMap, listVar, isAll,currentSource);
                    }
                } else {
                    if (sf.getSourceRef() != null) {
                        iteratorBeforeNodes(process, sourceFlowElement, nodeMap, listVar, isAll,currentSource);
                    }
                }
            }
        }
    }

    protected List<String> findNextTaskDefKeys(TaskInfo task, boolean isAll) {
        List<String> nextTaskDefKeys = null;
        try {
            Map<String, FlowNode> nextTask = findNextTask(task, isAll);
            if (nextTask != null && nextTask.size() > 0) {
                nextTaskDefKeys = Lists.newArrayList();
                nextTaskDefKeys.addAll(nextTask.keySet());
            }
        } catch (Exception e) {
            log.error("获取前置节点失败", e);
            return nextTaskDefKeys;
        }

        return nextTaskDefKeys;
    }

    protected List<String> findNextTaskDefKeys(String taskId, boolean isAll) {
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return findNextTaskDefKeys(hisTask, isAll);
    }

    /**
     * 查询流程当前节点的下一步节点。用于流程提示时的提示。
     *
     * @param task 任务实体类
     * @return
     * @throws Exception
     */
    public Map<String, FlowNode> findNextTask(TaskInfo task, boolean isAll) {
        Map<String, FlowNode> nodeMap = Maps.newHashMap();
        //查询流程定义。
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<Process> processList = bpmnModel.getProcesses();
        Process process = processList.get(0);
        //当前节点流定义
        FlowNode sourceFlowElement = (FlowNode) process.getFlowElement(task.getTaskDefinitionKey());
        //找到当前任务的流程变量
        List<HistoricVariableInstance> listVar = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
        iteratorNextNodes(process, sourceFlowElement, nodeMap, listVar, isAll);
        return nodeMap;
    }

    /**
     * 查询流程当前节点的下一步节点。用于流程提示时的提示。
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    public Map<String, FlowNode> findNextTask(String taskId, boolean isAll) {
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return findNextTask(hisTask, isAll);
    }

    /**
     * 查询流程当前节点的下一步节点。用于流程提示时的提示。
     *
     * @param process
     * @param sourceFlowElement
     * @param nodeMap
     * @param listVar
     * @throws Exception
     */
    private void iteratorNextNodes(Process process, FlowNode sourceFlowElement, Map<String, FlowNode> nodeMap, List<HistoricVariableInstance> listVar, Boolean isAll) {
        List<SequenceFlow> list = sourceFlowElement.getOutgoingFlows();
        for (SequenceFlow sf : list) {
            sourceFlowElement = (FlowNode) process.getFlowElement(sf.getTargetRef());
            if ((filterExpression(sf.getConditionExpression(), listVar))) {
                if (sourceFlowElement instanceof UserTask) {
                    if (isAll && sf.getSourceRef() != null) {
                        iteratorNextNodes(process, sourceFlowElement, nodeMap, listVar, isAll);
                    }
                    nodeMap.put(sourceFlowElement.getId(), sourceFlowElement);
                } else if (sourceFlowElement instanceof ExclusiveGateway) {
                    iteratorNextNodes(process, sourceFlowElement, nodeMap, listVar, isAll);
                } else if (sourceFlowElement instanceof ParallelGateway) {
                    iteratorNextNodes(process, sourceFlowElement, nodeMap, listVar, isAll);
                }
            }
        }
    }

    /**
     * 表达式校验，遍历节点时，根据表达式选择分支
     *
     * @author houjinrong@chtwm.com
     * date 2018/5/10 14:30
     */
    private boolean filterExpression(String conditionExpression, List<HistoricVariableInstance> listVar) {
        if (StringUtils.isNotEmpty(conditionExpression)&&listVar!=null) {
            ExpressionFactory factory = new ExpressionFactoryImpl();
            SimpleContext context = new SimpleContext();
            for (HistoricVariableInstance var : listVar) {
                if(var!=null&&StringUtils.isNotBlank(var.getVariableName())&&var.getValue()!=null){

                context.setVariable(var.getVariableName(), factory.createValueExpression(var.getValue(), var.getValue().getClass()));
                }
            }
            ValueExpression e = factory.createValueExpression(context, conditionExpression, boolean.class);

            try {
                return (Boolean) e.getValue(context);
            } catch (PropertyNotFoundException ex) {
                log.error("未传入与表达式" + conditionExpression + "对应的参数值", ex);
                return false;
            }
        }

        return true;
    }

    /**
     * 将activiti任务对象转为本地定义的任务对象，解决无法转为json的问题
     *
     * @author houjinrong@chtwm.com
     * date 2018/5/10 14:58
     */
    protected List<TaskVo> transferTask(String userId, List<Task> taskList, boolean isAll) {
        List<TaskVo> taskVoList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(taskList)) {
            for (Task t : taskList) {
                TaskVo vo = new TaskVo();
                transferTaskInfo(userId, t, vo, isAll);
                taskVoList.add(vo);
            }
        }
        return taskVoList;
    }

    /**
     * 将activiti任务对象转为本地定义的任务对象，解决无法转为json的问题
     *
     * @author houjinrong@chtwm.com
     * date 2018/5/10 14:58
     */
    protected List<TaskVo> transferHisTask(String userId, List<HistoricTaskInstance> taskList, boolean isAll) {
        List<TaskVo> taskVoList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(taskList)) {
            for (HistoricTaskInstance t : taskList) {
                TaskVo vo = new TaskVo();
                transferTaskInfo(userId, t, vo, isAll);
                taskVoList.add(vo);
            }
        }
        return taskVoList;
    }

    private void transferTaskInfo(String userId, TaskInfo task, TaskVo vo, boolean isAll) {
        vo.setId(task.getId());
        vo.setTaskName(task.getName());
        vo.setProcessOwner(task.getOwner());
        vo.setTaskCreateTime(task.getCreateTime());
        vo.setBusinessName(task.getCategory());
        vo.setBusinessKey(task.getDescription());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        vo.setTaskAssign(task.getAssignee());
        if (isAll) {
            if (task instanceof HistoricTaskInstance) {
                HistoricTaskInstance hisTask = (HistoricTaskInstance) task;
                if (TaskStatusEnum.COMPLETE_AGREE.desc.equals(hisTask.getDeleteReason())) {
                    vo.setTaskState(TaskStatusEnum.AGREE.status + "");
                } else if (TaskStatusEnum.COMPLETE_REFUSE.desc.equals(hisTask.getDeleteReason())) {
                    vo.setTaskState(TaskStatusEnum.REFUSE.status + "");
                }
                if (StringUtils.isNotBlank(task.getAssignee())) {
                    vo.setTaskAssign(task.getAssignee().replaceAll("_Y", "").replaceAll("_N", ""));
                }
            } else {
                vo.setTaskState(task.getPriority() + "");
            }
        } else {
            if (task instanceof HistoricTaskInstance) {
                vo.setTaskState(task.getPriority() + "");
            } else {
                String assignee = task.getAssignee();
                if (StringUtils.isNotBlank(assignee)) {
                    List list = Arrays.asList(assignee.split(","));
                    if (list.contains(userId)) {
                        vo.setTaskState(TaskStatusEnum.OPEN.status + "");
                    }
                }
            }
        }
    }

    /**
     * 删除流程
     *
     * @param processInstanceId
     * @param deleteReason
     * @author houjinrong@chtwm.com
     */
    protected void deleteProcessInstance(String processInstanceId, String deleteReason) {
        if (StringUtils.isBlank(processInstanceId)) {
            return;
        }

        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
        finishProcessInstance(processInstanceId, ProcessStatusEnum.FINISHED_N.status);
    }

    /**
     * 流程结束更改流程状态
     *
     * @param processInstanceId
     */
    protected void finishProcessInstance(String processInstanceId, int processInstanceState) {
        if (StringUtils.isBlank(processInstanceId)) {
            return;
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.where("proc_inst_id={0}", processInstanceId);

        RuProcinst ruProcinst = new RuProcinst();
        ruProcinst.setProcInstState(processInstanceState + "");

        ruProcinstService.update(ruProcinst, wrapper);
    }

    /**
     * 获取上一步审批人
     *
     * @param hisTask 任务
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/29 10:37
     */
    protected String getBeforeAssignee(HistoricTaskInstance hisTask) {
        if (hisTask == null) {
            return null;
        }
        List<String> beforeTaskDefKeys = findBeforeTaskDefKeys(hisTask, false);
        if (CollectionUtils.isEmpty(beforeTaskDefKeys)) {
            return null;
        }
        Set<String> set=new HashSet<>();
        for(String taskKey : beforeTaskDefKeys){
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisTask.getProcessInstanceId()).taskDefinitionKey(taskKey).orderByTaskCreateTime().desc().list();
            if(list != null && list.size()>0 && StringUtils.isNotBlank(list.get(0).getAssignee())){
                set.add(list.get(0).getAssignee().replace("_Y","").replace("_N",""));
            }
        }

        return StringUtils.join(set.toArray(),",");
    }

    /**
     * 获取上一步审批人
     *
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/29 10:37
     */
    protected String getBeforeAssignee(String taskId) {
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (hisTask == null) {
            return null;
        }

        return getBeforeAssignee(hisTask);
    }

    /**
     * 获取下一步审批人
     *
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/29 10:38
     */
    protected String getNextAssignee(String taskId) {
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (hisTask == null) {
            return null;
        }

        Integer appKey = getAppKey(hisTask.getProcessInstanceId());
        //下一审批人指的是当前处于审批中的任务的审批人，而不是下一个节点的审批人，会出现下一个节点还没有审批
        // List<String> nextTaskDefKeys = findNextTaskDefKeys(hisTask, false);
        List<Task> list = taskService.createTaskQuery().processInstanceId(hisTask.getProcessInstanceId()).list();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<String> nextTaskDefKeys=new ArrayList<>();
        for(Task task :list){
            nextTaskDefKeys.add(task.getTaskDefinitionKey());
        }
        Set<String> set=new HashSet<>();

        EntityWrapper<TRuTask> wrapper = new EntityWrapper<>();
        wrapper.in("task_def_key", nextTaskDefKeys).andNew("proc_inst_id={0}",hisTask.getProcessInstanceId());
        List<TRuTask> tRuTasks = tRuTaskService.selectList(wrapper);

        for (TRuTask t : tRuTasks) {
            if(StringUtils.isNotBlank(t.getAssigneeReal())) {
                set.add(t.getAssigneeReal());
            }else{
                if(t.getAssigneeType().intValue()==AssignTypeEnum.ROLE.code) {
                    RbacDomainContext.getContext().setDomain(rbacKey);
                    List<RbacUser> rbacUsers = privilegeService.getUsersByRoleId(appKey, null, Long.parseLong(t.getAssignee()));
                    for (RbacUser rbacUser : rbacUsers) {
                        set.add(rbacUser.getCode());
                    }
                }
            }
        }

        return StringUtils.join(set.toArray(),",");
    }

    /**
     * 通过用户ID获取用户所有所属角色
     *
     * @param processInstanceId 流程实例ID
     * @param userId            用户ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/1 11:29
     */
    protected List<Long> getAllRoleByUserId(String processInstanceId, String userId) {
        Integer system = getAppKey(processInstanceId);
        if (system == null) {
            return null;
        }
        RbacDomainContext.getContext().setDomain(rbacKey);
        List<RbacRole> roles = privilegeService.getAllRoleByUserId(system, userId);
        if (CollectionUtils.isEmpty(roles)) {
            return null;
        }
        List<Long> roleIds = Lists.newArrayList();
        for (RbacRole role : roles) {
            roleIds.add(role.getId());
        }

        return roleIds;
    }

    /**
     * 通过流程实例ID获取系统KEY
     *
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/1 11:21
     */
    protected Integer getAppKey(String processInstanceId) {
        EntityWrapper<RuProcinst> wrapper = new EntityWrapper<>();
        wrapper.eq("proc_inst_id", processInstanceId);
        RuProcinst ruProcinst = ruProcinstService.selectOne(wrapper);
        return ruProcinst == null ? null : ruProcinst.getAppKey();
    }

    /**
     * 检验用户是否有权操作该任务
     *
     * @author houjinrong@chtwm.com
     * date 2018/6/1 11:50
     */
    protected TRuTask validTaskAssignee(TaskInfo task, Set<String> assigneeSet, List<TRuTask> tRuTasks) {
        if (CollectionUtils.isEmpty(tRuTasks) || CollectionUtils.isEmpty(assigneeSet)) {
            return null;
        }

        Integer assigneeType = tRuTasks.get(0).getAssigneeType();
        List<String> assigneeList = Lists.newArrayList();
        List<Long> roleIds = Lists.newArrayList();
        for (TRuTask rt : tRuTasks) {
            if(StringUtils.isNotBlank(rt.getAssigneeReal())) {
                assigneeList.addAll(Arrays.asList(rt.getAssigneeReal().split(",")));
            }
        }
        for (TRuTask rt : tRuTasks) {
            if(StringUtils.isNotBlank(rt.getAssigneeReal())){
                Iterator<String> iterator = assigneeSet.iterator();
                while (iterator.hasNext()){
                    if(!assigneeList.contains(iterator.next())){
                        iterator.remove();
                    }
                }
                if(CollectionUtils.isNotEmpty(assigneeSet)){
                    return rt;
                }
            }else{
                if(AssignTypeEnum.ROLE.code.equals(assigneeType)){
                    //角色，不需要签收
                    if(CollectionUtils.isEmpty(roleIds)){
                        for(String assignee : assigneeSet){
                            List<Long> allRoleIds = getAllRoleByUserId(task.getProcessInstanceId(), assignee);
                            if(CollectionUtils.isNotEmpty(allRoleIds)){
                                roleIds.addAll(allRoleIds);
                            }
                        }
                    }

                    if (CollectionUtils.isEmpty(roleIds)) {
                        return null;
                    }

                    if (roleIds.contains(Long.parseLong(rt.getAssignee()))) {
                        return rt;
                    }
                }else{
                    //其他情况
                }
            }
        }

        return null;
    }

    public Result validateApproveParam(TRuTask ruTask, TaskParam taskParam) {

        if (taskParam.getPass() != TaskStatusEnum.COMPLETE_AGREE.status && taskParam.getPass() != TaskStatusEnum.COMPLETE_REFUSE.status) {
            return new Result(Constant.PARAM_ERROR, "参数错误！");
        }

        return new Result(true, "success");
    }

    /**
     * 获取当前流程实例下的当前任务节点
     * 如果任务已完成，则获取最后节点
     * @param processInstance 流程实例
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/8 15:56
     */
    public List<TaskNodeVo> getCurrentTask(ProcessInstanceResult processInstance) {
        List<TaskNodeVo> taskNodes = Lists.newArrayList();
        if(processInstance.getProcessInstanceState().equals(ProcessStatusEnum.UNFINISHED.status+"")){
            //未完成
            EntityWrapper<TRuTask> wrapper = new EntityWrapper<>();
            wrapper.eq("proc_inst_id", processInstance.getProcessInstanceId());
            wrapper.in("task_def_key", processInstance.getCurrentTaskKey().split(","));
            List<TRuTask> tRuTasks = tRuTaskService.selectList(wrapper);

            Map<String, TRuTask> map = Maps.newHashMap();
            Map<String,List<AssigneeVo>> assigneeMap = Maps.newHashMap();
            Map<String, Set<String>> assigneeNameMap = Maps.newHashMap();

            for(TRuTask tr : tRuTasks){
                map.put(tr.getTaskDefKey(), tr);
                List<AssigneeVo> users = Lists.newArrayList();
                if(StringUtils.isNotBlank(tr.getAssigneeReal())){
                    String assigneeReal = tr.getAssigneeReal();
                    for(String assignee : assigneeReal.split(",")){
                        Emp rbacUser = empService.selectByCode(assignee);
                        if(rbacUser!=null) {
                            AssigneeVo assigneeVo = new AssigneeVo();
                            assigneeVo.setUserCode(rbacUser.getCode());
                            assigneeVo.setUserName(rbacUser.getName());
                            assigneeVo.setIsComplete(0);

                            users.add(assigneeVo);
                        }
                    }
                }else{
                    if(AssignTypeEnum.ROLE.code.equals(tr.getAssigneeType())){
                        users = getAllUserByRoleCode(processInstance.getAppKey(), Long.parseLong(tr.getAssignee()));
                    }
                }
                if(CollectionUtils.isNotEmpty(users)){
                    for(AssigneeVo av : users){
                        if(assigneeNameMap.containsKey(tr.getTaskDefKey())){
                            assigneeNameMap.get(tr.getTaskDefKey()).add(av.getUserName());
                        }else{
                            Set<String> assigneeSet = Sets.newHashSet(av.getUserName());
                            assigneeNameMap.put(tr.getTaskDefKey(), assigneeSet);
                        }
                    }
                }

                if(assigneeMap.containsKey(tr.getTaskDefKey())){
                    assigneeMap.get(tr.getTaskDefKey()).addAll(users);
                }else{
                    assigneeMap.put(tr.getTaskDefKey(), users);
                }
            }

            if(map.size() > 0){
                Set<String> keySet = map.keySet();
                for(String key : keySet){
                    TaskNodeVo taskNode = new TaskNodeVo();
                    taskNode.setAssignee(assigneeMap.get(key));
                    taskNode.setAssigneeStr(StringUtils.join(assigneeNameMap.get(key), ","));
                    taskNode.setTaskId(map.get(key).getTaskId());
                    taskNode.setTaskDefinitionName(map.get(key).getTaskDefName());
                    taskNode.setTaskDefinitionKey(map.get(key).getTaskDefKey());
                    taskNodes.add(taskNode);
                }
            }
        }else {
            //完成（通过/拒绝）
            List<HistoricTaskInstance> hisTasks = Lists.newArrayList();
            for(String taskDefKey : processInstance.getCurrentTaskKey().split(",")){
                List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstance.getProcessInstanceId()).taskDefinitionKey(taskDefKey).orderByTaskCreateTime().desc().list();
                if(CollectionUtils.isNotEmpty(list)){
                    hisTasks.add(list.get(0));
                }
            }
            Map<String, Integer> temMap = Maps.newHashMap();

            String currentAssignee = null;
            for(HistoricTaskInstance hisTask : hisTasks){
                if(temMap.containsKey(hisTask.getTaskDefinitionKey())){
                    continue;
                }
                TaskNodeVo taskNode = new TaskNodeVo();

                currentAssignee = hisTask.getAssignee()==null?"":hisTask.getAssignee().replaceAll("_Y", "").replaceAll("_N", "");
                taskNode.setTaskId(hisTask.getId());

                String[] assigns=currentAssignee.split(",");
                AssigneeVo assigneeVo;
                List<AssigneeVo> list=new ArrayList<>();
                Set<String> assigneeNameSet = Sets.newHashSet();
                for(String assign:assigns){
                    assigneeVo=new AssigneeVo();
                    Emp rbacUser=empService.selectByCode(assign);
                    if(rbacUser!=null) {
                        assigneeVo.setUserCode(rbacUser.getCode());
                        assigneeVo.setUserName(rbacUser.getName());
                        list.add(assigneeVo);

                        assigneeNameSet.add(assigneeVo.getUserName());
                    }
                }
                taskNode.setAssigneeStr(StringUtils.join(assigneeNameSet, ","));
                taskNode.setAssignee(list);
                taskNode.setTaskDefinitionName(hisTask.getName());
                taskNode.setTaskDefinitionKey(hisTask.getTaskDefinitionKey());
                taskNodes.add(taskNode);
            }
        }
        return taskNodes;
    }

    /**
     * 保存下一节点审批人到临时审批人表
     * @param assigneeNext
     * @param processInstanceId
     * @param currentAssignee
     * @param taskDefKeyBefore
     * @param version
     * @return
     */
    protected Result setNextAssigneeTemp(Task task, String processDefinitionKey, String assigneeNext, String processInstanceId,String currentAssignee, String taskDefKeyBefore, int version, Map<String, Map<String,AssigneeTemp>> assigneeMap){
        log.info("setNextAssigneeTemp开始,入参：taskId"+task.getId()+"assigneeNext:"+assigneeNext+"assigneeMap："+assigneeMap+"currentAssignee:"+currentAssignee);
        Result result = new Result();

        EntityWrapper<AssigneeTemp> _wrapper = new EntityWrapper<>();
        _wrapper.eq("proc_inst_id", task.getProcessInstanceId());
        _wrapper.eq("task_def_key_before",taskDefKeyBefore);
        _wrapper.eq("delete_flag", 0);
        List<AssigneeTemp> assigneeTemps = assigneeTempService.selectList(_wrapper);
        if(CollectionUtils.isEmpty(assigneeTemps)){
            if(StringUtils.isBlank(assigneeNext)){
                log.info("当前节点需设置下步节点审批人， 未发现审批人信息。");
                result.setCode(CodeConts.FAILURE);
                result.setSuccess(false);
                result.setMsg("当前节点需设置下步节点审批人， 未发现审批人信息。");
                return result;
            }
            try {
                JSONArray jsonArray = JSONArray.parseArray(assigneeNext);
                assigneeTemps = validateSetNextAssignee(task, processDefinitionKey, jsonArray, processInstanceId, currentAssignee, taskDefKeyBefore, version);

                if(CollectionUtils.isNotEmpty(assigneeTemps)){
                    assigneeTempService.insertBatch(assigneeTemps);
                }
            } catch (JSONException e) {
                log.error("下步审批人参数格式不正确，不是正确的JSON格式", e);
                throw new WorkFlowException("下步审批人参数格式不正确，不是正确的JSON格式");
            }
        }

        if(CollectionUtils.isNotEmpty(assigneeTemps)){
            for(AssigneeTemp aTemp : assigneeTemps){
                //TODO 循环assigneeTemps  推送审批人信息
                if(assigneeMap.containsKey(aTemp.getTaskDefKey())){
                    Map<String, AssigneeTemp> assigneeTempMap = assigneeMap.get(aTemp.getTaskDefKey());
                    if(assigneeTempMap.containsKey(aTemp.getRoleCode())){
                        AssigneeTemp assigneeTemp = assigneeTempMap.get(aTemp.getRoleCode());
                        assigneeTemp.setAssigneeCode(assigneeTemp.getAssigneeCode()+","+aTemp.getAssigneeCode());
                        assigneeTemp.setAssigneeName(assigneeTemp.getAssigneeName()+","+aTemp.getAssigneeName());
                    }else{
                        assigneeTempMap.put(aTemp.getRoleCode(), aTemp);
                    }

                }else{
                    Map<String,AssigneeTemp> assigneeTempMap = Maps.newHashMap();
                    assigneeTempMap.put(aTemp.getRoleCode(), aTemp);
                    assigneeMap.put(aTemp.getTaskDefKey(), assigneeTempMap);
                }
            }
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 设置下一步审批人时，校验
     * @author houjinrong@chtwm.com
     * date 2018/6/6 18:57
     */
    public List<AssigneeTemp> validateSetNextAssignee(Task task, String processDefinitionKey, JSONArray jsonArray, String processInstanceId,String currentAssignee, String taskDefKeyBefore, int version){
        List<AssigneeTemp> result = Lists.newArrayList();

        String assignee = null;
        String candidateIds = null;
        EntityWrapper<TUserTask> wrapper;
        Integer appKey = getAppKey(processInstanceId);

        TUserTask userTask;
        String taskDefinitionKey;
        List<String> nextTaskDefKeys = findNextTaskDefKeys(task, false);
        for(int i = 0;i<jsonArray.size();i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            taskDefinitionKey = jsonObject.getString("taskDefinitionKey");
            if(!nextTaskDefKeys.contains(taskDefinitionKey)){
                log.info("任务节点KEY不匹配");
                return null;
            }

            wrapper = new EntityWrapper<>();
            wrapper.eq("proc_def_key", processDefinitionKey);
            wrapper.eq("version_", version);
            wrapper.eq("task_def_key", taskDefinitionKey);
            userTask = tUserTaskService.selectOne(wrapper);

            //key为任务节点key
            if(userTask == null){
                log.info("任务节点key不存在");
                return null;
            }

            log.info("节点配置信息：{}", JSONObject.toJSONString(userTask));

            //角色ID，多个逗号隔开
            candidateIds = userTask.getCandidateIds();
            String roleCode = null;
            String roleName = null;
            JSONArray assigneeArray = jsonObject.getJSONArray("assignee");
            if(assigneeArray == null || assigneeArray.size() == 0){
                //当选择的审批人为空时，审批人设置按配置处理
                AssigneeTemp assigneeTemp = new AssigneeTemp();
                assigneeTemp.setProcInstId(processInstanceId);
                assigneeTemp.setTaskDefKey(taskDefinitionKey);
                assigneeTemp.setCreator(currentAssignee);
                assigneeTemp.setDeleteFlag(0);
                assigneeTemp.setTaskDefKeyBefore(taskDefKeyBefore);
                assigneeTemp.setRoleCode(null);
                assigneeTemp.setRoleName(null);
                assigneeTemp.setAssigneeCode(null);
                assigneeTemp.setAssigneeName(null);
                result.add(assigneeTemp);
            }else{
                //选择多个审批人，分别遍历校验
                String userCode;
                String userName;
                for(int k=0;k<assigneeArray.size();k++){
                    userCode = assigneeArray.getJSONObject(k).getString("userCode");
                    userName = assigneeArray.getJSONObject(k).getString("userName");
                    assignee = assignee==null?userCode:assignee+","+userCode;
                    //获取用户所有所属角色
                    RbacDomainContext.getContext().setDomain(rbacKey);
                    List<RbacRole> roles = privilegeService.getAllRoleByUserId(appKey, userCode);
                    if(CollectionUtils.isEmpty(roles)){
                        log.info("用户【"+userCode+"】没有角色权限，无法匹配审批人资格");
                        return null;
                    }
                    log.info("审批人信息{}，审批人角色{}",candidateIds,JSONObject.toJSONString(roles));
                    if(CollectionUtils.isNotEmpty(roles)){
                        for(RbacRole r : roles){
                            if(candidateIds.indexOf(r.getId()+"") > -1){
                                roleCode = r.getId()+"";
                                roleName = r.getRoleName();
                                break;
                            }
                        }
                    }else{
                        log.info("未找到【"+userCode+"】的角色");
                        return null;
                    }

                    if(roleCode == null){
                        log.info("用户【"+userCode+"】没有权限");
                        return null;
                    }

                    AssigneeTemp assigneeTemp = new AssigneeTemp();
                    assigneeTemp.setProcInstId(processInstanceId);
                    assigneeTemp.setTaskDefKey(taskDefinitionKey);
                    assigneeTemp.setCreator(currentAssignee);
                    assigneeTemp.setDeleteFlag(0);
                    assigneeTemp.setTaskDefKeyBefore(taskDefKeyBefore);
                    assigneeTemp.setRoleCode(roleCode);
                    assigneeTemp.setRoleName(roleName);
                    assigneeTemp.setAssigneeCode(userCode);
                    assigneeTemp.setAssigneeName(userName);
                    result.add(assigneeTemp);
                }
            }
        }

        return result;
    }

    /**
     * 设置下一步审批人时，校验
     * @author houjinrong@chtwm.com
     * date 2018/6/6 18:57
     */
    @Deprecated
    public JSONObject validateSetNextAssignee(JSONArray jsonArray, String processInstanceId, int version){
        String assignee = null;
        String candidateIds = null;
        EntityWrapper<TUserTask> wrapper;
        Integer appKey = getAppKey(processInstanceId);

        JSONObject resultJson = new JSONObject();
        TUserTask userTask;
        String taskDefinitionKey;
        for(int i = 0;i<jsonArray.size();i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            taskDefinitionKey = jsonObject.getString("taskDefinitionKey");
            wrapper = new EntityWrapper<>();
            wrapper.eq("version_", version);
            wrapper.eq("task_def_key", taskDefinitionKey);
            userTask = tUserTaskService.selectOne(wrapper);

            //key为任务节点key
            if(userTask == null){
                log.info("任务节点key不存在");
                return null;
            }
            //角色ID，多个逗号隔开
            candidateIds = userTask.getCandidateIds();
            String roleCode = null;
            String roleName = null;
            JSONArray assigneeArray = jsonObject.getJSONArray("assignee");
            if(assigneeArray == null || assigneeArray.size() == 0){
                //当选择的审批人为空时，审批人设置按配置处理
                resultJson.put(taskDefinitionKey, null);
            }else{
                //选择多个角色，分别遍历校验
                String userCode;
                for(int k=0;k<assigneeArray.size();k++){
                    userCode = assigneeArray.getJSONObject(k).getString("userCode");
                    assignee = assignee==null?userCode:assignee+","+userCode;
                    //获取用户所有所属角色
                    RbacDomainContext.getContext().setDomain(rbacKey);
                    List<RbacRole> roles = privilegeService.getAllRoleByUserId(appKey, userCode);
                    if(CollectionUtils.isEmpty(roles)){
                        log.info("用户【"+userCode+"】没有角色权限，无法匹配审批人资格");
                        return null;
                    }
                    for(RbacRole r : roles){
                        if(candidateIds.indexOf(r.getId()+"") > -1){
                            roleCode = r.getId()+"";
                            roleName = r.getRoleName();
                            break;
                        }
                    }
                    if(roleCode == null){
                        log.info("用户【"+userCode+"】没有权限");
                        return null;
                    }
                }

                JSONObject assigneeJson = new JSONObject();
                assigneeJson.put("roleCode", roleCode);
                assigneeJson.put("roleName", roleName);
                assigneeJson.put("assignee", assignee);

                assigneeArray.add(assigneeJson);

                resultJson.put(taskDefinitionKey, assigneeArray);
            }
        }

        return resultJson;
    }

    /**
     * 获取角色编下的所有用户
     * @author houjinrong@chtwm.com
     * date 2018/6/6 20:16
     */
    private List<AssigneeVo> getAllUserByRoleCode(Integer appKey, Long roleCode){
        RbacDomainContext.getContext().setDomain(rbacKey);
        List<RbacUser> users = privilegeService.getUsersByRoleId(appKey, null, roleCode);
        if(CollectionUtils.isEmpty(users)){
            return null;
        }
        List<AssigneeVo> userCodeList = Lists.newArrayList();
        for(RbacUser user : users){
            AssigneeVo assigneeVo = new AssigneeVo();
            assigneeVo.setUserCode(user.getCode());
            assigneeVo.setUserName(user.getName());
            userCodeList.add(assigneeVo);
        }

        return userCodeList;
    }

    protected Integer getVersion(String processDefinitionId){
        //查询流程定义信息
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(processDefinitionId);
        return processDefinition==null?null:processDefinition.getVersion();
    }

    protected Integer getVersionByProcessInstanceId(String processInstanceId){
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        //查询流程定义信息
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(historicProcessInstance.getProcessDefinitionId());
        return processDefinition==null?null:processDefinition.getVersion();
    }

    /**
     * 设置代理人工号和名称
     * @param taskResult
     * @param assigneeVoList
     * @param assignees
     */
    protected void setAssigneeDelegate(TaskResult taskResult, List<AssigneeVo> assigneeVoList, List<String> assignees){
        if(CollectionUtils.isEmpty(assigneeVoList) || CollectionUtils.isEmpty(assignees)){
            return;
        }
        List<String> assigneeDelegates = Lists.newArrayList();
        List<String> assigneeNameDelegates = Lists.newArrayList();
        for(AssigneeVo assigneeVo : assigneeVoList){
            if(assignees.contains(assigneeVo.getUserCode())){
                assigneeDelegates.add(assigneeVo.getUserCode());
                assigneeNameDelegates.add(assigneeVo.getUserName());
            }
        }
        taskResult.setAssigneeDelegate(StringUtils.join(assigneeDelegates, ","));
        taskResult.setAssigneeNameDelegate(StringUtils.join(assigneeNameDelegates, ","));
    }

    /**
     * processInsrtanceId 流程实例ID
     * @author houjinrong@chtwm.com
     * date 2018/7/5 10:46
     */
    protected String getProcessCreator(String processInstanceId){
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        return historicProcessInstance.getStartUserId();
    }
}
