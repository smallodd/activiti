package com.hengtian.flow.controller.manage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.DateUtils;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.flow.controller.WorkflowBaseController;
import com.hengtian.flow.model.RuProcinst;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.*;
import com.hengtian.flow.vo.CommentVo;

import com.user.entity.emp.Emp;
import com.user.service.emp.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * 工作流程相关-页面
 * @author houjinrong@chtwm.com
 * date 2018/5/9 17:42
 */
@Slf4j
@Controller
@RequestMapping("/workflow/page")
public class WorkflowPageController extends WorkflowBaseController{

    @Autowired
    private TRuTaskService tRuTaskService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private RuProcinstService ruProcinstService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private EmpService empService;
    @Autowired
    private TUserTaskService tUserTaskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ActivitiService activitiService;

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

    /**
     * 签收/退签 选择人员
     * @param taskId 任务ID
     * @param claimType 1-签收；2-退签
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/14 13:53
     */
    @GetMapping("/user/claim")
    public String selectUserClaim(Model model, String taskId, int claimType){
        if(StringUtils.isNotBlank(taskId)){
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if(task != null){
                EntityWrapper<RuProcinst> wrapper = new EntityWrapper<>();
                wrapper.where("proc_inst_id={0}", task.getProcessInstanceId());
                RuProcinst ruProcinst = ruProcinstService.selectOne(wrapper);

                EntityWrapper<TRuTask> wrapper1 = new EntityWrapper<>();
                wrapper1.where("task_id={0}", taskId);
                List<TRuTask> ruTasks = tRuTaskService.selectList(wrapper1);

                model.addAttribute("taskId", taskId);
                model.addAttribute("claimType", claimType);
                model.addAttribute("system", ruProcinst.getAppKey());
                model.addAttribute("ruTasks", ruTasks);
            }
        }

        return "workflow/task/select_user_claim";
    }

    /**
     * 任务办理页面
     * @param model
     * @param taskId 任务ID
     * @return
     */
    @GetMapping("/task/complete/{taskId}")
    public String completeTaskPage(Model model,@PathVariable("taskId") String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();

        List<CommentVo> comments = new ArrayList<CommentVo>();
        List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);

        //审批意见
        for(Comment comment : commentList){
            CommentEntity c = (CommentEntity)comment;
            CommentVo vo = new CommentVo();
            Emp user = empService.selectByCode(comment.getUserId());
            vo.setCommentUser(user==null?c.getUserId():user.getName());
            vo.setCommentTime(DateUtils.formatDateToString(comment.getTime()));
            vo.setCommentContent(c.getMessage());

            comments.add(vo);
        }


        //查询流程定义信息
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(task.getProcessDefinitionId());

        //判断是否需要设置下一个节点审批人
        EntityWrapper<TUserTask> wrapper = new EntityWrapper<>();
        wrapper.eq("task_def_key", task.getTaskDefinitionKey());
        wrapper.eq("version_", processDefinition.getVersion());
        TUserTask tUserTask = tUserTaskService.selectOne(wrapper);

        model.addAttribute("needSetNext", tUserTask.getNeedSetNext());
        model.addAttribute("task", task);
        model.addAttribute("comments", comments);
        return "workflow/task/task_complete";
    }

    /**
     * 任务转办
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/18 16:01
     */
    @GetMapping("/task/transfer/{taskId}")
    public String transferTaskPage(Model model,@PathVariable("taskId") String taskId){
        model.addAttribute("taskId", taskId);
        return "workflow/task/task_delegate";
    }

    /**
     * 任务跳转页面
     * @param taskId 任务ID
     */
    @GetMapping("/task/jump")
    public String taskJump(Model model,@RequestParam("taskId") String taskId) {
        Result result = workflowService.getBeforeNodes(taskId, getUserId(),true,false);
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task != null){
            model.addAttribute("processInstanceId", task.getProcessInstanceId());
        }
        Set<String> assignee = getAssigneeUserByTaskId(task);
        model.addAttribute("tasks", result.getObj());
        model.addAttribute("assignee", assignee);
        model.addAttribute("taskId",taskId);
        return "workflow/task/task_jump";
    }

    /**
     * 任务审批人
     * @author houjinrong@chtwm.com
     * date 2018/6/28 11:39
     */
    @GetMapping("/task/assignee/{taskId}")
    public String taskAssignee(Model model,@PathVariable("taskId") String taskId){
        model.addAttribute("taskId",taskId);
        return  "/workflow/task/task_assignee";
    }

    /**
     * 开启流程任务
     * @author houjinrong@chtwm.com
     * date 2018/9/6 9:48
     */
    @GetMapping("/process/start/{processDefinitionId}")
    public String processStart(Model model,@PathVariable("processDefinitionId") String processDefinitionId){
        try {
            InputStream processResource = activitiService.getProcessResource("xml", processDefinitionId);
            String resource = new Scanner(processResource).useDelimiter("\\Z").next();
            Document parse = Jsoup.parse(resource);
            String varName = parse.text();
            if(StringUtils.isNotBlank(varName)){
                Set<String> expressionNameSet = workflowService.getExpressionName(varName);
                model.addAttribute("expressionNameSet",expressionNameSet);
            }
        } catch (Exception e) {
            log.error("开启流程时获取属性异常", e);
        }
        model.addAttribute("processDefinitionId", processDefinitionId);
        return  "/workflow/process/process_start";
    }
}
