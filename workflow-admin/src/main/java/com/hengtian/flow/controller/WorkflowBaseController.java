package com.hengtian.flow.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hengtian.common.base.BaseRestController;
import com.hengtian.common.enums.AssignTypeEnum;
import com.hengtian.common.result.TaskNodeResult;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.model.RuProcinst;
import com.hengtian.flow.model.TButton;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.*;
import com.rbac.entity.RbacRole;
import com.rbac.entity.RbacUser;
import com.rbac.service.PrivilegeService;
import com.rbac.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.activiti.engine.FormService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 基类
 * @author houjinrong@chtwm.com
 * date 2018/5/29 17:34
 */
@Slf4j
public class WorkflowBaseController extends BaseRestController {


    @Autowired
    private TaskService taskService;
    @Autowired
    private TRuTaskService tRuTaskService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TTaskButtonService tTaskButtonService;
    @Autowired
    private TUserTaskService tUserTaskService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private RuProcinstService ruProcinstService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    FormService formService;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    UserService userService;

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

    protected Set<String> getAssigneeUserByTaskId(String taskId){
        if(StringUtils.isBlank(taskId)){
            log.info("参数错误：taskId为空");
            return null;
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        return getAssigneeUserByTaskId(task);
    }

    /**
     * 获取任务办理人
     * @param task 任务
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/18 15:58
     */
    protected Set<String> getAssigneeUserByTaskId(TaskInfo task){
        if(task == null){
            log.info("任务不存在");
            return null;
        }

        //获取已审批人
        List<String> assigneeList = Lists.newArrayList();
        if(StringUtils.isNotBlank(task.getAssignee())){
            String assignee = task.getAssignee().replaceAll("_N","").replaceAll("_Y","");
            assigneeList = Arrays.asList(assignee.split(","));
        }

        //查询审批时该节点的执行execution
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) (processEngine.getRepositoryService().getProcessDefinition(task.getProcessDefinitionId()));

        EntityWrapper<TUserTask> wrapper_ = new EntityWrapper<>();
        wrapper_.where("task_def_key={0}", task.getTaskDefinitionKey()).andNew("version_={0}", definition.getVersion()).andNew("proc_def_key={0}", definition.getKey());

        TUserTask tUserTask = tUserTaskService.selectOne(wrapper_);

        Set<String> result = Sets.newHashSet();

        EntityWrapper<TRuTask> wrapper = new EntityWrapper<>();
        wrapper.where("task_id={0}", task.getId());
        List<TRuTask> tRuTasks = tRuTaskService.selectList(wrapper);

        if(tUserTask.getNeedSign() == 0 && AssignTypeEnum.ROLE.code.equals(tUserTask.getAssignType())){
            EntityWrapper<RuProcinst> _wrapper = new EntityWrapper<>();
            _wrapper.eq("proc_inst_id", task.getProcessInstanceId());
            RuProcinst ruProcinst = ruProcinstService.selectOne(_wrapper);
            Integer appKey = ruProcinst==null?null:ruProcinst.getAppKey();

            for(TRuTask t : tRuTasks){
                if(StringUtils.isNotBlank(t.getAssigneeReal())){
                    String[] array = t.getAssigneeReal().split(",");
                    for(String a : array){
                        if(!assigneeList.contains(a)){
                            result.add(a);
                        }
                    }
                }else{
                    List<RbacUser> users = privilegeService.getUsersByRoleId(appKey, null, Long.parseLong(t.getAssignee()));
                    if(CollectionUtils.isNotEmpty(users)){
                        for(RbacUser u : users){
                            if(!assigneeList.contains(u.getCode())){
                                result.add(u.getCode());
                            }
                        }
                    }
                }
            }
        }else{
            for(TRuTask t : tRuTasks){
                if(StringUtils.isNotBlank(t.getAssigneeReal())){
                    String[] array = t.getAssigneeReal().split(",");
                    for(String a : array){
                        if(!assigneeList.contains(a)){
                            result.add(a);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * 获取任务办理人
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/18 15:58
     */
    protected JSONArray getAssigneeUserTreeByTaskId(String taskId){
        if(StringUtils.isBlank(taskId)){
            return null;
        }

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task == null){
            return null;
        }

        JSONArray json = new JSONArray();
        EntityWrapper<TRuTask> wrapper = new EntityWrapper<>();
        wrapper.where("task_id={0}", taskId);
        List<TRuTask> tRuTasks = tRuTaskService.selectList(wrapper);
        Integer appKey = runtimeService.getVariable(task.getExecutionId(), "appKey", Integer.class);
        for(TRuTask t : tRuTasks){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", t.getAssignee());
            jsonObject.put("text", t.getAssigneeName());
            if(AssignTypeEnum.ROLE.code.equals(t.getAssigneeType()) || AssignTypeEnum.EXPR.code.equals(t.getAssigneeType())){
                JSONArray jsonArray = new JSONArray();
                if(StringUtils.isNotBlank(t.getAssigneeReal())){
                    String[] array = t.getAssigneeReal().split(",");
                    for(String a : array){
                        JSONObject child = new JSONObject();
                        child.put("id", t.getAssignee()+":"+a);
                        RbacUser user = userService.getUserById(a);
                        child.put("text", user == null?a:user.getName());
                        if(!jsonObject.containsKey("children")){
                            jsonArray.add(child);
                            jsonObject.put("children", jsonArray);
                        }else{
                            jsonObject.accumulate("children", child);
                        }
                    }
                }else{
                    if(AssignTypeEnum.ROLE.code.equals(t.getAssigneeType())){
                        List<RbacUser> users = privilegeService.getUsersByRoleId(appKey, "", Long.parseLong(t.getAssignee()));
                        if(CollectionUtils.isNotEmpty(users)){
                            for(RbacUser u : users){
                                JSONObject child = new JSONObject();
                                child.put("id", t.getAssignee()+":"+u.getCode());
                                child.put("text", u.getName());
                                if(!jsonObject.containsKey("children")){
                                    jsonArray.add(child);
                                    jsonObject.put("children", jsonArray);
                                }else{
                                    jsonObject.accumulate("children", child);
                                }
                            }
                        }
                    }
                }
                if(jsonObject.containsKey("children")){
                    json.add(jsonObject);
                }
            } else {
                json.add(jsonObject);
            }
        }

        return json;
    }

    public TaskNodeResult setButtons(TaskNodeResult taskNodeResult){

            String id=taskNodeResult.getProcessInstanceId();
        ProcessInstance processInstance=runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();


                List<TButton> tButtons = tTaskButtonService.selectTaskButtons( processInstance.getProcessDefinitionKey(),taskNodeResult.getTaskDefinedKey());
        TaskFormData taskFormData=formService.getTaskFormData(taskNodeResult.getTaskId());
        if(taskFormData!=null){
            taskNodeResult.setFormKey(taskFormData.getFormKey());
        }
                taskNodeResult.setButtonKeys(tButtons);


        return  taskNodeResult;
    }

    /**
     * 查询代理人
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/21 18:04
     */
    public void setAssigneeAndRole(PageInfo pageInfo, String assignee, int appKey){
        List<RbacRole> roles = privilegeService.getAllRoleByUserId(appKey, assignee);
        pageInfo.getCondition().put("assignee", assignee);
        String roleIds = null;
        for(RbacRole role : roles){
            roleIds = roleIds == null?role.getId()+"":roleIds+","+role.getId();
        }
        Map<String, Object> condition = Maps.newHashMap(pageInfo.getCondition());
        condition.put("roleId", roleIds);
        pageInfo.setCondition(condition);
    }

    /**
     * 验证审批人权限
     * @param task
     * @param assignee
     * @return
     */
    public boolean validateTaskAssignee(TaskInfo task, String assignee){
        EntityWrapper<TRuTask> wrapper = new EntityWrapper<>();
        wrapper.where("task_id={0}", task.getId());

        List<TRuTask> tRuTasks = tRuTaskService.selectList(wrapper);
        for(TRuTask rt : tRuTasks){
            if(StringUtils.isNotBlank(rt.getAssigneeReal())){
                if(rt.getAssigneeReal().indexOf(assignee) > -1){
                    return true;
                }
            }else{
                if(AssignTypeEnum.ROLE.code.equals(rt.getAssigneeType())){
                    List<RbacUser> users = privilegeService.getUsersByRoleId(rt.getAppKey(), null, Long.parseLong(rt.getAssignee()));
                    for(RbacUser u : users){
                        if(u.getCode().equals(assignee)){
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
