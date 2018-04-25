package com.hengtian.enquire.controller;

import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.shiro.ShiroUser;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 问询
 *
 * @author chenzhangyan  on 2018/4/18.
 */
@RequestMapping("enquire")
public class EnquireController extends BaseController {
    /**
     * 问询列表
     *
     * @return
     */
    @GetMapping("enquireTaskList")
    public String enquireTaskList(HttpServletRequest request) {
        String userId = getUserId();
        request.setAttribute("userId", userId);
        return "enquire/enquireTask";
    }

    /**
     * 被问询列表
     *
     * @return
     */
    @GetMapping("enquiredTaskList")
    public String enquiredTaskList(HttpServletRequest request) {
        String userId = getUserId();
        request.setAttribute("userId", userId);
        return "enquire/enquiredTask";
    }

    /**
     * 问询意见查询接口
     *
     * @param taskId 任务id
     * @return
     */
    @ResponseBody
    @SysLog(value = "问询意见查询")
    @PostMapping("enquireComment")
    public String enquireComment(HttpServletRequest request, String taskId) {
        String userId = getUserId();
        request.setAttribute("userId", userId);
        request.setAttribute("taskId", taskId);
        return "enquire/enquireComment";
    }
}
