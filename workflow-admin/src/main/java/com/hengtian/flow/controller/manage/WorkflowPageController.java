package com.hengtian.flow.controller.manage;

import com.hengtian.flow.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 工作流程相关-页面
 * @author houjinrong@chtwm.com
 * date 2018/5/9 17:42
 */
@Controller
@RequestMapping("/workflow/page")
public class WorkflowPageController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 流程定义管理
     * @return 页面
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:25
     */
    @GetMapping("/processDef")
    public String processDef(){
        return "workflow/process/process_def";
    }

    /**
     * 流程实例部署
     * @return 页面
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:54
     */
    @GetMapping("/processDef/deploy")
    public String processDefDeploy(){
        return "workflow/process/process_inst";
    }

    /**
     * 代办任务列表-页面
     * @author houjinrong@chtwm.com
     * date 2018/5/10 13:29
     */
    @GetMapping("/task")
    public String task(){
        return  "/workflow/task/task_list";
    }
}
