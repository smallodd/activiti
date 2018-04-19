package com.hengtian.flow.controller;

import com.hengtian.common.param.ProcessParam;
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


    @RequestMapping("/rest/task/page")
    public Object taskList(String userId,
           @ApiParam(value = "任务查询条件", name = "processParam", required = true) @RequestBody ProcessParam processParam){

        return null;
    }
}
