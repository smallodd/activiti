package com.hengtian.flow.controller;

import com.hengtian.common.base.BaseController;
import com.hengtian.common.param.AskTaskParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.WorkflowService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 问询
 *
 * @author chenzhangyan  on 2018/4/18.
 */
@RequestMapping("/rest/ask")
@Controller
public class AskController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(AskController.class);
    @Autowired
    private TAskTaskService tAskTaskService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private TaskService taskService;

    /**
     * 问询列表
     *
     * @return
     */
    @GetMapping("askTaskList")
    public String askTaskList() {
        return "ask/askTask";
    }

    /**
     * 被问询列表
     *
     * @return
     */
    @GetMapping("askedTaskList")
    public String askedTaskList() {
        return "ask/askedTask";
    }

    /**
     * 问询意见查询接口
     *
     * @param taskId 任务id
     * @return
     */
    @GetMapping("comment")
    public String comment(HttpServletRequest request, @RequestParam String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            request.setAttribute("currentTaskDefKey", task.getTaskDefinitionKey());
            request.setAttribute("processInstanceId", task.getProcessInstanceId());
        }
        Result result = workflowService.getBeforeNodes(taskId, getUserId(), true,false);
        request.setAttribute("tasks", result.getObj());
        return "ask/comment";
    }

    /**
     * 问询意见查询接口
     *
     * @param askId 问询id
     * @return
     */
    @GetMapping("detail")
    public String detail(@RequestParam String askId, HttpServletRequest request) {
        Result askComment = workflowService.askComment(getUserId(), askId);
        request.setAttribute("askComment", askComment.getObj());
        request.setAttribute("askId", askId);
        return "ask/detail";
    }

    /**
     * 回复意见查询接口
     *
     * @param askId 问询id
     * @return
     */
    @GetMapping("answer")
    public String answer(@RequestParam String askId, HttpServletRequest request) {
        Result askComment = workflowService.askComment(getUserId(), askId);
        request.setAttribute("askComment", askComment.getObj());
        return "ask/answer";
    }

    /**
     * 问询任务列表
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "askTaskDataGrid")
    public PageInfo askTaskDataGrid(AskTaskParam askTaskParam, Integer page, Integer rows) {
        askTaskParam.setPageNum(page);
        askTaskParam.setPageSize(rows);
        askTaskParam.setCreateId(getUserId());
        return tAskTaskService.enquireTaskList(askTaskParam);
    }


    /**
     * 被问询任务列表
     *
     * @param askTaskParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @PostMapping(value = "askedTaskDataGrid")
    public PageInfo askedTaskDataGrid(AskTaskParam askTaskParam, Integer page, Integer rows) {
        askTaskParam.setPageNum(page);
        askTaskParam.setPageSize(rows);
        askTaskParam.setAskUserId(getUserId());
        return tAskTaskService.enquiredTaskList(askTaskParam);
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
    public Object askTask(@RequestParam String processInstanceId, @RequestParam String currentTaskDefKey, @RequestParam String commentResult, @RequestParam String targetTaskDefKey,@RequestParam String askedUserId,@RequestParam String userId) {
        try {
            if(StringUtils.isBlank(userId)&&getShiroUser()==null){
                return renderError("请传问询人员工号");
            }
            if(StringUtils.isBlank(processInstanceId)){
                return renderError("流程实例id不能为空");
            }
            if(StringUtils.isBlank(currentTaskDefKey)){
                return renderError("当前节点信息不能为空");
            }
            if(StringUtils.isBlank(commentResult)){
                return renderError("问询信息不能为空");
            }
            if(StringUtils.isBlank(targetTaskDefKey)){
                return renderError("被问询节点key不能为空");
            }
            if(StringUtils.isBlank(askedUserId)){
                return renderError("被问询人员");
            }
            if(StringUtils.isBlank(userId)){
                userId=getUserId();
            }
            return workflowService.taskEnquire(userId, processInstanceId, currentTaskDefKey, targetTaskDefKey, commentResult,askedUserId);
        } catch (Exception e) {
            log.error("", e);
            return new Result(false, "操作失败");
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
        try {
            return workflowService.taskConfirmEnquire(userId, askId,commentResult);
        } catch (Exception e) {
            log.error("", e);
            return new Result(false, "操作失败");
        }
    }
}
