package com.hengtian.flow.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;

import com.hengtian.application.model.AppModel;
import com.hengtian.application.service.AppModelService;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.ProcessParam;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import com.hengtian.flow.action.TaskAdapter;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.TRuTaskService;
import com.hengtian.flow.service.TUserTaskService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public Result startProcessInstance(@RequestBody @ApiParam(value = "processParam", name = "创建任务必传信息", required = true) ProcessParam processParam) {
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
                logger.info("系统键值：【{}】对应的modelkey:【{}】关系不存在!", processParam.getAppKey(), processParam.getProcessDefinitionKey());
                result.setCode(Constant.RELATION_NOT_EXIT);
                result.setMsg("系统键值：【" + processParam.getAppKey() + "】对应的modelkey:【" + processParam.getProcessDefinitionKey() + "】关系不存在!");
                result.setSuccess(false);
                return result;

            }
            //校验当前业务主键是否已经在系统中存在
            boolean isInFlow = checkBusinessKeyIsInFlow(processParam.getProcessDefinitionKey(), processParam.getAppKey(), processParam.getBussinessKey());

            if (isInFlow) {
                logger.info("业务主键【{}】已经提交过任务", processParam.getBussinessKey());
                //已经创建过则返回错误信息
                result.setSuccess(false);
                result.setMsg("此条信息已经提交过任务");
                result.setCode(Constant.BUSSINESSKEY_EXIST);
                return result;
            } else {
                variables.put("customApprover", true);
                //生成任务
                ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processParam.getProcessDefinitionKey(), processParam.getBussinessKey(), variables, processParam.getAppKey());
                //给对应实例生成标题
                runtimeService.setProcessInstanceName(processInstance.getId(), processParam.getTitle());

                //查询创建完任务之后生成的任务信息
                List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();

                if (!processParam.isCustomApprover()) {
                    logger.info("工作流平台设置审批人");
                    for (int i = 0; i < taskList.size(); i++) {
                        Task task = taskList.get(0);
                        EntityWrapper entityWrapper = new EntityWrapper();
                        entityWrapper.where("proc_def_key={0}", processParam.getProcessDefinitionKey()).andNew("task_def_key={0}", task.getTaskDefinitionKey()).andNew("version_={0}", processInstance.getProcessDefinitionVersion());
                        //查询当前任务任务节点信息
                        TUserTask tUserTask = tUserTaskService.selectOne(entityWrapper);
                        setApprover(task, tUserTask);
                    }
                    result.setSuccess(true);
                    result.setCode(Constant.SUCCESS);
                    result.setMsg("申请成功");
                    result.setObj(taskList);

                } else {
                    logger.info("业务平台设置审批人");
                    for (Task task : taskList) {
                        TRuTask tRuTask = new TRuTask();
                        tRuTask.setExpireTime(task.getDueDate());
                        tRuTask.setIsFinished(0);
                        tRuTask.setTaskId(task.getId());
                        tRuTask.setOwer(task.getOwner());
                        tRuTaskService.insert(tRuTask);
                    }
                    result.setSuccess(true);
                    result.setCode(Constant.SUCCESS);
                    result.setMsg("申请成功");
                    result.setObj(taskList);
                }
            }

        }
        return result;
    }

    /**
     * 设置审批人接口
     *
     * @param task
     * @param tUserTask
     */
    private void setApprover(Task task, TUserTask tUserTask) {
        String approvers = tUserTask.getCandidateIds();
        taskService.setAssignee(task.getId(), approvers);
        TRuTask tRuTask = new TRuTask();
        String[] approverList = approvers.split(",");
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
            tRuTaskService.insert(tRuTask);
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
    private Boolean checkBusinessKeyIsInFlow(String processDefiniKey, String bussinessKey, String appKey) {
        TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey(processDefiniKey).processInstanceBusinessKey(bussinessKey).taskTenantId(appKey);

        Task task = taskQuery.singleResult();

        if (task != null) {
            return true;
        }
        return false;
    }


    /**
     * 任务操作接口
     *
     * @param taskActionParam
     *  请求类型
     *   1跳转 jump
     *   2转办 transfer
     *   3催办 remind
     *   4问询 enquire
     *   5确认问询 confirmEnquire
     *   6撤回 revoke
     *   7取消 cancel
     *   8挂起任务 suspendTask
     *   9激活任务 activateTask
     *
     * @return result
     * @author houjinrong@chtwm.com
     * date 2018/4/18 9:38
     */
    public Object taskAction(TaskActionParam taskActionParam) {
        String actionType = taskActionParam.getActionType();
        if (StringUtils.isBlank(actionType)) {
            return renderError("操作类型不能为空");
        }
        Result validate = taskActionParam.validate();
        if (validate.isSuccess()) {
            TaskAdapter taskAdapter = new TaskAdapter();
            return taskAdapter.action(actionType);
        }else{
            return validate;
        }
    }
}
