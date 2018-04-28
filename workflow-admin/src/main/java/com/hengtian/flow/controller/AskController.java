package com.hengtian.flow.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.param.AskTaskParam;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.TUserTaskService;
import com.hengtian.flow.service.WorkflowService;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 问询
 *
 * @author chenzhangyan  on 2018/4/18.
 */
@RequestMapping("ask")
@Controller
public class AskController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(AskController.class);
    @Autowired
    private TAskTaskService tAskTaskService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TUserTaskService tUserTaskService;

    /**
     * 问询列表
     *
     * @return
     */
    @GetMapping("askTaskList")
    public String askTaskList() {
        return "ask/askTask";
    }

    /**
     * 被问询列表
     *
     * @return
     */
    @GetMapping("askedTaskList")
    public String askedTaskList() {
        return "ask/askedTask";
    }

    /**
     * 问询意见查询接口
     *
     * @param taskId 任务id
     * @return
     */
    @GetMapping("comment")
    public String comment(HttpServletRequest request, String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        //查询流程定义
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId()).singleResult();
//        EntityWrapper<TUserTask> entityWrapper = new EntityWrapper<>();
//        entityWrapper.where("proc_def_key = {0}", pd.getKey())
//                .where("task_def_key={0}", task.getTaskDefinitionKey())
//                .andNew("version_={0}", pd.getVersion());
//        TUserTask userTask = tUserTaskService.selectOne(entityWrapper);
        //todo 可询问节点 应限制只能为上级节点
//        if (userTask != null) {
        //根据流程定义KEY查询用户任务
        EntityWrapper<TUserTask> wrapper = new EntityWrapper<>();
        wrapper.where("proc_def_key = {0}", pd.getKey()).and("version_={0}", pd.getVersion());
//            wrapper.lt("order_num", userTask.getOrderNum());
        wrapper.orderBy("order_num", true);
        List<TUserTask> tasks = tUserTaskService.selectList(wrapper);
        request.setAttribute("tasks", tasks);
//        }
        request.setAttribute("taskId", taskId);
        return "ask/comment";
    }


    /**
     * 问询任务列表
     *
     * @return
     */
    @ResponseBody
    @PostMapping(value = "askTaskDataGrid")
    public PageInfo askTaskDataGrid(AskTaskParam askTaskParam, Integer page, Integer rows) {
        askTaskParam.setPageNum(page);
        askTaskParam.setPageSize(rows);
        askTaskParam.setCreateId(getUserId());
        return tAskTaskService.enquireTaskList(askTaskParam);
    }


    /**
     * 被问询任务列表
     *
     * @param askTaskParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @PostMapping(value = "askedTaskDataGrid")
    public PageInfo askedTaskDataGrid(AskTaskParam askTaskParam, Integer page, Integer rows) {
        askTaskParam.setPageNum(page);
        askTaskParam.setPageSize(rows);
        askTaskParam.setAskUserId(getUserId());
        return tAskTaskService.enquiredTaskList(askTaskParam);
    }

    /**
     * 问询意见查询接口
     *
     * @param taskId 任务ID
     * @return
     */
    @ResponseBody
    @PostMapping(value = "askCommentData")
    public Result askCommentData(@ApiParam(value = "任务ID", name = "taskId", required = true) String taskId) {
        try {
            return workflowService.askComment(getUserId(), taskId);
        } catch (Exception e) {
            log.error("", e);
            return new Result(false, "查询失败");
        }
    }

    /**
     * 问询
     *
     * @param taskId            任务ID
     * @param commentResult     问询详情
     * @param taskDefinitionKey 任务节点KEY
     * @return
     */
    @RequestMapping(value = "askTask")
    @ResponseBody
    public Result askTask(String taskId, String commentResult, String taskDefinitionKey) {
        try {
            return workflowService.taskEnquire(getUserId(), taskId, taskDefinitionKey, commentResult);
        } catch (Exception e) {
            log.error("", e);
            return new Result(false, "操作失败");
        }
    }
}
