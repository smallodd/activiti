package com.hengtian.flow.controller;

import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.param.TaskRemindQueryParam;
import com.hengtian.enquire.service.EnquireService;
import com.hengtian.flow.service.RemindTaskService;
import com.hengtian.flow.service.WorkflowService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ma on 2018/4/17.
 * 所有列表查询都放这里
 */
@Controller
public class WorkflowQueryController {

    @Autowired
    private RemindTaskService remindTaskService;
    @Autowired
    private EnquireService enquireService;

    @Autowired
    private WorkflowService workflowService;

    /**
     * 催办任务列表
     * @param taskRemindQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @ResponseBody
    @SysLog("催办任务列表")
    @ApiOperation(httpMethod = "POST", value = "催办任务列表")
    @RequestMapping(value = "/rest/task/remind/page", method = RequestMethod.POST)
    public Object taskRemindList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskRemindQueryParam taskRemindQueryParam){
        return remindTaskService.taskRemindList(taskRemindQueryParam);
    }

    /**
     * 被催办任务列表
     * @param taskRemindQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @ResponseBody
    @SysLog("被催办任务列表")
    @ApiOperation(httpMethod = "POST", value = "被催办任务列表")
    @RequestMapping(value = "/rest/task/reminded/page", method = RequestMethod.POST)
    public Object taskRemindedList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskRemindQueryParam taskRemindQueryParam){
        return remindTaskService.taskRemindedList(taskRemindQueryParam);
    }

    /**
     * 未办任务列表
     * @param taskQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @ResponseBody
    @SysLog("未办任务列表")
    @ApiOperation(httpMethod = "POST", value = "未办任务列表")
    @RequestMapping(value = "/rest/task/open/page", method = RequestMethod.POST)
    public Object taskOpenList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam){
        return workflowService.taskOpenList(taskQueryParam);
    }

    /**
     * 已办任务列表
     * @param taskQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @ResponseBody
    @SysLog("已办任务列表")
    @ApiOperation(httpMethod = "POST", value = "已办任务列表")
    @RequestMapping(value = "/rest/task/close/page", method = RequestMethod.POST)
    public Object taskCloseList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam){
        return workflowService.taskOpenList(taskQueryParam);
    }


    /**
     * 问询任务列表
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @SysLog("问询任务列表")
    @ApiOperation(httpMethod = "POST", value = "问询任务列表")
    @RequestMapping(value = "/rest/task/enquire/page",method = RequestMethod.POST)
    public Object enquireTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        return enquireService.enquireTaskList(taskQueryParam);
    }


    /**
     * 被问询任务列表
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @SysLog("被问询任务列表")
    @ApiOperation(httpMethod = "POST", value = "被问询任务列表")
    @RequestMapping(value = "/rest/task/enquired/page",method = RequestMethod.POST)
    public Object enquiredTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        return enquireService.enquiredTaskList(taskQueryParam);
    }

    /**
     * 问询意见查询接口
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @SysLog("问询意见查询接口")
    @ApiOperation(httpMethod = "POST", value = "问询意见查询接口")
    @RequestMapping(value = "/rest/task/enquired/comment",method = RequestMethod.POST)
    public Object enquireComment(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        return null;
    }
}
