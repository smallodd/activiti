package com.hengtian.flow.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.application.model.AppModel;
import com.hengtian.application.service.AppModelService;
import com.hengtian.common.enums.AssignType;
import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.enums.TaskType;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.ProcessParam;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.param.TaskParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import com.hengtian.flow.extend.TaskAdapter;
import com.hengtian.flow.model.AppProcinst;
import com.hengtian.flow.model.TAskTask;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.AppProcinstService;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.TRuTaskService;
import com.hengtian.flow.service.TUserTaskService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2018/4/12.
 * 所有涉及到操作类的功能都放到这里
 */
@Controller
@RequestMapping("/rest/flow/operate")
public class WorkflowOperateController extends WorkflowBaseController {
    private Logger logger = LoggerFactory.getLogger(WorkflowOperateController.class);
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;
    @Autowired
    TUserTaskService tUserTaskService;
    @Autowired
    AppModelService appModelService;
    @Autowired
    TRuTaskService tRuTaskService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    HistoryService historyService;

    @Autowired
    TAskTaskService tAskTaskService;

    @Autowired
    AppProcinstService appProcinstService;

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
        String jsonVariables = processParam.getJsonVariables();
        Map<String, Object> variables = new HashMap<>();
        if (StringUtils.isNotBlank(jsonVariables)) {
            variables = JSON.parseObject(jsonVariables);
        }

        //校验参数是否合法
        Result result = processParam.validate();
        if (!result.isSuccess()) {

            return result;
        } else {
            EntityWrapper<AppModel> wrapperApp = new EntityWrapper();

            wrapperApp.where("app_key={0}", processParam.getAppKey()).andNew("model_key={0}", processParam.getProcessDefinitionKey());
            AppModel appModelResult = appModelService.selectOne(wrapperApp);
            //系统与流程定义之间没有配置关系
            if (appModelResult == null) {
                logger.info("系统键值：【{}】对应的modelKey:【{}】关系不存在!", processParam.getAppKey(), processParam.getProcessDefinitionKey());
                result.setCode(Constant.RELATION_NOT_EXIT);
                result.setMsg("系统键值：【" + processParam.getAppKey() + "】对应的modelKey:【" + processParam.getProcessDefinitionKey() + "】关系不存在!");
                result.setSuccess(false);
                return result;

            }
            //校验当前业务主键是否已经在系统中存在
            boolean isInFlow = checkBusinessKeyIsInFlow(processParam.getProcessDefinitionKey(), processParam.getBussinessKey(), processParam.getAppKey());

            if (isInFlow) {
                logger.info("业务主键【{}】已经提交过任务", processParam.getBussinessKey());
                //已经创建过则返回错误信息
                result.setSuccess(false);
                result.setMsg("此条信息已经提交过任务");
                result.setCode(Constant.BUSSINESSKEY_EXIST);
                return result;
            } else {
                variables.put("customApprover", processParam.isCustomApprover());
                variables.put("appKey", processParam.getAppKey());
                //生成任务
                ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processParam.getProcessDefinitionKey(), processParam.getBussinessKey(), variables);

                //添加应用-流程实例对应关系
                AppProcinst appProcinst = new AppProcinst(processParam.getAppKey(), processInstance.getProcessInstanceId());
                appProcinstService.insert(appProcinst);

                //给对应实例生成标题
                runtimeService.setProcessInstanceName(processInstance.getId(), processParam.getTitle());
                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().latestVersion().singleResult();
                //查询创建完任务之后生成的任务信息
                List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
                //String aa=net.sf.json.JSONObject.fromObject(taskList);
                if (!processParam.isCustomApprover()) {
                    logger.info("工作流平台设置审批人");
                    for (int i = 0; i < taskList.size(); i++) {
                        Task task = taskList.get(0);
                        EntityWrapper entityWrapper = new EntityWrapper();
                        entityWrapper.where("proc_def_key={0}", processParam.getProcessDefinitionKey()).andNew("task_def_key={0}", task.getTaskDefinitionKey()).andNew("version_={0}", processDefinition.getVersion());
                        //查询当前任务任务节点信息
                        TUserTask tUserTask = tUserTaskService.selectOne(entityWrapper);
                        boolean flag = setApprover(task, tUserTask);
                        if (!flag) {
                            taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), "生成扩展任务时失败，删除任务！");//备注
                            runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), "");
                            historyService.deleteHistoricProcessInstance(processInstance.getProcessInstanceId());//(顺序不能换)

                            result.setSuccess(false);
                            result.setCode(Constant.FAIL);
                            result.setMsg("生成扩展任务失败，删除其他信息");
                            return result;
                        }
                    }
                    result.setSuccess(true);
                    result.setCode(Constant.SUCCESS);
                    result.setMsg("申请成功");
                    result.setObj(toTaskNodeResultList(taskList));

                } else {
                    logger.info("业务平台设置审批人");
//
                    result.setSuccess(true);
                    result.setCode(Constant.SUCCESS);
                    result.setMsg("申请成功");
                    result.setObj(toTaskNodeResultList(taskList));

                }
            }

        }
        return result;
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
        Boolean flag = setApprover(task, tUserTask);
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


    @RequestMapping(value = "approveTask", method = RequestMethod.POST)
    @ResponseBody
    @SysLog("审批任务接口")
    @ApiOperation(httpMethod = "POST", value = "审批任务接口")
    public Object approveTask(@RequestBody TaskParam taskParam) {
        Result result = new Result();
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

            return renderError("您的问询信息还未得到相应，不能审批通过", Constant.ASK_TASK_EXIT);
        }
        //查询当前任务节点审批人是不是当前人

        return approveTask(task, taskParam);


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
     * 挂起任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return
     */
    @SysLog(value = "挂起任务")
    @RequestMapping(value = "suspendTask", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "挂起任务接口")
    public Object suspendTask(@ApiParam(name = "taskId", required = true, value = "任务ID") String taskId,
                              @ApiParam(name = "userId", required = true, value = "用户ID") String userId) {
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.SUSPEND.value);
        taskActionParam.setUserId(userId);
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
    public Object revokeProcessInstance(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") String processInstanceId,
                                        @ApiParam(name = "userId", required = true, value = "用户ID") String userId) {
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.REVOKE.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setProcessInstanceId(processInstanceId);
        return taskAction(taskActionParam);
    }

    /**
     * 激活任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return
     */
    @SysLog(value = "激活任务")
    @RequestMapping(value = "activateTask", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "激活任务接口")
    public Object activateTask(@ApiParam(name = "taskId", required = true, value = "任务ID") String taskId,
                               @ApiParam(name = "userId", required = true, value = "用户ID") String userId) {
        TaskActionParam taskActionParam = new TaskActionParam();
        taskActionParam.setActionType(TaskActionEnum.ACTIVATE.value);
        taskActionParam.setUserId(userId);
        taskActionParam.setTaskId(taskId);
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
    public Result suspendProcess(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") String processInstanceId,
                                 @ApiParam(name = "userId", required = true, value = "用户ID") String userId) {
        return null;
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
    public Result activateProcess(@ApiParam(name = "processInstanceId", required = true, value = "流程实例ID") String processInstanceId,
                                  @ApiParam(name = "userId", required = true, value = "用户ID") String userId) {
        return null;
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
     *                        8挂起任务 suspend
     *                        9激活任务 activate
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
