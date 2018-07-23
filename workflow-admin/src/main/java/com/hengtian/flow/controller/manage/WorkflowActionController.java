package com.hengtian.flow.controller.manage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Maps;
import com.hengtian.application.model.AppModel;
import com.hengtian.application.service.AppModelService;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.enums.ResultEnum;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.ProcessParam;
import com.hengtian.common.param.TaskParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.service.TRuTaskService;
import com.hengtian.flow.service.WorkflowService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工作流程相关-操作
 * @author houjinrong@chtwm.com
 * date 2018/5/9 17:42
 */
@Controller
@RequestMapping("/workflow/action")
public class WorkflowActionController extends BaseController {

    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TRuTaskService tRuTaskService;
    @Autowired
    private AppModelService appModelService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 启动流程热任务
     * @param processKey 流程定义KEY
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/7/23 13:23
     */
    @SysLog(value="任务开启模拟")
    @PostMapping("/process/start")
    @ResponseBody
    public Object startProcessInstance(String processKey){
        ProcessParam processParam = new ProcessParam();
        processParam.setBusinessKey(UUID.randomUUID().toString());
        processParam.setCustomApprover(false);
        processParam.setCreatorId("admin");
        processParam.setProcessDefinitionKey(processKey);
        EntityWrapper entityWrapper=new EntityWrapper();
        entityWrapper.where("model_key={0}",processKey);
        List<AppModel> list = appModelService.selectList(entityWrapper);
        if(list == null || list.size() == 0){
            return renderError("启动流程失败：模型未关联到应用系统中");
        }
        processParam.setAppKey(Integer.valueOf(list.get(0).getAppKey()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateNowStr = sdf.format(new Date());
        processParam.setTitle("模拟测试任务title"+dateNowStr);

        try {
            return workflowService.startProcessInstance(processParam);
        } catch (Exception e) {
            Result result = new Result();

            logger.error("启动流程失败", e);
            result.setMsg(e.getMessage());
            result.setCode(Constant.FAIL);
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * 签收任务
     * @param claimUser 签收人
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/14 15:17
     */
    @SysLog(value="任务签收")
    @PostMapping(value="/task/claim")
    @ResponseBody
    public Object taskClaim(String claimUser, String taskId){
        if(StringUtils.isBlank(claimUser) || StringUtils.isBlank(taskId)){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        Map<String,String> userMap = Maps.newHashMap();
        for(String userCode : claimUser.split(",")){
            String[] split = userCode.split(":");
            if(userMap.containsKey(split[0])){
                userMap.put(split[0],userMap.get(split[0])+","+split[1]);
            }else{
                userMap.put(split[0],split[1]);
            }
        }

        try {
            if(MapUtils.isNotEmpty(userMap)){
                Iterator<Map.Entry<String, String>> iterator = userMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, String> next = iterator.next();
                    Result result = workflowService.taskClaim(next.getValue(), taskId, next.getKey());
                    if(!result.isSuccess()){
                        return result;
                    }
                }

            }
        } catch (Exception e) {
            logger.info("", e);
            return renderError(ResultEnum.FAIL.msg);
        }
        return resultSuccess(ResultEnum.SUCCESS.msg);
    }

    /**
     * 签收任务
     * @param claimUser 签收人
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/14 15:17
     */
    @SysLog(value="任务退签")
    @PostMapping(value="/task/unclaim")
    @ResponseBody
    public Object taskUnclaim(String claimUser, String taskId){
        if(StringUtils.isBlank(claimUser) || StringUtils.isBlank(taskId)){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        Map<String,String> userMap = Maps.newHashMap();
        for(String userCode : claimUser.split(",")){
            String[] split = userCode.split(":");
            if(userMap.containsKey(split[0])){
                userMap.put(split[0],userMap.get(split[0])+","+split[1]);
            }else{
                userMap.put(split[0],split[1]);
            }
        }

        try {
            if(MapUtils.isNotEmpty(userMap)){
                Iterator<Map.Entry<String, String>> iterator = userMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, String> next = iterator.next();
                    for(String assignee : next.getValue().split(",")){
                        Result result = workflowService.taskUnclaim(assignee, taskId, next.getKey());
                        if(!result.isSuccess()){
                            return result;
                        }
                    }
                }

            }
        } catch (Exception e) {
            logger.info("", e);
            return renderError(ResultEnum.FAIL.msg);
        }
        return resultSuccess(ResultEnum.SUCCESS.msg);
    }


    /**
     * 办理任务(完成任务)
     * @param taskId 任务ID
     * @param commentContent 审批意见
     * @param commentResult 审批结果 2：同意；3：不同意
     * author houjinrong@chtwm.com
     * date 2018/5/18 17:57
     */
    @SysLog(value="办理任务")
    @PostMapping("/task/complete")
    @ResponseBody
    public Object completeTask(@RequestParam("taskId") String taskId,
                               @RequestParam("assignee") String assignee,
                               @RequestParam(value = "jsonVariable",required = false) String jsonVariable,
                               @RequestParam("commentContent") String commentContent,
                               @RequestParam("commentResult") Integer commentResult,
                               @RequestParam("assigneeNext") String assigneeNext){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        TaskParam taskParam = new TaskParam();
        taskParam.setAssigneeNext(assigneeNext);
        if(task==null){
            return renderError(ResultEnum.TASK_NOT_EXIST.msg, ResultEnum.TASK_NOT_EXIST.code) ;
        }

        EntityWrapper<TRuTask> wrapper = new EntityWrapper<>();
        wrapper.where("task_id={0}",taskId);
        List<TRuTask> tRuTasks = tRuTaskService.selectList(wrapper);

        //查看审批人是否有权限
        TRuTask ruTask = workflowService.validateTaskAssignee(task, assignee, tRuTasks);
        if(ruTask == null){
            return renderError("该用户没有操作此任务的权限");
        }

        if(CollectionUtils.isEmpty(tRuTasks)){
            return renderError(ResultEnum.TASK_ASSIGNEE_ILLEGAL.msg, ResultEnum.TASK_ASSIGNEE_ILLEGAL.code) ;
        }

        try {
            if(StringUtils.isNotBlank(jsonVariable)) {
                JSONObject.parseObject(jsonVariable);
            }
        }catch (Exception e){
            return renderError(ResultEnum.PARAM_ERROR.msg,ResultEnum.PARAM_ERROR.code);
        }


        taskParam.setAssignee(assignee);

        taskParam.setComment("【管理员代办】"+commentContent);
        taskParam.setPass(commentResult);
        taskParam.setTaskId(taskId);

        taskParam.setJsonVariables(jsonVariable);
        Object result = workflowService.approveTask(task,taskParam);
        return JSONObject.toJSONString(result);
    }

    /**
     * 转办任务
     * @param taskId 任务ID
     * @param userId 任务原所属用户ID
     * @param transferUserId 任务要转办用户ID
     * @return
     * author houjinrong@chtwm.com
     * date 2018/5/18 17:57
     */
    @SysLog(value="转办任务")
    @PostMapping("/task/transfer")
    @ResponseBody
    public Object transferTask(String taskId, String userId, String transferUserId){
        if(StringUtils.isBlank(taskId) || StringUtils.isBlank(userId) || StringUtils.isBlank(transferUserId)){
            return renderError(ResultEnum.PARAM_ERROR.msg);
        }
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if(task == null){
                return renderError(ResultEnum.TASK_NOT_EXIST.msg);
            }
            return workflowService.taskTransfer(userId, taskId, transferUserId);
        } catch (Exception e) {
            return renderError("委派任务失败，系统错误！");
        }
    }
}
