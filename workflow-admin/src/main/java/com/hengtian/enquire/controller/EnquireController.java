package com.hengtian.enquire.controller;

import com.hengtian.common.operlog.SysLog;
import org.springframework.web.bind.annotation.*;

/**
 * 问询
 *
 * @author chenzhangyan  on 2018/4/18.
 */
@RequestMapping("enquire")
public class EnquireController {
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
     * 问询
     *
     * @return
     */
    @GetMapping("enquire")
    public String enquire() {
        return "enquire/enquire";
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
        return "enquire/enquireComment";
    }
}
