package com.hengtian.flow.controller;

import com.hengtian.common.operlog.SysLog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by ma on 2018/4/17.
 * 所有列表查询都放这里
 */
@Controller
@RequestMapping("/rest/flow/page")
public class WorkflowPageController {
    /**
     * 问询列表
     *
     * @return
     */
    @GetMapping("/enquireTaskList")
    public String enquireTaskList() {
        return "activiti/enquireTask";
    }

    /**
     * 被问询列表
     *
     * @return
     */
    @GetMapping("/enquiredTaskList")
    public String enquiredTaskList() {
        return "activiti/enquiredTask";
    }


    /**
     * 问询
     *
     * @return
     */
    @GetMapping("/enquire")
    public String enquire() {
        return "activiti/enquire";
    }

    /**
     * 问询意见查询接口
     *
     * @return
     */
    @ResponseBody
    @SysLog(value = "问询意见查询")
    @PostMapping("/enquireComment/{taskId}")
    public String enquireComment(@PathVariable String taskId) {
        return "activiti/enquireComment";
    }
}
