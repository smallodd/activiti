package com.hengtian.flow.controller.manage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.utils.DateUtils;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.flow.controller.WorkflowBaseController;
import com.hengtian.flow.model.RuProcinst;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.service.RuProcinstService;
import com.hengtian.flow.service.TRuTaskService;
import com.hengtian.flow.vo.CommentVo;
import com.rbac.entity.RbacUser;
import com.rbac.service.UserService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作流程相关-页面
 * @author houjinrong@chtwm.com
 * date 2018/5/9 17:42
 */
@Controller
@RequestMapping("/workflow/page")
public class WorkflowPageController extends WorkflowBaseController{

    @Autowired
    private TRuTaskService tRuTaskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuProcinstService ruProcinstService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;

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
    public String selectUserClaim(Model model, String taskId, int claimType, String procDefId){
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
        List<Comment> commentList= taskService.getProcessInstanceComments(processInstanceId);
        for(Comment comment : commentList){
            CommentEntity c = (CommentEntity)comment;
            CommentVo vo = new CommentVo();
            RbacUser user = userService.getUserById(comment.getUserId());
            vo.setCommentUser(user==null?c.getUserId():user.getName());
            vo.setCommentTime(DateUtils.formatDateToString(comment.getTime()));
            vo.setCommentContent(c.getMessage());

            comments.add(vo);
        }

        model.addAttribute("task", task);
        model.addAttribute("comments", comments);
        model.addAttribute("assignees", getAssigneeUserByTaskId(taskId));
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
}
