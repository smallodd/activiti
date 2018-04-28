package com.hengtian.flow.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.enums.TaskActionEnum;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.AskTaskParam;
import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.TUserTaskService;
import com.hengtian.flow.service.WorkflowService;
import io.swagger.annotations.ApiOperation;
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
    public String comment(HttpServletRequest request, @RequestParam String taskId) {
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
        request.setAttribute("currentTaskDefKey", task.getTaskDefinitionKey());
        request.setAttribute("processInstanceId", task.getProcessInstanceId());
        return "ask/comment";
    }

    /**
     * 问询意见查询接口
     *
     * @param taskId 任务id
     * @return
     */
    @GetMapping("detail")
    public String detail(@RequestParam String taskId, HttpServletRequest request) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            Result askComment = workflowService.askComment(getUserId(), task.getProcessInstanceId(), task.getTaskDefinitionKey());
            request.setAttribute("askComment", askComment);
        }
        return "ask/detail";
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
     * 问询
     *
     * @param processInstanceId 流程实例ID
     * @param commentResult     问询详情
     * @param currentTaskDefKey 当前任务节点KEY
     * @param targetTaskDefKey  目标任务节点KEY
     * @return
     */
    @RequestMapping(value = "askTask")
    @ResponseBody
    public Result askTask(String processInstanceId, String currentTaskDefKey, String commentResult, String targetTaskDefKey) {
        try {
            TaskActionParam taskActionParam = new TaskActionParam();
            taskActionParam.setProcessInstanceId(processInstanceId);
            taskActionParam.setCurrentTaskDefKey(currentTaskDefKey);
            taskActionParam.setCommentResult(commentResult);
            taskActionParam.setTargetTaskDefKey(targetTaskDefKey);
            //参数校验
            Result validate = taskActionParam.validate();
            if (validate.isSuccess()) {
                return workflowService.taskEnquire(getUserId(), processInstanceId, currentTaskDefKey, targetTaskDefKey, commentResult);
            }
            return validate;
        } catch (Exception e) {
            log.error("", e);
            return new Result(false, "操作失败");
        }
    }


    /**
     * 确认问询
     *
     * @param processInstanceId 任务流程ID
     * @return
     */
    @RequestMapping(value = "askComment", method = RequestMethod.POST)
    @ResponseBody
    public Result askComment(String processInstanceId, String taskDefKey) {
        try {
            TaskActionParam taskActionParam = new TaskActionParam();
            taskActionParam.setProcessInstanceId(processInstanceId);
            taskActionParam.setTargetTaskDefKey(taskDefKey);
            //参数校验
            Result validate = taskActionParam.validate();
            if (validate.isSuccess()) {
                return workflowService.taskConfirmEnquire(getUserId(), processInstanceId, taskDefKey);
            }
            return validate;
        } catch (Exception e) {
            log.error("", e);
            return new Result(false, "操作失败");
        }
    }
}
