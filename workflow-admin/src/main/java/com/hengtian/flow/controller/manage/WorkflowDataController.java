package com.hengtian.flow.controller.manage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.hengtian.common.enums.AssignTypeEnum;
import com.hengtian.common.enums.TaskListEnum;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.controller.WorkflowBaseController;
import com.hengtian.flow.model.TRuTask;
import com.hengtian.flow.service.ActivitiService;
import com.hengtian.flow.service.TRuTaskService;
import com.hengtian.flow.service.WorkflowService;
import com.rbac.entity.RbacUser;
import com.rbac.service.PrivilegeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流程相关-数据查询
 *
 * @author houjinrong@chtwm.com
 * date 2018/5/9 17:41
 */
@RestController
@RequestMapping("/workflow/data")
public class WorkflowDataController extends WorkflowBaseController {

    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private TRuTaskService tRuTaskService;
    @Autowired
    private TaskService taskService;

    /**
     * 流程定义列表-分页
     *
     * @param page
     * @param rows
     * @param key
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:53
     */
    @PostMapping("/processDef")
    @ResponseBody
    public PageInfo processDef(Integer page, Integer rows, String key) {
        PageInfo pageInfo = new PageInfo(page, rows);
        Map<String, Object> params = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(key)) {
            params.put("key", key.trim());
        }
        pageInfo.setCondition(params);
        activitiService.selectProcessDefinitionDataGrid(pageInfo);
        return pageInfo;
    }

    /**
     * 代办任务列表-页面
     *
     * @param taskQueryParam
     * @author houjinrong@chtwm.com
     * date 2018/5/10 13:29
     */
    @PostMapping("/task")
    @ResponseBody
    public Object task(TaskQueryParam taskQueryParam) {
        if (StringUtils.isBlank(taskQueryParam.getAssignee())) {
            PageInfo pageInfo = workflowService.allTaskPage(taskQueryParam, TaskListEnum.ACTIVE.type);
            return pageInfo;
        } else {
            PageInfo pageInfo = workflowService.myTaskPage(taskQueryParam, TaskListEnum.ACTIVE.type);
            return pageInfo;
        }
    }

    /**
     * 代办任务列表-页面
     *
     * @param taskQueryParam
     * @author houjinrong@chtwm.com
     * date 2018/5/10 13:29
     */
    @PostMapping("/task/his")
    @ResponseBody
    public Object hisTask(TaskQueryParam taskQueryParam) {
        if (StringUtils.isBlank(taskQueryParam.getAssignee())) {
            PageInfo pageInfo = workflowService.allTaskPage(taskQueryParam, TaskListEnum.CLOSE.type);
            return pageInfo;
        } else {
            PageInfo pageInfo = workflowService.myTaskPage(taskQueryParam, TaskListEnum.CLOSE.type);
            return pageInfo;
        }
    }

    /**
     * 签收/退签 选择人员
     *
     * @param taskId    任务ID
     * @param claimType 1-签收；2-退签
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/14 13:53
     */
    @PostMapping("/user/claim")
    @ResponseBody
    public Object selectUserClaim(String taskId, int claimType, int system) {
        EntityWrapper<TRuTask> wrapper = new EntityWrapper();
        wrapper.where("task_id={0}", taskId);
        List<TRuTask> tRuTasks = tRuTaskService.selectList(wrapper);
        if (CollectionUtils.isEmpty(tRuTasks)) {
            return null;
        }

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return null;
        }
        List<RbacUser> userList = Lists.newArrayList();
        List<RbacUser> tempUserList;
        String assignee = StringUtils.isBlank(task.getAssignee()) ? null : task.getAssignee().replaceAll("_Y", "").replaceAll("_N", "");
        List<String> assigneeList = StringUtils.isBlank(assignee) ? Lists.newArrayList() : Arrays.asList(assignee.split(","));
        for (TRuTask tRuTask : tRuTasks) {
            if (AssignTypeEnum.ROLE.code.equals(tRuTask.getAssigneeType())) {
                tempUserList = privilegeService.getUsersByRoleId(system, null, Long.parseLong(tRuTask.getAssignee()));
                List<String> assigneeReal = StringUtils.isBlank(tRuTask.getAssigneeReal()) ? null : Arrays.asList(tRuTask.getAssigneeReal().split(","));
                for (RbacUser user : tempUserList) {
                    if (claimType == 1) {
                        if (assigneeReal == null || !assigneeReal.contains(user.getCode())) {
                            user.setJobCode(tRuTask.getId() + ":" + user.getCode());
                            userList.add(user);
                        }
                    } else if (claimType == 2) {
                        if (assigneeReal != null && assigneeReal.contains(user.getCode()) && !assigneeList.contains(user.getCode())) {
                            user.setJobCode(tRuTask.getId() + ":" + user.getCode());
                            userList.add(user);
                        }
                    }
                }
            }
        }

        return userList;
    }

    /**
     * 任务转办-办理人树形结构
     *
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/18 16:01
     */
    @GetMapping("/task/transfer/tree/{taskId}")
    @ResponseBody
    public Object transferAssigneeTree(@PathVariable("taskId") String taskId) {
        return getAssigneeUserTreeByTaskId(taskId);
    }
}
