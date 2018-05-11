package com.hengtian.flow.controller.manage;

import com.hengtian.common.utils.StringUtils;
import com.hengtian.flow.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
     * 代办任务列表
     * @author houjinrong@chtwm.com
     * date 2018/5/10 13:29
     */
    @GetMapping("/task")
    public String task(){
        return  "/workflow/task/task_list";
    }

    /**
     * 历史任务列表
     * @author houjinrong@chtwm.com
     * date 2018/5/11 15:29
     */
    @GetMapping("/task/his")
    public String hisTask(){
        return  "/workflow/task/task_his_list";
    }

    /**
     * 流程图展示
     * @param request
     * @param response
     * @param processDefinitionId
     * @param processInstanceId
     */
    @GetMapping("/diagram")
    public void diagramViewer(HttpServletRequest request, HttpServletResponse response,
            String processDefinitionId, String processInstanceId){
        try {
            if(StringUtils.isBlank(processInstanceId)){
                response.sendRedirect(request.getContextPath() + "/diagram-viewer/index.html?processDefinitionId="+processDefinitionId);
            }else{
                response.sendRedirect(request.getContextPath() + "/diagram-viewer/index.html?processDefinitionId="+processDefinitionId+"&processInstanceId="+processInstanceId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
