package com.hengtian.flow.controller;

import com.hengtian.common.base.BaseController;
import com.hengtian.common.param.AskTaskParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 意见征询
 *
 * @author chenzhangyan  on 2018/4/18.
 */
@Slf4j
@RequestMapping("/ask")
@Controller
public class AskController extends BaseController {
    @Autowired
    private TAskTaskService tAskTaskService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private TaskService taskService;

    /**
     * 意见征询列表
     *
     * @return
     */
    @GetMapping("askTaskList")
    public String askTaskList() {
        return "ask/askTask";
    }

    /**
     * 被意见征询列表
     *
     * @return
     */
    @GetMapping("askedTaskList")
    public String askedTaskList() {
        return "ask/askedTask";
    }

    /**
     * 意见征询意见查询接口
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
     * 意见征询意见查询接口
     *
     * @param askId 意见征询id
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
     * @param askId 意见征询id
     * @return
     */
    @GetMapping("answer")
    public String answer(@RequestParam String askId, HttpServletRequest request) {
        Result askComment = workflowService.askComment(getUserId(), askId);
        request.setAttribute("askComment", askComment.getObj());
        return "ask/answer";
    }

    /**
     * 意见征询任务列表
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "askTaskDataGrid")
    public PageInfo askTaskDataGrid(AskTaskParam askTaskParam, Integer page, Integer rows) {
        askTaskParam.setPageNum(page);
        askTaskParam.setPageSize(rows);
        String currentUserId= getShiroUser().getId();
        if(StringUtils.isNotBlank(currentUserId)&&!currentUserId.contains("admin")) {
            askTaskParam.setCreateId(getUserId());
        }
        return tAskTaskService.enquireTaskList(askTaskParam);
    }


    /**
     * 被意见征询任务列表
     *
     * @param askTaskParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @PostMapping(value = "askedTaskDataGrid")
    public PageInfo askedTaskDataGrid(AskTaskParam askTaskParam, Integer page, Integer rows) {
        askTaskParam.setPageNum(page);
        askTaskParam.setPageSize(rows);
        String currentUserId= getShiroUser().getId();
        if(StringUtils.isNotBlank(currentUserId)&&!currentUserId.contains("admin")) {
            askTaskParam.setCreateId(getUserId());
        }
        return tAskTaskService.enquiredTaskList(askTaskParam);
    }


}
