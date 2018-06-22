package com.hengtian.flow.controller.rest;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.hengtian.common.enums.AssignTypeEnum;
import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.ProcessParam;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.param.TaskParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import com.hengtian.common.result.TaskNodeResult;
import com.hengtian.flow.controller.WorkflowBaseController;
import com.hengtian.flow.extend.TaskAdapter;
import com.hengtian.flow.model.*;
import com.hengtian.flow.service.*;
import com.rbac.entity.RbacUser;
import com.rbac.service.PrivilegeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by ma on 2018/4/12.
 * 所有涉及到操作类的功能都放到这里
 */
@Controller
@RequestMapping("/rest/flow/operate")
public class WorkflowOperateController extends WorkflowBaseController {
    private Logger logger = LoggerFactory.getLogger(WorkflowOperateController.class);

    @Autowired
    TaskService taskService;
    @Autowired
    TAskTaskService tAskTaskService;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    TRuTaskService tRuTaskService;
    @Autowired
    TUserTaskService tUserTaskService;
    @Autowired
    TApprovalAgentService tApprovalAgentService;
    @Autowired
    HistoryService historyService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TWorkDetailService tWorkDetailService;
    @Autowired
    PrivilegeService privilegeService;
    /**
     * 任务创建接口
     *
     * @param processParam
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    @SysLog("接口创建任务操作")
    @ApiOperation(httpMethod = "POST", value = "生成任务接口")
    public Result startProcessInstance(@ApiParam(value = "创建任务必传信息", name = "processParam", required = true) @ModelAttribute ProcessParam processParam) {
        logger.info("接口创建任务开始,方法【startProcessInstance】，请求参数{}", JSONObject.toJSONString(processParam));

        //校验参数是否合法
        Result result = processParam.validate();
        if (!result.isSuccess()) {
            return result;
        } else {
            try {
                return workflowService.startProcessInstance(processParam);
            } catch (Exception e) {
                logger.error("任务生成失败", e);
                result.setMsg("任务生成失败，请联系管理员进行排查！");
                result.setCode(Constant.FAIL);
                result.setSuccess(false);
                return result;

            }
        }

    }

    /**
     * 设置审批人接口
     *
     * @param taskParam
     * @return
     */
    @RequestMapping(value = "setAssignee", method = RequestMethod.POST)
    @ResponseBody
    @SysLog("设置审批人接口")
    @ApiOperation(httpMethod = "POST", value = "设置审批人接口")
    public Object setAssignee(@ApiParam(value = "设置审批人信息", name = "taskParam", required = true) @ModelAttribute TaskParam taskParam) {
        logger.info("设置审批人接口调用,方法【setAssignee】，参数{}", JSONObject.toJSONString(taskParam));
        Result result = new Result();
        if (StringUtils.isBlank(taskParam.getAssignee()) || StringUtils.isBlank(taskParam.getTaskId())) {
            logger.info("参数不合法");
            result.setMsg("参数不合法");
            result.setCode(Constant.PARAM_ERROR);
            return result;
        }
//        if (!TaskTypeEnum.checkExist(taskParam.getTaskType())) {
//            logger.info("任务类型不存在");
//            result.setCode(Constant.TASK_TYPE_ERROR);
//            result.setMsg("任务类型不正确");
//            result.setObj(TaskTypeEnum.getTaskTypeList());
//            return result;
//        }
//
//        if (!AssignTypeEnum.checkExist(taskParam.getAssignType())) {
//            logger.info("审批人类型不存在");
//            result.setCode(Constant.ASSIGN_TYPE_ERROR);
//            result.setMsg("审批人类型不正确");
//            result.setObj(AssignTypeEnum.getList());
//            return result;
//        }
        Task task = taskService.createTaskQuery().taskId(taskParam.getTaskId()).singleResult();
        if (task == null) {
            result.setMsg("任务不存在！");
            result.setCode(Constant.TASK_NOT_EXIT);
            result.setSuccess(false);
            return result;
        }
        //判断此节点可以设置审批人
        Map<String, Object> map = taskService.getVariables(task.getId());
        if (!Boolean.valueOf(map.get("customApprover").toString())) {
            return renderError("此任务不可以设置审批人！审批人由操作后台设置", Constant.PARAM_ERROR);
        }
        ProcessInstance processInstance=runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        EntityWrapper entityWrapper=new EntityWrapper();
        entityWrapper.where("proc_def_key={0}",processInstance.getProcessDefinitionKey()).andNew("task_def_key={0}",task.getTaskDefinitionKey()).andNew("version_={0}",processInstance.getProcessDefinitionVersion());
        TUserTask tUserTask=tUserTaskService.selectOne(entityWrapper);

        tUserTask.setCandidateIds(taskParam.getAssignee());
        Boolean flag = workflowService.setAssignee(task, tUserTask);
        if (flag) {
            result.setMsg("设置成功！");
            result.setCode(Constant.SUCCESS);
            result.setSuccess(true);
        } else {
            result.setMsg("设置失败，请联系管理员！");
            result.setCode(Constant.FAIL);
        }
        return result;
    }

    /**
     * 审批任务接口
     *
     * @param taskParam 任务信息对象 @TaskParam
     * @return
     */
    @RequestMapping(value = "approveTask", method = RequestMethod.POST)
    @ResponseBody
    @SysLog("审批任务接口")
    @ApiOperation(httpMethod = "POST", value = "审批任务接口")
    public Object approveTask(@ModelAttribute("taskParam") @Valid TaskParam taskParam) {
        logger.info("任务审批方法开始，方法【approveTask】，taskParam：{}",JSONObject.toJSONString(taskParam));
        Task task = taskService.createTaskQuery().taskId(taskParam.getTaskId()).singleResult();
        if (task == null) {
            return renderError("任务不存在！", Constant.TASK_NOT_EXIT);
        }
        //查询是否当前审批人是否在当前结点有问询信息
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.where("current_task_key={0}", task.getTaskDefinitionKey()).andNew("is_ask_end={0}", 0).andNew("ask_user_id={0}", taskParam.getAssignee()).andNew("proc_inst_id={0}",task.getProcessInstanceId());
        //查询是否有正在问询的节点
        TAskTask tAskTask = tAskTaskService.selectOne(entityWrapper);
        if (tAskTask != null) {
            return renderError("您的问询信息还未得到响应，不能审批通过", Constant.ASK_TASK_EXIT);
        }
        //查询当前任务节点审批人是不是当前人
        Object result=workflowService.approveTask(task, taskParam);
        logger.info("任务审批方法执行结束出参：{}",JSONObject.toJSONString(result));
        return result;
    }

    /**
     * 批量审批接口
     *
     * @param taskIds      任务id字符串，用","隔开
     * @param pass         类型 1是通过，2是拒绝 3是通过自定义参数流转
     * @param jsonVariable 参数map
     * @param assignee     审批人
     * @return
     */
    @RequestMapping(value = "approveTaskBatch", method = RequestMethod.POST)
    @ResponseBody
    @SysLog("批量审批任务接口")
    @ApiOperation(httpMethod = "POST", value = "批量审批任务接口")
    public Object approveTaskBatch(@ApiParam(value = "任务id列表，用','隔开", name = "taskIds", required = true) @RequestParam("taskIds") String taskIds,
                                   @ApiParam(value = "1是通过，2是拒绝，3通过自定义参数流转", name = "pass", required = true) @RequestParam("pass") Integer pass,
                                   @ApiParam(value = "自定义参数流转", name = "jsonVariable", required = false, example = "{'a':'b'}") @RequestParam(value = "jsonVariable", required = false) String jsonVariable,
                                   @ApiParam(value = "审批人信息", name = "assignee", required = true) @RequestParam("assignee") String assignee) {
        Map map = JSONObject.parseObject(jsonVariable);
        Result result = new Result();
        result.setMsg("审批成功");
        result.setSuccess(true);
        result.setCode(Constant.SUCCESS);
        if (StringUtils.isBlank(taskIds) || pass == null) {
            return renderError("请传正确的参数！", Constant.PARAM_ERROR);
        }
        String[] array = taskIds.split(",");
        for (String taskId : array) {

            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                return renderError("请传正确的参数！【" + taskId + "】任务不存在！", Constant.PARAM_ERROR);
            }
            TaskParam taskParam = new TaskParam();
            taskParam.setTaskId(taskId);
            taskParam.setPass(pass);
            taskParam.setJsonVariables(jsonVariable);
            taskParam.setAssignee(assignee);
            workflowService.approveTask(task, taskParam);
        }
        return result;
    }

    /**
     * 获取任务节点信息
     *
     * @param taskId
     * @return
     */
    @SysLog(value = "获取任务节点信息")
    @RequestMapping(value = "taskNodeDetail", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "获取任务节点信息")
    public Object taskNodeDetail(@ApiParam(value = "任务id", name = "taskId", required = true) @RequestParam("taskId") String taskId) {
        logger.info("=================任务节点信息获取开始，方法【taskNodeDetail】=====================");
        logger.info("=================入参taskId:{}=====================",taskId);
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return renderError("任务不存在！", Constant.TASK_NOT_EXIT);
        }

        return setButtons( TaskNodeResult.toTaskNodeResult(task));
    }

    /**
     * 获取任务自定义信息或流程实例自定义信息
     *
     * @param taskId
     * @param type
     * @return
     */
    @SysLog(value = "获取任务自定义信息或流程实例自定义信息")
    @RequestMapping(value = "getVariables", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "获取任务自定义信息或流程实例自定义信息")
    public Object getVariables(@ApiParam(value = "任务id", name = "taskId", required = true) @RequestParam("taskId") String taskId,
                               @ApiParam(value = "类型是流程实例的还是任务的", name = "type", required = true, example = "1是流程实例，2是任务的") @RequestParam("type") Integer type) {
        logger.info("获取任务自定义信息或流程实例自定义信息开始，方法【getVariables】，入参taskId:{},type:{}",taskId,type);
        if (type == null || (type != 1 && type != 2)) {
            return renderError("参数不正确，类型不存在！", Constant.PARAM_ERROR);
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return renderError("参数不正确，任务不存在！", Constant.TASK_NOT_EXIT);
        }
        Map map = new HashMap();
        if (type == 1) {
            map = taskService.getVariables(taskId);
        } else {
            map = taskService.getVariablesLocal(taskId);
        }
        logger.info("获取任务自定义信息或流程实例自定义信息开始，出参map:{}",map);
        return resultSuccess("操作成功", map);
    }

    /**
     * 设置自定义参数
     *
     * @param taskId 任务id
     * @return
     */
    @SysLog(value = "设置自定义参数")
    @RequestMapping(value = "setVariables", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "设置自定义参数")
    public Object setVariables(@ApiParam(value = "任务id", name = "taskId", required = true) @RequestParam("taskId") String taskId, @ApiParam(value = "自定义参数的json串", name = "jsonMap", required = true) @RequestParam("jsonMap") String jsonMap, @ApiParam(value = "类型", name = "type", required = true) @RequestParam("type") Integer type) {
        logger.info("设置自定义参数开始，方法【setVariables】：入参taskId:{},jsonMap:{},type:{}",taskId,jsonMap,type);
        if (StringUtils.isBlank(jsonMap) || type == null || (type != 1 && type != 2)) {
            return renderError("参数错误", Constant.PARAM_ERROR);
        }
        try {
            Map map = JSONObject.parseObject(jsonMap);
            if (type == 1) {
                taskService.setVariables(taskId, map);
            } else {
                taskService.setVariablesLocal(taskId, map);
            }
        } catch (Exception e) {
            return renderError("jsonMap参数错误！", Constant.PARAM_ERROR);
        }
        logger.info("设置自定义参数完成");
        return renderSuccess();
    }

    /**
     * 任务跳转
     *
     * @param taskId            任务ID
     * @param userId            任务原所属用户ID
     * @param taskDefinitionKey 任务节点KEY
     * @return
     */
    @SysLog(value = "任务跳转")
    @RequestMapping(value = "jumpTask", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "任务跳转接口")
    public Object jumpTask(@ApiParam(name = "taskId", required = true, value = "任务ID") @RequestParam String taskId,
                           @ApiParam(name = "userId", required = true, value = "任务原所属用户ID") @RequestParam String userId,
                           @ApiParam(name = "taskDefinitionKey", required = true, value = "任务节点KEY") @RequestParam String taskDefinitionKey) {
        logger.info("任务跳转方法执行开始，方法名【jumpTask】,入参：taskId:{},userId:{},taskDefinitionKey:{}",taskId,userId,taskDefinitionKey);
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.JUMP.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setTaskId(taskId);
        taskActionParam.setTargetTaskDefKey(taskDefinitionKey);
        Object result=taskAction(taskActionParam);
        logger.info("任务跳转方法执行完成，出参：{}",JSONObject.toJSONString(result));
        return result;
    }

    /**
     * 任务转办
     *
     * @param taskId         任务ID
     * @param userId         任务原所属用户ID
     * @param transferUserId 任务要转办用户ID
     * @return
     */
    @SysLog(value = "任务转办")
    @RequestMapping(value = "transferTask", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "任务转办接口")
    public Object transferTask(@ApiParam(name = "taskId", required = true, value = "任务ID") @RequestParam String taskId,
                               @ApiParam(name = "userId", required = true, value = "任务原所属用户ID") @RequestParam String userId,
                               @ApiParam(name = "transferUserId", required = true, value = "任务要转办用户ID") @RequestParam String transferUserId) {
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.TRANSFER.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setTargetUserId(transferUserId);
        taskActionParam.setTaskId(taskId);
        return taskAction(taskActionParam);
    }


    /**
     * 任务撤办
     *
     * @param processInstanceId 流程实例ID
     * @param userId            用户ID
     * @return
     */
    @SysLog(value = "任务撤办")
    @RequestMapping(value = "revokeProcessInstance", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "任务撤办接口")
    public Object revokeProcessInstance(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") @RequestParam String processInstanceId,
                                        @ApiParam(name = "userId", required = true, value = "用户ID") @RequestParam String userId) {
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.REVOKE.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setProcessInstanceId(processInstanceId);
        return taskAction(taskActionParam);
    }

    /**
     * 挂起流程
     *
     * @param processInstanceId 流程实例ID
     * @param userId            用户ID
     * @return
     */
    @SysLog(value = "挂起流程")
    @RequestMapping(value = "suspendProcess", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "挂起流程接口")
    public Object suspendProcess(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") @RequestParam String processInstanceId,
                                 @ApiParam(name = "userId", required = true, value = "用户ID") @RequestParam String userId) {
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.SUSPEND.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setProcessInstanceId(processInstanceId);
        return taskAction(taskActionParam);
    }

    /**
     * 激活流程
     *
     * @param processInstanceId 流程实例ID
     * @param userId            用户ID
     * @return
     */
    @SysLog(value = "激活流程")
    @RequestMapping(value = "activateProcess", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "激活流程接口")
    public Object activateProcess(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") @RequestParam String processInstanceId,
                                  @ApiParam(name = "userId", required = true, value = "用户ID") @RequestParam String userId) {
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.ACTIVATE.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setProcessInstanceId(processInstanceId);
        return taskAction(taskActionParam);
    }

    /**
     * 任务操作接口
     *
     * @param taskActionParam 请求类型 actionType
     *                        1跳转 jump
     *                        2转办 transfer
     *                        3催办 remind
     *                        4问询 enquire
     *                        5确认问询 confirmEnquire
     *                        6撤回 revoke
     *                        7取消 cancel
     *                        8挂起流程 suspend
     *                        9激活流程 activate
     * @return result
     * @author houjinrong@chtwm.com
     * date 2018/4/18 9:38
     */
    @RequestMapping(value = "/option", method = RequestMethod.POST)
    @ResponseBody
    @SysLog("任务操作接口")
    @ApiOperation(httpMethod = "POST", value = "任务操作接口")
    public Object taskAction(@ApiParam(name = "taskActionParam", required = true, value = "操作类型参数") @ModelAttribute TaskActionParam taskActionParam) {
        logger.info("任务操作接口开始调用，方法名【taskAction】，入参{}",JSONObject.toJSONString(taskActionParam));
        String actionType = taskActionParam.getActionType();
        if (StringUtils.isBlank(actionType)) {
            return renderError("操作类型不能为空");
        } else if (StringUtils.isBlank(taskActionParam.getUserId())) {
            return renderError("操作人工号不能为空");
        } else if (StringUtils.isBlank(taskActionParam.getProcessInstanceId())) {
            return renderError("流程实例ID不能为空");
        }

        //参数校验
        Result validate = taskActionParam.validate();
        if (validate.isSuccess()) {
            //校验操作人权限
            Result re = validateTask(taskActionParam);
            if(!re.isSuccess()){
                return re;
            }
            TaskAdapter taskAdapter = new TaskAdapter();
            try {
                Result result = taskAdapter.taskAction(taskActionParam);
                //存储操作记录
                if(result.isSuccess()){
                    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(taskActionParam.getProcessInstanceId()).singleResult();
                    TWorkDetail tWorkDetail = new TWorkDetail();
                    tWorkDetail.setCreateTime(new Date());
                    tWorkDetail.setDetail("工号为【" + taskActionParam.getUserId() + "】的员工进行了【" + TaskActionEnum.getDesc(taskActionParam.getActionType()) + "】操作");
                    tWorkDetail.setProcessInstanceId(taskActionParam.getProcessInstanceId());
                    tWorkDetail.setOperator(taskActionParam.getUserId());
                    tWorkDetail.setTaskId(taskActionParam.getTaskId());
                    tWorkDetail.setAprroveInfo(taskActionParam.getCommentResult());
                    List<HistoricTaskInstance> historicTaskInstances=historyService.createHistoricTaskInstanceQuery().taskId(taskActionParam.getTaskId()).orderByTaskCreateTime().desc().list();
                    tWorkDetail.setOperateAction(TaskActionEnum.getDesc(taskActionParam.getActionType()));
                    tWorkDetail.setOperTaskKey(historicTaskInstances.get(0).getName());
                    tWorkDetail.setBusinessKey(processInstance.getBusinessKey());
                    tWorkDetailService.insert(tWorkDetail);
                }
                return result;
            } catch (Exception e) {
                logger.error(TaskActionEnum.getDesc(taskActionParam.getActionType())+"失败：{}", e);
                return renderError("操作失败");
            }
        } else {
            return validate;
        }
    }

    /**
     * 委派授权
     * @param approvalAgent
     * @return
     */
    @RequestMapping(value = "/delegate", method = RequestMethod.POST)
    @ResponseBody
    public Object delegate(TApprovalAgent approvalAgent){
        if(approvalAgent.getBeginTime()==null||approvalAgent.getEndTime()==null||StringUtils.isBlank(approvalAgent.getClient())||StringUtils.isBlank(approvalAgent.getAgent())){
            return renderError("参数不正确",Constant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper=new EntityWrapper();
        entityWrapper.where("client={0}",approvalAgent.getClient()).andNew("agent={0}",approvalAgent.getAgent()).andNew("status={0}",0);
        TApprovalAgent tApprovalAgent=tApprovalAgentService.selectOne(entityWrapper);
        if(tApprovalAgent!=null){
            return renderError("该代理已经设置过",Constant.AGENT_HAVE_EXIST);
        }
        approvalAgent.setStatus(0);
        tApprovalAgentService.insert(approvalAgent);

        return renderSuccess();
    }

    private Result validateTask(TaskActionParam taskActionParam){
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(taskActionParam.getProcessInstanceId()).list();
        if(CollectionUtils.isNotEmpty(taskList)){
            if(StringUtils.isNotBlank(taskActionParam.getTaskId())){
                if(TaskActionEnum.REVOKE.value.equals(taskActionParam.getActionType())){
                    //撤回时taskId为已办理完的任务ID
                }else{
                    boolean b = false;
                    for(Task t : taskList){
                        if(t.getId().equals(taskActionParam.getTaskId())){
                            b = true;
                            break;
                        }
                    }
                    if(!b){
                        return new Result(false,Constant.ASK_TASK_EXIT,"流程实例ID与任务ID不对应");
                    }

                    EntityWrapper<TRuTask> wrapper = new EntityWrapper();
                    wrapper.where("task_id={0}", taskActionParam.getTaskId());
                    List<TRuTask> tRuTasks = tRuTaskService.selectList(wrapper);
                    List<String> assigneeList = Lists.newArrayList();
                    for(TRuTask rt : tRuTasks){
                        if(StringUtils.isNotBlank(rt.getAssigneeReal())){
                            assigneeList.addAll(Arrays.asList(rt.getAssigneeReal().split(",")));
                        }else if(AssignTypeEnum.ROLE.code.equals(rt.getAssigneeType())){
                            Integer appKey = runtimeService.getVariable(taskActionParam.getProcessInstanceId(), "appKey", Integer.class);
                            List<RbacUser> users = privilegeService.getUsersByRoleId(appKey, null, Long.parseLong(rt.getAssignee()));
                            for(RbacUser u : users){
                                assigneeList.add(u.getCode());
                            }
                        }
                    }
                    if(!assigneeList.contains(taskActionParam.getUserId())){
                        return new Result(false,Constant.AGENT_HAVE_EXIST,"用户【"+taskActionParam.getUserId()+"】没有权限进行该操作");
                    }
                }
            }
        }else{
            return new Result(false,Constant.ASK_TASK_EXIT,"流程实例ID无效或没有可撤回的任务");
        }

        return new Result(true,Constant.SUCCESS,"用户【"+taskActionParam.getUserId()+"】"+TaskActionEnum.getDesc(taskActionParam.getActionType())+"成功");
    }


    /**
     * 问询
     *
     * @param processInstanceId 流程实例ID
     * @param commentResult     问询详情
     * @param currentTaskDefKey 当前任务节点KEY
     * @param targetTaskDefKey  目标任务节点KEY
     * @return
     */
    @PostMapping(value = "askTask")
    @ResponseBody
    public Object askTask(@RequestParam String processInstanceId, @RequestParam String currentTaskDefKey, @RequestParam String commentResult, @RequestParam String targetTaskDefKey,@RequestParam String askedUserId,@RequestParam(required = false) String userId) {
        logger.info("问询接口开始执行，方法【askTask】，入参processInstanceId{},currentTaskDefKey{},commentResult{},targetTaskDefKey{},askedUserId{},userId{}",processInstanceId,currentTaskDefKey,commentResult,targetTaskDefKey,askedUserId,userId);
        try {
            if(com.hengtian.common.utils.StringUtils.isBlank(userId)&&getShiroUser()==null){
                return renderError("请传问询人员工号");
            }
            if(com.hengtian.common.utils.StringUtils.isBlank(processInstanceId)){
                return renderError("流程实例id不能为空");
            }
            if(com.hengtian.common.utils.StringUtils.isBlank(currentTaskDefKey)){
                return renderError("当前节点信息不能为空");
            }
            if(com.hengtian.common.utils.StringUtils.isBlank(commentResult)){
                return renderError("问询信息不能为空");
            }
            if(com.hengtian.common.utils.StringUtils.isBlank(targetTaskDefKey)){
                return renderError("被问询节点key不能为空");
            }
            if(com.hengtian.common.utils.StringUtils.isBlank(askedUserId)){
                return renderError("被问询人员");
            }
            if(com.hengtian.common.utils.StringUtils.isBlank(userId)){
                userId=getUserId();
            }
            Task task=taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(currentTaskDefKey).singleResult();
          boolean flag=  validateTaskAssignee(task,userId);
          if(!flag){
              return new  Result(false,Constant.FAIL, "当前用户没有操作此任务的权限");
          }
            return workflowService.taskEnquire(userId, processInstanceId, currentTaskDefKey, targetTaskDefKey, commentResult,askedUserId);
        } catch (Exception e) {
            logger.error("", e);
            return new Result(false,Constant.FAIL, "操作失败");
        }
    }


    /**
     * 确认问询
     *
     * @param askId         问询ID
     * @param commentResult 回复
     * @return
     */
    @RequestMapping(value = "askConfirm", method = RequestMethod.POST)
    @ResponseBody
    public Result askConfirm(@RequestParam String askId,@RequestParam String userId,@RequestParam String commentResult ) {
        logger.info("确认问询开始，方法【askConfirm】，入参：askId:{},userId:{},commentResult{}",askId,userId,commentResult);
        try {
            return workflowService.taskConfirmEnquire(userId, askId,commentResult);
        } catch (Exception e) {
            logger.error("", e);
            return new Result(false, Constant.FAIL,"操作失败");
        }
    }
}
