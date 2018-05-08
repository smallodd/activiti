package com.hengtian.flow.controller.manage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workflow/page")
public class WorkflowPageController {

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
}
