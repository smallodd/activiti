package com.hengtian.flow.controller;

import com.hengtian.common.base.BaseController;
import com.hengtian.common.param.AskTaskParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.WorkflowService;
import com.hengtian.flow.vo.TaskVo;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 问询
 *
 * @author chenzhangyan  on 2018/4/18.
 */
@RequestMapping("ask")
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
        List<TaskVo> tasks = workflowService.getParentTasks(taskId, true);
        request.setAttribute("tasks", tasks);
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
    @RequestMapping(value = "askTask")
    @ResponseBody
    public Result askTask(@RequestParam String processInstanceId, @RequestParam String currentTaskDefKey, @RequestParam String commentResult, @RequestParam String targetTaskDefKey) {
        try {
            return workflowService.taskEnquire(getUserId(), processInstanceId, currentTaskDefKey, targetTaskDefKey, commentResult);
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
    public Result askConfirm(@RequestParam String askId, @RequestParam String commentResult) {
        try {
            return workflowService.taskConfirmEnquire(getUserId(), askId, commentResult);
        } catch (Exception e) {
            log.error("", e);
            return new Result(false, "操作失败");
        }
    }
}
