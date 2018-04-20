package com.hengtian.flow.controller;

import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.flow.service.WorkflowService;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by ma on 2018/4/17.
 * 所有列表查询都放这里
 */
@Controller
public class WorkflowQueryController {

    private WorkflowService workflowService;

    /**
     * 催办任务列表
     * @param taskQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @RequestMapping("/rest/task/remind/page")
    public Object taskRemindList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam){
        //workflowService.taskRemindList(taskQueryParam);
        return null;
    }

    /**
     * 被催办任务列表
     * @param taskQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @RequestMapping("/rest/task/reminded/page")
    public Object taskRemindedList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam){
        //workflowService.taskRemindedList(taskQueryParam);
        return null;
    }


    /**
     * 问询任务列表
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return
     */
    @RequestMapping("/rest/task/enquire/page")
    public Object enquireTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        return null;
    }


    /**
     * 被问询任务列表
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return
     */
    @RequestMapping("/rest/task/enquired/page")
    public Object enquiredTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        return null;
    }
}
