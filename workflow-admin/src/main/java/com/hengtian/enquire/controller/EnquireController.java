package com.hengtian.enquire.controller;

import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.TaskEnquireParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.enquire.service.EnquireService;
import com.hengtian.flow.service.WorkflowService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 问询
 *
 * @author chenzhangyan  on 2018/4/18.
 */
@RequestMapping("enquire")
@Controller
public class EnquireController extends BaseController {
    @Autowired
    private EnquireService enquireService;

    @Autowired
    private WorkflowService workflowService;

    /**
     * 问询列表
     *
     * @return
     */
    @GetMapping("enquireTaskList")
    public String enquireTaskList() {
        return "enquire/enquireTask";
    }

    /**
     * 被问询列表
     *
     * @return
     */
    @GetMapping("enquiredTaskList")
    public String enquiredTaskList() {
        return "enquire/enquiredTask";
    }

    /**
     * 问询意见查询接口
     *
     * @param taskId 任务id
     * @return
     */
    @ResponseBody
    @PostMapping("enquireComment")
    public String enquireComment(HttpServletRequest request, String taskId) {
        request.setAttribute("taskId", taskId);
        return "enquire/enquireComment";
    }


    /**
     * 问询任务列表
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "enquireTaskDataGrid")
    public PageInfo enquireTaskDataGrid(TaskEnquireParam taskEnquireParam, Integer page, Integer rows) {
        taskEnquireParam.setPageNum(page);
        taskEnquireParam.setPageSize(rows);
        taskEnquireParam.setCreateId(getUserId());
        return enquireService.enquireTaskList(taskEnquireParam);
    }


    /**
     * 被问询任务列表
     *
     * @param taskEnquireParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @PostMapping(value = "enquiredTaskDataGrid")
    public PageInfo enquiredTaskDataGrid(TaskEnquireParam taskEnquireParam, Integer page, Integer rows) {
        taskEnquireParam.setPageNum(page);
        taskEnquireParam.setPageSize(rows);
        taskEnquireParam.setAskUserId(getUserId());
        return enquireService.enquiredTaskList(taskEnquireParam);
    }

    /**
     * 问询意见查询接口
     *
     * @param taskId 任务ID
     * @return
     */
    @ResponseBody
    @PostMapping(value = "enquireCommentData")
    public Result enquireCommentData(@ApiParam(value = "任务ID", name = "taskId", required = true) String taskId) {
        return workflowService.enquireComment(getUserId(), taskId);
    }

}
