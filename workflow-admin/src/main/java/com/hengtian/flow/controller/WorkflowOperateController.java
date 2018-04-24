package com.hengtian.flow.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.enums.AssignType;
import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.enums.TaskType;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.ProcessParam;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.param.TaskParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import com.hengtian.common.result.TaskNodeResult;
import com.hengtian.flow.extend.TaskAdapter;
import com.hengtian.flow.model.TAskTask;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.TRuTaskService;
import com.hengtian.flow.service.TUserTaskService;
import com.hengtian.flow.service.WorkflowService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ValuedDataObject;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    /**
     * 任务创建接口
     *
     * @param processParam
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    @SysLog("接口创建任务操作")
    @ApiOperation(httpMethod = "POST", value = "生成任务接口")
    public Result startProcessInstance(@ApiParam(value = "创建任务必传信息", name = "processParam", required = true) @RequestBody ProcessParam processParam) {
        logger.info("接口创建任务开始，请求参数{}", JSONObject.toJSONString(processParam));

        //校验参数是否合法
        Result result = processParam.validate();
        if (!result.isSuccess()) {

            return result;
        } else {

           return workflowService.startProcessInstance(processParam);
        }

    }

    /**
     * 设置审批人接口
     *
     * @param taskParam
     * @return
     */
    @RequestMapping(value = "setApprover", method = RequestMethod.POST)
    @ResponseBody
    @SysLog("设置审批人接口")
    @ApiOperation(httpMethod = "POST", value = "设置审批人接口")
    public Object setApprover(@ApiParam(value = "设置审批人信息", name = "taskParam", required = true) @RequestBody TaskParam taskParam) {
        logger.info("设置审批人接口调用，参数{}", JSONObject.toJSONString(taskParam));
        Result result = new Result();
        if (StringUtils.isBlank(taskParam.getApprover()) || taskParam.getAssignType() == null || StringUtils.isBlank(taskParam.getTaskType()) || StringUtils.isBlank(taskParam.getTaskId())) {
            logger.info("参数不合法");
            result.setMsg("参数不合法");
            result.setCode(Constant.PARAM_ERROR);
            return result;
        }
        if (!TaskType.checkExist(taskParam.getTaskType())) {
            logger.info("任务类型不存在");
            result.setCode(Constant.TASK_TYPE_ERROR);
            result.setMsg("任务类型不正确");
            result.setObj(TaskType.getTaskTypeList());
            return result;
        }

        if (!AssignType.checkExist(taskParam.getAssignType())) {
            logger.info("审批人类型不存在");
            result.setCode(Constant.ASSIGN_TYPE_ERROR);
            result.setMsg("审批人类型不正确");
            result.setObj(AssignType.getList());
            return result;
        }
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
        TUserTask tUserTask = new TUserTask();
        tUserTask.setAssignType(taskParam.getAssignType());
        tUserTask.setTaskType(taskParam.getTaskType());
        tUserTask.setCandidateIds(taskParam.getApprover());
        Boolean flag = workflowService.setApprover(task, tUserTask);
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
     * @param taskParam  任务信息对象 @TaskParam
     * @return
     */
    @RequestMapping(value = "approveTask", method = RequestMethod.POST)
    @ResponseBody
    @SysLog("审批任务接口")
    @ApiOperation(httpMethod = "POST", value = "审批任务接口")
    public Object approveTask(@RequestBody TaskParam taskParam) {

        Task task = taskService.createTaskQuery().taskId(taskParam.getTaskId()).singleResult();
        if (task == null) {
            return renderError("任务不存在！", Constant.TASK_NOT_EXIT);
        }
        //查询是否当前审批人是否在当前结点有问询信息
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.where("current_task_key={0}", task.getTaskDefinitionKey()).andNew("is_ask_end={0}", 0).andNew("ask_user_id={0}", taskParam.getApprover());
        //查询是否有正在问询的节点
        TAskTask tAskTask = tAskTaskService.selectOne(entityWrapper);
        if (tAskTask != null) {

            return renderError("您的问询信息还未得到响应，不能审批通过", Constant.ASK_TASK_EXIT);
        }
        //查询当前任务节点审批人是不是当前人

        return workflowService.approveTask(task, taskParam);


    }

    /**
     * 批量审批接口
     * @param taskIds  任务id字符串，用","隔开
     * @param type     类型 1是通过，2是拒绝 3是通过自定义参数流转
     * @param jsonVariable   参数map
     * @param approver    审批人
     * @return
     */
    @RequestMapping(value = "approveTaskList", method = RequestMethod.POST)
    @ResponseBody
    @SysLog("批量审批任务接口")
    @ApiOperation(httpMethod = "POST", value = "批量审批任务接口")
    public Object approveTaskList(@ApiParam(value = "任务id列表，用','隔开", name = "taskIds", required = true) @RequestParam("taskIds") String taskIds, @ApiParam(value = "1是通过，2是拒绝，3通过自定义参数流转", name = "type", required = true)  @RequestParam("type") Integer type,@ApiParam(value = "自定义参数流转", name = "jsonVariable", required = false,example = "{'a':'b'}") @RequestParam(value = "jsonVariable",required = false)  String jsonVariable,@ApiParam(value = "审批人信息", name = "approver", required = true)  @RequestParam("approver")String approver){
        Map map=JSONObject.parseObject(jsonVariable);
        Result result=new Result();
        result.setMsg("审批成功");
        if(StringUtils.isBlank(taskIds)||type==null){
            return renderError("请传正确的参数！",Constant.PARAM_ERROR);
        }
        String [] strs=taskIds.split(",");
        for(String taskId:strs){

            Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
            if(task==null){
                return renderError("请传正确的参数！【"+taskId+"】任务不存在！",Constant.PARAM_ERROR);
            }
            EntityWrapper wrapper=new EntityWrapper();
            wrapper.where("task_id={0}",task.getId());
            wrapper.like("approver_real","%"+approver+"%");
            TRuTask tRuTask=tRuTaskService.selectOne(wrapper);
            if(tRuTask==null){
                result.setMsg(result.getMsg()+","+task.getName()+"不属于"+"【"+approver+"】审批失败");
            }
            if(type.intValue()==1) {
                taskService.setAssignee(task.getId(),approver+"_Y");
                taskService.addComment(taskId, task.getProcessInstanceId(), "批量同意");
                taskService.complete(taskId,map);
            }else if(type.intValue()==2){
                taskService.setAssignee(task.getId(),approver+"_N");
                taskService.addComment(taskId, task.getProcessInstanceId(), "拒绝");
                taskService.deleteTask(taskId,"批量拒绝");
            }else if(type.intValue()==3){
                taskService.setAssignee(task.getId(),approver+"_F");
                taskService.addComment(taskId, task.getProcessInstanceId(), "流程流转");
                taskService.complete(taskId,map);

            }
        }
        return  result;
    }

    /**
     * 获取任务节点信息
     * @param taskId
     * @return
     */
    @SysLog(value = "获取任务节点信息")
    @RequestMapping(value = "taskNodeDetail", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "获取任务节点信息")
    public Object taskNodeDetail(@ApiParam(value = "任务id", name = "taskId", required = true) @RequestParam("taskId")String taskId){
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task==null){
            return renderError("任务不存在！",Constant.TASK_NOT_EXIT);
        }
        return renderSuccess(TaskNodeResult.toTaskNodeResult(task));
    }

    /**
     * 获取任务自定义信息或流程实例自定义信息
     * @param taskId
     * @param type
     * @return
     */
    @SysLog(value = "获取任务自定义信息或流程实例自定义信息")
    @RequestMapping(value = "getVariables", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "获取任务自定义信息或流程实例自定义信息")
    public Object getVariables(@ApiParam(value = "任务id", name = "taskId", required = true) @RequestParam("taskId")String taskId,@ApiParam(value = "类型是流程实例的还是任务的", name = "taskId", required = true,example = "1是流程实例，2是任务的") @RequestParam("type")Integer type){

        if(type==null||(type!=1&&type!=2)){
            return  renderError("参数不正确，类型不存在！",Constant.PARAM_ERROR);
        }
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task==null){
            return renderError("参数不正确，任务不存在！",Constant.TASK_NOT_EXIT);
        }
        Map map=new HashMap();
        if(type==1){
            map =taskService.getVariables(taskId);
        }else{
            map=taskService.getVariablesLocal(taskId);
        }

        return resultSuccess("操作成功",map);
    }

    /**
     * 设置自定义参数
     * @param taskId  任务id

     * @return
     */
    @SysLog(value = "设置自定义参数")
    @RequestMapping(value = "setVariables", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "设置自定义参数")
    public Object setVariables(@ApiParam(value = "任务id", name = "taskId", required = true) @RequestParam("taskId") String taskId,@ApiParam(value = "自定义参数的json串", name = "jsonMap", required = true) @RequestParam("jsonMap")String jsonMap,@ApiParam(value = "类型", name = "type", required = true) @RequestParam("type")Integer type){
        if(StringUtils.isBlank(jsonMap)||type==null||(type!=1&&type!=2)){
            return renderError("参数错误",Constant.PARAM_ERROR);
        }
        try {
            Map map=JSONObject.parseObject(jsonMap);
            if(type==1) {
                taskService.setVariables(taskId, map);
            }else{
                taskService.setVariablesLocal(taskId,map);
            }
        }catch (Exception e){
            return renderError("jsonMap参数错误！",Constant.PARAM_ERROR);
        }
       return renderSuccess();
    }
    @SysLog(value = "获取下一节点信息")
    @RequestMapping(value = "getNextTaskNode", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "获取所有节点")
    public Object getNextTaskNode(@ApiParam(value = "任务id", name = "taskId", required = true) @RequestParam("taskId")String taskId){
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();

        if(task==null){
            return renderError("查询失败，任务不存在",Constant.PARAM_ERROR);
        }
        List<TaskNodeResult> taskNodeResults=new ArrayList<>();
       List<TaskDefinition> list=getTaskDefinitionList(task.getProcessInstanceId());
        for(TaskDefinition taskDefinition:list){
            TaskNodeResult taskNodeResult=new TaskNodeResult();
            if(taskDefinition.getFormKeyExpression()!=null){

                taskNodeResult.setFormKey(taskDefinition.getFormKeyExpression().getExpressionText());

            }
            taskNodeResult.setName(taskDefinition.getNameExpression().getExpressionText());
            taskNodeResult.setTaskDefinedKey(taskDefinition.getKey());
            EntityWrapper entityWrapper =new EntityWrapper();
            entityWrapper.where("task_def_key={0}",taskDefinition.getKey());
            TUserTask tUserTask=tUserTaskService.selectOne(entityWrapper);
            if(tUserTask!=null&&StringUtils.isNotBlank(tUserTask.getCandidateIds())){

                taskNodeResult.setApprover(tUserTask.getCandidateIds());
                taskNodeResult.setAssignType(tUserTask.getAssignType());
            }
            taskNodeResults.add(taskNodeResult);

        }
        return resultSuccess("成功",taskNodeResults);
    }
    /**
     * 获取代办任务总数
     * @return
     */
    public Object taskCount(){

        return null;
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
    public Object jumpTask(@ApiParam(name = "taskId", required = true, value = "任务ID") String taskId,
                           @ApiParam(name = "userId", required = true, value = "任务原所属用户ID") String userId,
                           @ApiParam(name = "taskDefinitionKey", required = true, value = "任务节点KEY") String taskDefinitionKey) {
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.JUMP.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setTaskId(taskId);
        taskActionParam.setTargetTaskDefKey(taskDefinitionKey);
        return taskAction(taskActionParam);
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
    public Object transferTask(@ApiParam(name = "taskId", required = true, value = "任务ID") String taskId,
                               @ApiParam(name = "userId", required = true, value = "任务原所属用户ID") String userId,
                               @ApiParam(name = "transferUserId", required = true, value = "任务要转办用户ID") String transferUserId) {
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.TRANSFER.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setTargetUserId(transferUserId);
        taskActionParam.setTaskId(taskId);
        return taskAction(taskActionParam);
    }

    /**
     * 问询
     *
     * @param processInstanceId 流程实例ID
     * @param userId            用户ID
     * @param taskDefinitionKey 任务节点KEY
     * @return
     */
    @SysLog(value = "问询")
    @RequestMapping(value = "enquire", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "问询接口")
    public Object enquire(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") String processInstanceId,
                          @ApiParam(name = "userId", required = true, value = "用户ID") String userId,
                          @ApiParam(name = "taskDefinitionKey", required = true, value = "任务节点KEY") String taskDefinitionKey) {
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.ENQUIRE.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setProcessInstanceId(processInstanceId);
        taskActionParam.setTargetTaskDefKey(taskDefinitionKey);
        return taskAction(taskActionParam);
    }

    /**
     * 确认问询
     *
     * @param processInstanceId 任务ID
     * @param userId            用户ID
     * @return
     */
    @SysLog(value = "确认问询")
    @RequestMapping(value = "confirmEnquiries", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "确认问询接口")
    public Object confirmEnquiries(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") String processInstanceId,
                                   @ApiParam(name = "userId", required = true, value = "用户ID") String userId) {
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.CONFIRMENQUIRE.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setProcessInstanceId(processInstanceId);
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
    public Object revokeProcessInstance(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") String processInstanceId,
                                        @ApiParam(name = "userId", required = true, value = "用户ID") String userId) {
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
    public Object suspendProcess(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") String processInstanceId,
                                 @ApiParam(name = "userId", required = true, value = "用户ID") String userId) {
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
    public Object activateProcess(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") String processInstanceId,
                                  @ApiParam(name = "userId", required = true, value = "用户ID") String userId) {
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
    public Object taskAction(TaskActionParam taskActionParam) {
        String actionType = taskActionParam.getActionType();
        if (StringUtils.isBlank(actionType)) {
            return renderError("操作类型不能为空");
        } else if (StringUtils.isBlank(taskActionParam.getUserId())) {
            return renderError("操作人工号不能为空");
        }

        //校验操作人权限


        //参数校验
        Result validate = taskActionParam.validate();
        if (validate.isSuccess()) {
            TaskAdapter taskAdapter = new TaskAdapter();
            return taskAdapter.taskAction(taskActionParam);
        } else {
            return validate;
        }
    }
}
