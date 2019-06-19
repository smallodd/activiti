package com.hengtian.flow.controller.rest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.common.common.CodeConts;
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
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.flow.controller.WorkflowBaseController;
import com.hengtian.flow.extend.TaskAdapter;
import com.hengtian.flow.model.TApprovalAgent;
import com.hengtian.flow.model.TAskTask;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.model.TWorkDetail;
import com.hengtian.flow.service.TApprovalAgentService;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.TRuTaskService;
import com.hengtian.flow.service.TUserTaskService;
import com.hengtian.flow.service.TWorkDetailService;
import com.hengtian.flow.service.WorkflowService;
import com.rbac.entity.RbacUser;
import com.rbac.service.PrivilegeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2018/4/12.
 * 所有涉及到操作类的功能都放到这里
 */
@Slf4j
@Controller
@RequestMapping("/rest/flow/operate")
public class WorkflowOperateController extends WorkflowBaseController {

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
    @Reference(version = "1.0.0")
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
        log.info("接口创建任务开始,方法【startProcessInstance】，请求参数{}", JSONObject.toJSONString(processParam));

        //校验参数是否合法
        Result result = processParam.validate();
        if (!result.isSuccess()) {
            log.info("参数不合法：{}",JSONObject.toJSONString(result));
            return result;
        } else {
            try {
                return workflowService.startProcessInstance(processParam);
            } catch (Exception e) {
                log.error("任务生成失败", e);
                result.setMsg("任务生成失败："+e.getMessage());
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
        log.info("设置审批人接口调用,方法【setAssignee】，参数{}", JSONObject.toJSONString(taskParam));
        Result result = new Result();
        if (StringUtils.isBlank(taskParam.getAssignee()) || StringUtils.isBlank(taskParam.getTaskId())) {
            log.info("参数不合法");
            result.setMsg("参数不合法");
            result.setCode(Constant.PARAM_ERROR);
            return result;
        }
//        if (!TaskTypeEnum.checkExist(taskParam.getTaskType())) {
//            log.info("任务类型不存在");
//            result.setCode(Constant.TASK_TYPE_ERROR);
//            result.setMsg("任务类型不正确");
//            result.setObj(TaskTypeEnum.getTaskTypeList());
//            return result;
//        }
//
//        if (!AssignTypeEnum.checkExist(taskParam.getAssignType())) {
//            log.info("审批人类型不存在");
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
        if (!Boolean.valueOf(map.get(ConstantUtils.SET_ASSIGNEE_FLAG).toString())) {
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
        log.info("任务审批方法开始，方法【approveTask】，taskParam：{}",JSONObject.toJSONString(taskParam));
        try {
            Task task = taskService.createTaskQuery().taskId(taskParam.getTaskId()).singleResult();
            if (task == null) {
                return renderError("任务不存在！", Constant.TASK_NOT_EXIT);
            }

            //查询是否当前审批人是否在当前结点有意见征询信息
            EntityWrapper entityWrapper = new EntityWrapper();
            entityWrapper.where("current_task_key={0}", task.getTaskDefinitionKey()).andNew("is_ask_end={0}", 0).andNew("ask_user_id={0}", taskParam.getAssignee()).andNew("proc_inst_id={0}",task.getProcessInstanceId());
            //查询是否有正在意见征询的节点
            TAskTask tAskTask = tAskTaskService.selectOne(entityWrapper);
            if (tAskTask != null) {
                return renderError("您的意见征询信息还未得到响应，不能审批通过", Constant.ASK_TASK_EXIT);
            }

            //查询当前任务节点审批人是不是当前人
            Object result = workflowService.approveTask(task, taskParam);
            log.info("任务审批方法执行结束出参：{}",JSONObject.toJSONString(result));
            return result;
        } catch (Exception e) {
            log.error("审批失败", e);
            return new Result(CodeConts.FAILURE, e.getMessage());
        }
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
        log.info("批量审批任务开始：入参pass:{}，jsonVariable:{},assignee:{}",pass,jsonVariable,assignee);
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
        log.info("批量审批任务结束，出参：{}",JSONObject.toJSONString(result));
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
        log.info("=================任务节点信息获取开始，方法【taskNodeDetail】,入参taskId:{}=====================", taskId);
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
                               @ApiParam(value = "类型是流程实例的还是任务的", name = "type", required = true, example = "1") @RequestParam("type") Integer type) {
        log.info("获取任务自定义信息或流程实例自定义信息开始，方法【getVariables】，入参taskId:{},type:{}",taskId,type);
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
        log.info("获取任务自定义信息或流程实例自定义信息开始，出参map:{}",map);
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
    public Object setVariables(@ApiParam(value = "任务id", name = "taskId", required = true) @RequestParam("taskId") String taskId,
                               @ApiParam(value = "自定义参数的json串", name = "jsonVariables", required = true) @RequestParam("jsonVariables") String jsonVariables,
                               @ApiParam(value = "类型", name = "type", required = true) @RequestParam("type") Integer type) {
        log.info("设置自定义参数开始，方法【setVariables】：入参taskId:{},jsonMap:{},type:{}",taskId,jsonVariables,type);
        if (StringUtils.isBlank(jsonVariables) || type == null || (type != 1 && type != 2)) {
            return renderError("参数错误", Constant.PARAM_ERROR);
        }
        try {
            Map map = JSONObject.parseObject(jsonVariables);
            if (type == 1) {
                taskService.setVariables(taskId, map);
            } else {
                taskService.setVariablesLocal(taskId, map);
            }
        } catch (Exception e) {
            return renderError("jsonMap参数错误！", Constant.PARAM_ERROR);
        }
        log.info("设置自定义参数完成");
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
        log.info("任务跳转方法执行开始，方法名【jumpTask】,入参：taskId:{},userId:{},taskDefinitionKey:{}",taskId,userId,taskDefinitionKey);
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.JUMP.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setTaskId(taskId);
        taskActionParam.setTargetTaskDefKey(taskDefinitionKey);
        Object result=taskAction(taskActionParam);
        log.info("任务跳转方法执行完成，出参：{}",JSONObject.toJSONString(result));
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
        log.info("任务转办，入参：taskId-{}，userId-{}，transferUserId-{}",taskId, userId, transferUserId);
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
        log.info("任务撤办，入参：processInstanceId-{}，userId-{}",processInstanceId, userId);
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.REVOKE.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setProcessInstanceId(processInstanceId);
        taskActionParam.setTaskId("-");
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
    @RequestMapping(value = "/process/suspend", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "挂起流程接口")
    public Object processSuspend(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") @RequestParam String processInstanceId,
                                 @ApiParam(name = "userId", required = true, value = "用户ID") @RequestParam String userId,
                                 @ApiParam(name = "needLog", required = true, value = "是否需要日志记录") @RequestParam boolean needLog) {
        log.info("挂起流程，入参：processInstanceId-{}，userId-{}，needLog-{}",processInstanceId, userId, needLog);
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.SUSPEND.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setProcessInstanceId(processInstanceId);
        return workflowService.processSuspend(taskActionParam, needLog);
    }

    /**
     * 激活流程
     *
     * @param processInstanceId 流程实例ID
     * @param userId            用户ID
     * @return
     */
    @SysLog(value = "激活流程")
    @RequestMapping(value = "/process/activate", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "激活流程接口")
    public Object processActivate(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") @RequestParam String processInstanceId,
                                  @ApiParam(name = "userId", required = true, value = "用户ID") @RequestParam String userId,
                                  @ApiParam(name = "needLog", required = true, value = "是否需要日志记录") @RequestParam boolean needLog) {
        log.info("激活流程，入参：processInstanceId-{}，userId-{}，needLog-{}",processInstanceId, userId, needLog);
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.ACTIVATE.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setProcessInstanceId(processInstanceId);
        return workflowService.processActivate(taskActionParam, needLog);
    }

    /**
     * 任务操作接口
     *
     * @param taskActionParam 请求类型 actionType
     *                        1跳转 jump
     *                        2转办 transfer
     *                        3催办 remind
     *                        4意见征询 enquire
     *                        5确认意见征询 confirmEnquire
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
        log.info("任务操作接口开始调用，方法名【taskAction】，入参{}",JSONObject.toJSONString(taskActionParam));
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
            /*Result re = validateTask(taskActionParam);
            if(!re.isSuccess()){
                return re;
            }*/
            TaskAdapter taskAdapter = new TaskAdapter();
            try {
                List<HistoricTaskInstance> historicTaskInstances=historyService.createHistoricTaskInstanceQuery().taskId(taskActionParam.getTaskId()).orderByTaskCreateTime().desc().list();
                Result result = taskAdapter.taskAction(taskActionParam);
                //存储操作记录
                if(result.isSuccess()){
                    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(taskActionParam.getProcessInstanceId()).singleResult();
                    TWorkDetail tWorkDetail = new TWorkDetail();
                    tWorkDetail.setCreateTime(new Date());
                    tWorkDetail.setDetail("工号【" + taskActionParam.getUserId() + "】进行了【" + TaskActionEnum.getDesc(taskActionParam.getActionType()) + "】操作");
                    tWorkDetail.setProcessInstanceId(taskActionParam.getProcessInstanceId());
                    tWorkDetail.setOperator(taskActionParam.getUserId());
                    tWorkDetail.setTaskId(taskActionParam.getTaskId());
                    if(StringUtils.isNotBlank(taskActionParam.getCommentResult())) {
                        tWorkDetail.setAprroveInfo(taskActionParam.getCommentResult());
                    }else{
                        tWorkDetail.setAprroveInfo(TaskActionEnum.getDesc(taskActionParam.getActionType()));
                    }

                    tWorkDetail.setOperateAction(TaskActionEnum.getDesc(taskActionParam.getActionType()));
                    tWorkDetail.setOperTaskKey(historicTaskInstances.get(0).getName());
                    tWorkDetail.setBusinessKey(processInstance.getBusinessKey());
                    tWorkDetailService.insert(tWorkDetail);
                }
                return result;
            } catch (Exception e) {
                log.error(TaskActionEnum.getDesc(taskActionParam.getActionType())+"失败：{}", e);
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

    /**
     * 意见征询
     *
     * @param processInstanceId 流程实例ID
     * @param commentResult     意见征询详情
     * @param currentTaskDefKey 当前任务节点KEY
     * @param targetTaskDefKey  目标任务节点KEY
     * @return
     */
    @PostMapping(value = "askTask")
    @ResponseBody
    public Object askTask(@RequestParam String processInstanceId,
                          @RequestParam String currentTaskDefKey,
                          @RequestParam String commentResult,
                          @RequestParam String targetTaskDefKey,
                          @RequestParam String askedUserId,
                          @RequestParam(required = false) String userId,
                          @RequestParam(required = false) String assigneeAgent) {
        log.info("意见征询接口开始执行，方法【askTask】，入参processInstanceId{},currentTaskDefKey{},commentResult{},targetTaskDefKey{},askedUserId{},userId{}",processInstanceId,currentTaskDefKey,commentResult,targetTaskDefKey,askedUserId,userId);
        try {
            if(StringUtils.isBlank(userId)&&getShiroUser()==null){
                return renderError("请传意见征询人员工号");
            }
            if(StringUtils.isBlank(processInstanceId)){
                return renderError("流程实例id不能为空");
            }
            if(StringUtils.isBlank(currentTaskDefKey)){
                return renderError("当前节点信息不能为空");
            }
            if(StringUtils.isBlank(commentResult)){
                return renderError("意见征询信息不能为空");
            }
            if(StringUtils.isBlank(targetTaskDefKey)){
                return renderError("被意见征询节点key不能为空");
            }
            if(StringUtils.isBlank(askedUserId)){
                return renderError("被意见征询人员");
            }
            if(StringUtils.isBlank(userId)){
                userId = getUserId();
            }
            /*Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(currentTaskDefKey).singleResult();
            boolean flag = validateTaskAssignee(task,userId);
            if(!flag){
                return new Result(false,Constant.FAIL, "当前用户没有操作此任务的权限");
            }*/
            return workflowService.taskEnquire(userId, processInstanceId, currentTaskDefKey, targetTaskDefKey, commentResult,askedUserId,assigneeAgent);
        } catch (Exception e) {
            log.error("", e);
            return new Result(false,Constant.FAIL, "操作失败");
        }
    }


    /**
     * 确认意见征询
     *
     * @param askId         意见征询ID
     * @param commentResult 回复
     * @return
     */
    @RequestMapping(value = "askConfirm", method = RequestMethod.POST)
    @ResponseBody
    public Result askConfirm(@RequestParam String askId,@RequestParam String userId,@RequestParam String commentResult ) {
        log.info("确认意见征询开始，方法【askConfirm】，入参：askId:{},userId:{},commentResult{}",askId,userId,commentResult);
        try {
            return workflowService.taskConfirmEnquire(userId, askId,commentResult);
        } catch (Exception e) {
            log.error("", e);
            return new Result(false, Constant.FAIL,"操作失败");
        }
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
                        return new Result(false,Constant.ASK_TASK_EXIT,"任务ID【"+taskActionParam.getTaskId()+"】没有对应的任务");
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
}
