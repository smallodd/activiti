package com.hengtian.flow.controller.rest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hengtian.common.enums.ResultEnum;
import com.hengtian.common.enums.TaskStatusEnum;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.AskTaskParam;
import com.hengtian.common.param.ProcessInstanceQueryParam;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.param.TaskRemindQueryParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.BeanUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.workflow.activiti.CustomDefaultProcessDiagramGenerator;
import com.hengtian.flow.controller.WorkflowBaseController;
import com.hengtian.flow.model.ProcessInstanceResult;
import com.hengtian.flow.model.RuProcinst;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.model.TaskResultInfo;
import com.hengtian.flow.service.RemindTaskService;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.TUserTaskService;
import com.hengtian.flow.service.TWorkDetailService;
import com.hengtian.flow.service.WorkflowService;
import com.hengtian.flow.vo.AssigneeVo;
import com.hengtian.flow.vo.TaskNodeVo;
import com.rbac.dubbo.RbacDomainContext;
import com.rbac.entity.RbacRole;
import com.rbac.service.PrivilegeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 所有列表查询都放这里
 *
 * @author mayunliang@chtwm.com
 * date 2018/4/17 9:38
 */
@Slf4j
@Controller
public class WorkflowQueryController extends WorkflowBaseController {


    @Autowired
    private RemindTaskService remindTaskService;
    @Autowired
    private TAskTaskService tAskTaskService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;
    @Autowired
    private ProcessEngineFactoryBean processEngine;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TWorkDetailService tWorkDetailService;
    @Reference(loadbalance = "rbac")
    private PrivilegeService privilegeService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    TUserTaskService tUserTaskService;

    /**
     * 获取我发起的流程
     *
     * @param processInstanceQueryParam 流程查询条件
     * @return
     * @author houjinrong@chtwm.com
     */
    @ResponseBody
    @SysLog("获取我发起的流程")
    @ApiOperation(httpMethod = "POST", value = "获取我发起的流程")
    @RequestMapping(value = "/rest/process/instance", method = RequestMethod.POST)
    public Object processInstanceList(@ApiParam(value = "流程查询条件", name = "processInstanceQueryParam", required = true) @ModelAttribute @Valid ProcessInstanceQueryParam processInstanceQueryParam) {
        log.info("----------------查询获取父级任务节点开始,入参 taskId：{}----------------", processInstanceQueryParam.toString());
        PageInfo pageInfo = new PageInfo(processInstanceQueryParam.getPage(), processInstanceQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(processInstanceQueryParam));
        workflowService.processInstanceList(pageInfo);

        return renderSuccess(pageInfo);
    }

    /**
     * 通过业务主键查询流程实例
     *
     * @param appKey          系统应用KEY
     * @param businessKey     业务主键
     * @param suspensionState 流程状态：1-激活；2-挂起
     * @return
     * @author houjinrong@chtwm.com
     */
    @ResponseBody
    @SysLog("通过业务主键查询流程实例")
    @ApiOperation(httpMethod = "POST", value = "通过业务主键查询流程实例")
    @RequestMapping(value = "/rest/process/instance/{businessKey}", method = RequestMethod.POST)
    public Object queryProcessInstanceByBusinessKey(@ApiParam(value = "系统应用KEY", name = "appKey", required = true) @RequestParam Integer appKey,
                                                    @ApiParam(value = "业务主键", name = "businessKey", required = true) @RequestParam @PathVariable String businessKey,
                                                    @ApiParam(value = "流程状态", name = "suspensionState", required = true) @RequestParam Integer suspensionState) {
        log.info("----------------通过业务主键查询流程实例,入参 appKey：{}；businessKey：{}----------------", appKey, businessKey);

        RuProcinst ruProcinst = workflowService.queryProcessInstanceByBusinessKey(appKey, businessKey, suspensionState);
        log.info("----------------通过业务主键查询流程实例，出参：{}----------------", appKey, ruProcinst);
        return renderSuccess(ruProcinst);
    }

    /**
     * 催办任务列表
     *
     * @param taskRemindQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @ResponseBody
    @SysLog("催办任务列表")
    @ApiOperation(httpMethod = "POST", value = "催办任务列表")
    @RequestMapping(value = "/rest/task/remind", method = RequestMethod.POST)
    public Object remindTaskList(@ApiParam(value = "查询条件", name = "taskRemindQueryParam", required = true) @ModelAttribute TaskRemindQueryParam taskRemindQueryParam) {
        log.info("----------------催办任务列表,入参:----------------", JSONObject.toJSONString(taskRemindQueryParam));
        return renderSuccess(remindTaskService.remindTaskList(taskRemindQueryParam));
    }

    /**
     * 被催办任务列表
     *
     * @param taskRemindQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @ResponseBody
    @SysLog("被催办任务列表")
    @ApiOperation(httpMethod = "POST", value = "被催办任务列表")
    @RequestMapping(value = "/rest/task/reminded", method = RequestMethod.POST)
    public Object remindedTaskList(@ApiParam(value = "查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskRemindQueryParam taskRemindQueryParam) {
        log.info("----------------被催办任务列表,入参:----------------", JSONObject.toJSONString(taskRemindQueryParam));
        return renderSuccess(remindTaskService.remindedTaskList(taskRemindQueryParam));
    }

    /**
     * 未办任务列表
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @ResponseBody
    @SysLog("未办任务列表")
    @ApiOperation(httpMethod = "POST", value = "未办任务列表")
    @RequestMapping(value = "/rest/task/open", method = RequestMethod.POST)
    public Object openTaskList(@ApiParam(value = "查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskQueryParam taskQueryParam) {
        log.info("查询待办任务列表开始，入参{}", taskQueryParam);
        if (StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() == null) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }

        //检验代理人信息
        if (StringUtils.isNotBlank(taskQueryParam.getAssigneeAgent())) {
            if (StringUtils.isNotBlank(taskQueryParam.getAssigneeAgentSecret())) {
                if (!workflowService.getAssigneeSecret(taskQueryParam.getAssignee(), taskQueryParam.getAssigneeAgent()).equals(taskQueryParam.getAssigneeAgentSecret())) {
                    return renderError("代理人信息不合法，没有权限查询待办任务列表。");
                }
            } else {
                return renderError("代理人信息不合法，没有权限查询待办任务列表。");
            }
        }

        if (StringUtils.isNotBlank(taskQueryParam.getTaskState())) {
            if (!(TaskStatusEnum.UNFINISHED_AGREE.status + "").equals(taskQueryParam.getTaskState()) && !(TaskStatusEnum.UNFINISHED_REFUSE.status + "").equals(taskQueryParam.getTaskState())) {
                log.info("审批人状态不正确，重置为空");
                taskQueryParam.setTaskState("");
            }
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(taskQueryParam));

        setAssigneeAndRole(pageInfo, taskQueryParam);
        workflowService.openTaskList(pageInfo);

        return renderSuccess(pageInfo);
    }

    /**
     * 已办任务列表
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return json
     * @author houjinrong@chtwm.com
     * date 2018/4/19 15:17
     */
    @ResponseBody
    @SysLog("已办任务列表")
    @ApiOperation(httpMethod = "POST", value = "已办任务列表")
    @RequestMapping(value = "/rest/task/close", method = RequestMethod.POST)
    public Object closeTaskList(@ApiParam(value = "查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskQueryParam taskQueryParam) {
        log.info("已办任务列表，入参{}", taskQueryParam);
        if (StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() == null) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        if (StringUtils.isNotBlank(taskQueryParam.getTaskState())) {
            if ((TaskStatusEnum.FINISHED_AGREE.status + "").equals(taskQueryParam.getTaskState())) {
                taskQueryParam.setTaskState(TaskStatusEnum.FINISHED_AGREE.desc);
            } else if ((TaskStatusEnum.FINISHED_REFUSE.status + "").equals(taskQueryParam.getTaskState())) {
                taskQueryParam.setTaskState(TaskStatusEnum.FINISHED_REFUSE.desc);
            } else {
                log.info("审批人状态不正确，重置为空");
                taskQueryParam.setTaskState("");
            }
        } else {
            log.info("审批人状态不正确，重置为空");
            taskQueryParam.setTaskState("");
        }

        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(taskQueryParam));
        workflowService.closeTaskList(pageInfo);

        return renderSuccess(pageInfo);
    }

    /**
     * 待处理任务列表
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:35
     */
    @ResponseBody
    @SysLog("待处理任务列表")
    @ApiOperation(httpMethod = "POST", value = "待处理任务列表", hidden = true)
    @RequestMapping(value = "/rest/task/active", method = RequestMethod.POST)
    public Object activeTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskQueryParam taskQueryParam) {
        log.info("待处理任务列表，入参{}", taskQueryParam);
        if (StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() == null) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }

        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(taskQueryParam));

        setAssigneeAndRole(pageInfo, taskQueryParam.getAssignee(), taskQueryParam.getAppKey());

        workflowService.activeTaskList(pageInfo);

        return renderSuccess(pageInfo);
    }

    /**
     * 待签收任务列表
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:35
     */
    @ResponseBody
    @SysLog("待签收任务列表")
    @ApiOperation(httpMethod = "POST", value = "待签收任务列表")
    @RequestMapping(value = "/rest/task/claim", method = RequestMethod.POST)
    public Object claimTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskQueryParam taskQueryParam) {
        log.info("待签收任务列表，入参{}", taskQueryParam);
        if (StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() == null) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(taskQueryParam));

        setAssigneeAndRole(pageInfo, taskQueryParam.getAssignee(), taskQueryParam.getAppKey());

        workflowService.claimTaskList(pageInfo);

        return renderSuccess(pageInfo);
    }

    /**
     * 意见征询任务列表
     *
     * @param taskEnquireParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @SysLog("意见征询任务列表")
    @ApiOperation(httpMethod = "POST", value = "意见征询任务列表")
    @RequestMapping(value = "/rest/task/enquire", method = RequestMethod.POST)
    public Object enquireTaskList(@ApiParam(value = "查询条件", name = "taskEnquireParam", required = true) @ModelAttribute AskTaskParam taskEnquireParam) {
        log.info("意见征询任务列表，入参{}", JSONObject.toJSONString(taskEnquireParam));
        if (StringUtils.isBlank(taskEnquireParam.getCreateId())) {
            return new Result(false, Constant.PARAM_ERROR, "createId不能为空");
        }
        return renderSuccess(tAskTaskService.enquireTaskList(taskEnquireParam));
    }


    /**
     * 被意见征询任务列表
     *
     * @param taskEnquireParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @SysLog("被意见征询任务列表")
    @ApiOperation(httpMethod = "POST", value = "被意见征询任务列表")
    @RequestMapping(value = "/rest/task/enquired", method = RequestMethod.POST)
    public Object enquiredTaskList(@ApiParam(value = "查询条件", name = "taskEnquireParam", required = true) @ModelAttribute AskTaskParam taskEnquireParam) {
        log.info("被意见征询任务列表，入参{}", JSONObject.toJSONString(taskEnquireParam));
        if (StringUtils.isBlank(taskEnquireParam.getAskUserId())) {
            return new Result(false, Constant.PARAM_ERROR, "askUserId不能为空");
        }
        return renderSuccess(tAskTaskService.enquiredTaskList(taskEnquireParam));
    }

    /**
     * 意见征询意见查询接口
     *
     * @param userId 操作人ID
     * @param askId  意见征询id
     * @return
     */
    @ResponseBody
    @SysLog("意见征询意见查询接口")
    @ApiOperation(httpMethod = "POST", value = "意见征询意见查询接口")
    @RequestMapping(value = "/rest/task/enquire/comment", method = RequestMethod.POST)
    public Object enquireComment(@ApiParam(value = "操作人ID", name = "userId", required = true) @RequestParam String userId,
                                 @ApiParam(value = "意见征询id", name = "askId", required = true) @RequestParam String askId) {
        log.info("意见征询意见查询，入参: userId-{};askId-{}", userId, askId);
        return workflowService.askComment(userId, askId);
    }

    /**
     * 操作流程详细信息
     *
     * @param processInstanceId 流程实例ID
     * @param businessKey       业务主键
     * @return
     */
    @ResponseBody
    @SysLog("操作流程详细信息")
    @ApiOperation(httpMethod = "POST", value = "操作流程详细信息")
    @RequestMapping(value = "/rest/process/operate/detail", method = RequestMethod.POST)
    public Object operateDetailInfo(@ApiParam(value = "流程实例ID", name = "processInstanceId") @RequestParam(required = false) String processInstanceId,
                                    @ApiParam(value = "业务主键", name = "businessKey", required = true) @RequestParam String businessKey) {
        return renderSuccess(tWorkDetailService.operateDetailInfo(processInstanceId, null, businessKey));
    }

    /**
     * 流程实例详情
     *
     * @param processInstanceId 流程实例ID
     * @param businessKey       业务主键
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/25 17:48
     */
    @ResponseBody
    @SysLog("流程实例详情")
    @ApiOperation(httpMethod = "POST", value = "流程实例详情")
    @RequestMapping(value = "/rest/process/detail", method = RequestMethod.POST)
    public Object processDetail(@ApiParam(value = "应用系统KEY", name = "appKey") @RequestParam Integer appKey,
                                @ApiParam(value = "流程实例ID", name = "processInstanceId") @RequestParam(required = false) String processInstanceId,
                                @ApiParam(value = "业务主键", name = "businessKey", required = true) @RequestParam String businessKey) {
        log.info("入参appKey：{} processInstanceId：{} businessKey：{}", appKey, processInstanceId, businessKey);
        if (appKey == null) {
            return renderError("参数错误：appKey为空");
        }
        HistoricProcessInstance historicProcessInstance = null;
        if (StringUtils.isNotBlank(processInstanceId)) {
            historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (historicProcessInstance == null) {
                return renderError("流程实例ID【" + processInstanceId + "】无对应的流程实例");
            }
        } else if (StringUtils.isNotBlank(businessKey)) {

            List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).variableValueEquals("appKey", appKey).orderByProcessInstanceStartTime().desc().list();
            if (list == null||list.size()==0) {
                return renderError("业务主键【" + businessKey + "】无对应的流程实例");
            }
            historicProcessInstance= list.get(0);
        } else {
            return renderError("参数异常");
        }

        ProcessInstanceResult processInstanceResult = new ProcessInstanceResult();

        BeanUtils.copy(historicProcessInstance, processInstanceResult);
        processInstanceId = historicProcessInstance.getId();
        //获取当前节点
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (CollectionUtils.isNotEmpty(taskList)) {
            List<TaskNodeVo> taskNodeVoList = Lists.newArrayList();
            for (Task task : taskList) {
                TaskNodeVo taskNodeVo = new TaskNodeVo();
                taskNodeVo.setTaskId(task.getId());
                taskNodeVo.setTaskDefinitionKey(task.getTaskDefinitionKey());
                taskNodeVo.setTaskDefinitionName(task.getName());

                taskNodeVoList.add(taskNodeVo);
            }
            processInstanceResult.setCurrentTaskNode(taskNodeVoList);
        }
        return renderSuccess(processInstanceResult);
    }

    /**
     * 流程定义列表
     *
     * @param appKey    应用系统KEY
     * @param nameOrKey 流程定义KEY/流程定义名称
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/15 17:39
     */
    @ResponseBody
    @SysLog("任务详情")
    @ApiOperation(httpMethod = "POST", value = "流程定义列表")
    @RequestMapping(value = "/rest/process/def/list", method = RequestMethod.POST)
    public Object queryProcessDefinitionList(@ApiParam(value = "应用系统KEY", name = "appKey") @RequestParam Integer appKey,
                                             @ApiParam(value = "流程定义KEY", name = "nameOrKey") @RequestParam(required = false) String nameOrKey,
                                             @ApiParam(value = "页码", name = "page") @RequestParam Integer page,
                                             @ApiParam(value = "每页条数", name = "rows") @RequestParam Integer rows) {
        log.info("appKey{}；nameOrKey{}；page{}；rows{}", appKey, nameOrKey, page, rows);
        //参数统一处理
        page = page == null ? 1 : page;
        rows = rows == null ? 10 : rows;

        PageInfo pageInfo = workflowService.queryProcessDefinitionList(appKey, nameOrKey, page, rows);

        return renderSuccess(pageInfo);
    }

    /**
     * 流程定义详情
     *
     * @param appKey               应用系统key
     * @param processDefinitionKey 流程定义主键
     * @return
     */
    @ResponseBody
    @SysLog("流程实例详情")
    @ApiOperation(httpMethod = "POST", value = "流程实例详情")
    @RequestMapping(value = "/rest/process/def/detail", method = RequestMethod.POST)
    public Object processDefDetail(@ApiParam(value = "应用系统KEY", name = "appKey") @RequestParam Integer appKey,
                                   @ApiParam(value = "流程定义key", name = "processDefinitionKey") @RequestParam String processDefinitionKey) {
        log.info("appKey{} processDefinitionKey{}", appKey, processDefinitionKey);
        if (appKey == null) {
            return renderError("参数错误：appKey为空");
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
        if (processDefinition == null) {
            return renderError("没找到对应的流程定义");
        }

        JSONObject result = new JSONObject();
        result.put("processDefinitionId", processDefinition.getId());
        result.put("processDefinitionName", processDefinition.getName());
        return renderSuccess(result);
    }

    /**
     * 流程任务跟踪
     *
     * @param processInstanceId
     * @return
     */
    @SysLog("流程任务跟踪")
    @ApiOperation(httpMethod = "GET", value = "流程任务跟踪")
    @RequestMapping(value = "/rest/process/schedule", method = RequestMethod.GET)
    public void getProcessSchedule(HttpServletResponse response,
                                   @ApiParam(value = "流程实例ID", name = "processInstanceId", required = true) @RequestParam String processInstanceId) {
        log.info("----------------获取流程跟踪图开始,入参 processInstanceId：{}----------------", processInstanceId);
        if (StringUtils.isBlank(processInstanceId)) {
            try {
                response.getWriter().print("流程实例ID不能为空");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //获取历史流程实例
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (processInstance == null) {
                log.info("流程实例ID【" + processInstanceId + "】对应的流程实例不存在");
                return;
            }
            //获取流程图
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
            processEngineConfiguration = processEngine.getProcessEngineConfiguration();
            Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);

            CustomDefaultProcessDiagramGenerator diagramGenerator = (CustomDefaultProcessDiagramGenerator) processEngineConfiguration.getProcessDiagramGenerator();
            ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

            List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();

            //高亮环节id集合
            List<String> highLightedActivitis = new ArrayList<String>();

            //高亮线路id集合
            List<String> highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);

            for (HistoricActivityInstance tempActivity : highLightedActivitList) {
                String activityId = tempActivity.getActivityId();
                highLightedActivitis.add(activityId);
            }
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            List<String> taskDefinitionKeyList = Lists.newArrayList();
            for (Task task : taskList) {
                taskDefinitionKeyList.add(task.getTaskDefinitionKey());
            }
            //生成流图片  5.18.0
            /*InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "PNG", highLightedActivitis, highLightedFlows,
                    processEngineConfiguration.getLabelFontName(),
                    processEngineConfiguration.getActivityFontName(),
                    processEngineConfiguration.getProcessEngineConfiguration().getClassLoader(), 1.0);*/
            //中文显示的是口口口，设置字体就好了
            //5.22.0
            InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis, highLightedFlows,
                    processEngineConfiguration.getLabelFontName(),
                    processEngineConfiguration.getActivityFontName(),
                    processEngineConfiguration.getAnnotationFontName(),
                    processEngineConfiguration.getProcessEngineConfiguration().getClassLoader(), 1.0, taskDefinitionKeyList);
            //单独返回流程图，不高亮显示
            //InputStream imageStream = diagramGenerator.generatePngDiagram(bpmnModel);
            //输出资源内容到相应对象
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = imageStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, len);
            }
            imageStream.close();
        } catch (IOException e) {
            log.error("获取流程任务跟踪标识图失败", e);
        }
        log.info("----------------获取流程跟踪图开始结束----------------", processInstanceId);
    }

    /**
     * 评论列表-流程
     *
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 17:08
     */
    @ResponseBody
    @SysLog("流程论列表评")
    @ApiOperation(httpMethod = "POST", value = "评论列表表评")
    @RequestMapping(value = "/rest/process/comment", method = RequestMethod.POST)
    public Object processCommentList(@ApiParam(value = "流程实例ID", name = "processInstanceId", required = true) @RequestParam String processInstanceId) {
        log.info("----------------查询审批意见列表开始,入参 processInstanceId：{}----------------", processInstanceId);
        if (StringUtils.isBlank(processInstanceId)) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
        List<CommentEntity> list = new ArrayList<CommentEntity>();
        for (Comment comment : commentList) {
            list.add((CommentEntity) comment);
        }
        return renderSuccess(list);
    }

    /**
     * 评论列表-任务
     *
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 17:08
     */
    @ResponseBody
    @SysLog("任务评论列表")
    @ApiOperation(httpMethod = "POST", value = "任务评论列表")
    @RequestMapping(value = "/rest/task/comment", method = RequestMethod.POST)
    public Object taskCommentList(@ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId) {
        log.info("----------------查询审批意见列表开始,入参 taskId：{}----------------", taskId);
        if (StringUtils.isBlank(taskId)) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }

        List<Comment> commentList = taskService.getTaskComments(taskId, "1");
        commentList.addAll(taskService.getTaskComments(taskId, "2"));
        return renderSuccess(commentList);
    }

    /**
     * 获取父级任务节点
     *
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/29 14:38
     */
    @ResponseBody
    @SysLog("获取上一步任务节点")
    @ApiOperation(httpMethod = "POST", value = "获取上一步任务节点")
    @RequestMapping(value = "/rest/node/before", method = RequestMethod.POST)
    public Object getBeforeNodes(@ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId,
                                 @ApiParam(value = "操作人ID", name = "userId", required = true) @RequestParam String userId,
                                 @ApiParam(value = "是否递归获取父级节点", name = "isAll", required = true) @RequestParam(defaultValue = "1") Integer isAll) {
        log.info("----------------查询获取父级任务节点开始,入参 taskId：{}----------------", taskId);
        return workflowService.getBeforeNodes(taskId, userId, isAll != 0, false);
    }

    /**
     * 获取可意见征询任务节点
     *
     * @param taskId 任务ID
     * @return date 2018/5/29 14:38
     */
    @ResponseBody
    @SysLog("获取可意见征询任务节点")
    @ApiOperation(httpMethod = "POST", value = "获取可意见征询任务节点")
    @RequestMapping(value = "/rest/askNodes", method = RequestMethod.POST)
    public Object getAskNodes(@ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId,
                              @ApiParam(value = "操作人ID", name = "userId", required = true) @RequestParam String userId) {
        log.info("----------------获取可意见征询任务节点,入参 taskId：{}----------------", taskId);
        return workflowService.getBeforeNodes(taskId, userId, true, true);
    }

    /**
     * 待处理任务总数
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:35
     */
    @ResponseBody
    @SysLog("待处理任务总数")
    @ApiOperation(httpMethod = "POST", value = "待处理任务总数")
    @RequestMapping(value = "/rest/task/active/count", method = RequestMethod.POST)
    public Object activeTaskCount(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskQueryParam taskQueryParam) {
        log.info("待处理任务总数查询开始，方法【activeTaskCount】，入参：{}", JSONObject.toJSONString(taskQueryParam));
        if (StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() == null) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }

        Map<String, Object> paraMap = Maps.newHashMap();
        BeanMap beanMap = new BeanMap(taskQueryParam);
        paraMap.putAll(beanMap);
        RbacDomainContext.getContext().setDomain("chtwm");
        List<RbacRole> roles = privilegeService.getAllRoleByUserId(taskQueryParam.getAppKey(), taskQueryParam.getAssignee());
        String roleId = null;

        for (RbacRole role : roles) {
            roleId = roleId == null ? role.getId() + "" : roleId + "," + role.getId();
        }

        if (StringUtils.isNotBlank(roleId)) {
            paraMap.put("roleId", roleId);
        }
        Long count = workflowService.activeTaskCount(paraMap);
        log.info("查询待办任务总数结束，出参：{}", count);
        return renderSuccess(count);
    }

    /**
     * 查询用户某个任务是否审批过
     *
     * @param businessKey 业务主键
     * @param appKey      appKey
     * @return
     */
    @SysLog("查询用户是否已经审批过某个任务")
    @ApiOperation(httpMethod = "POST", value = "查询用户是否已经审批过某个任务")
    @RequestMapping(value = "/rest/task/checkUserApproved", method = RequestMethod.POST)
    @ResponseBody
    public Object checkUserApproved(
            @ApiParam(value = "业务主键", name = "businessKey", required = true) @RequestParam("businessKey") String businessKey,
            @ApiParam(value = "系统键值", name = "appKey", required = true) @RequestParam("appKey") Integer appKey) {
        log.info("查询用户是否已经审批过某个任务,入参：businessKey-{},appKey-{}", businessKey, appKey);
        Result result = new Result();
        if (StringUtils.isBlank(businessKey) || appKey == null) {

            result.setSuccess(false);
            result.setCode(Constant.PARAM_ERROR);
            result.setMsg("参数错误！");
            return result;
        }
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKey(businessKey).processVariableValueEquals("appKey", appKey).orderByHistoricTaskInstanceEndTime().asc().list();

        if (list != null && list.size() > 0) {
            HistoricTaskInstance historicTaskInstance = list.get(0);
            if (historicTaskInstance.getEndTime() != null) {
                result.setMsg("任务已经审批结束");
                result.setSuccess(true);
                result.setCode(Constant.SUCCESS);
                return result;
            } else {
                result.setMsg("任务未审批结束");
                result.setSuccess(false);
                result.setCode(Constant.SUCCESS);
                return result;
            }
        } else {
            result.setMsg("任务信息不存在");
            result.setSuccess(false);
            result.setCode(Constant.TASK_NOT_EXIT);
            return result;
        }
    }

    /**
     * 任务详情
     *
     * @param userId 用户ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/1 9:40
     */
    @ResponseBody
    @SysLog("任务详情")
    @ApiOperation(httpMethod = "POST", value = "任务详情")
    @RequestMapping(value = "/rest/task/detail", method = RequestMethod.POST)
    public Object taskDetail(@ApiParam(value = "用户ID", name = "userId", required = true) @RequestParam String userId,
                             @ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(taskId)) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        return workflowService.taskDetail(userId, taskId);
    }

    /**
     * 运行中的任务获取下步节点审批人
     *
     * @param userId 用户ID
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/1 9:40
     */
    @ResponseBody
    @SysLog("任务详情")
    @ApiOperation(httpMethod = "POST", value = "下步节点审批人")
    @RequestMapping(value = "/rest/task/assignee/next", method = RequestMethod.POST)
    public Object getNextAssignee(@ApiParam(value = "用户ID", name = "userId", required = true) @RequestParam String userId,
                                  @ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(taskId)) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return renderError("taskId无效或任务已完成");
        }

        /*if(!validateTaskAssignee(task, userId)){
            return renderError("【"+userId+"】没有权限");
        }*/

        return renderSuccess(workflowService.getNextAssigneeWhenRoleApprove(task));
    }

    /**
     * 运行中的任务获取下步节点审批人
     *
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/1 9:40
     */
    @ResponseBody
    @SysLog("下步节点审批人")
    @ApiOperation(httpMethod = "POST", value = "下步节点审批人")
    @RequestMapping(value = "/rest/task/assignee/next/{processInstanceId}", method = RequestMethod.POST)
    public Object getNextAssigneeBy(@ApiParam(value = "流程实例ID", name = "processInstanceId", required = true) @PathVariable("processInstanceId") String processInstanceId) {
        log.info("通过流程实例查询当前节点的下部节点审批人信息，入参{}", processInstanceId);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (null == processInstance) {
            log.info("未找到运行的流程实例{}", processInstanceId);
            return renderError("未找到运行的流程实例");
        }

        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (CollectionUtils.isEmpty(taskList)) {
            log.info("流程实例{}当前无未审批任务", processInstanceId);
            return renderError("流程实例当前无未审批任务");
        }

        List<AssigneeVo> nextAssignees = Lists.newArrayList();
        Map<String, Integer> map = new HashMap();
        for (Task t : taskList) {
            List<TaskNodeVo> nextNodes = workflowService.getNextNodeByTask(processInstance, t);
            if (CollectionUtils.isNotEmpty(nextNodes)) {
                for (TaskNodeVo tn : nextNodes) {
                    List<AssigneeVo> assignee = tn.getAssignee();
                    assignee.stream().filter(a -> !map.containsKey(a.getUserCode())).forEach(a -> {
                        nextAssignees.add(a);
                        map.put(a.getUserCode(), 1);
                    });
                }
            }
        }

        return nextAssignees;
    }

    /**
     * 通过任务ID获取任务节点审批人
     *
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/1 9:40
     */
    @ResponseBody
    @SysLog("任务详情")
    @ApiOperation(httpMethod = "POST", value = "节点审批人")
    @RequestMapping(value = "/rest/task/assignee", method = RequestMethod.POST)
    public Object getTaskAssignee(@ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return renderError("taskId无效或任务已完成");
        }

        return renderSuccess(workflowService.getTaskAssignee(task, null));
    }

    @ResponseBody
    @SysLog("获取流程实例中自定义参数")
    @ApiOperation(httpMethod = "POST", value = "获取流程实例中自定义参数")
    @RequestMapping(value = "/rest/getVariables", method = RequestMethod.POST)
    public Object getVariables(String processId) {
        Map map = workflowService.getVariables(processId);

        return renderSuccess(map);
    }

    @ResponseBody
    @SysLog("获取某个用户某个任务的审批意见")
    @ApiOperation(httpMethod = "POST", value = "获取某个用户某个任务的审批意见")
    @RequestMapping(value = "/rest/comment", method = RequestMethod.POST)
    public Object getComments(String taskId, String userId) {
        CommentEntity comment = (CommentEntity) workflowService.getComments(taskId, userId);

        return renderSuccess(comment);
    }

    /**
     * 仅仅营销活动调用此接口
     *
     * @param processInstanceId
     * @return
     */
    @ResponseBody
    @SysLog("获取最后审批人")
    @ApiOperation(httpMethod = "POST", value = "获取最后审批人")
    @RequestMapping(value = "/rest/getLastApprover", method = RequestMethod.POST)
    public Object getLastApprover(String processInstanceId) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();
        if (CollectionUtils.isEmpty(list)) {
            log.info("历史纪录为空");
            return renderError("历史纪录为空");
        }
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("createTime", list.get(list.size() - 1).getStartTime());
        if (taskList == null || taskList.size() == 0) {
            jsonObject.put("lastApprover", list.get(0).getAssignee().replace("_Y", ""));
            jsonObject.put("complete", 1);
            jsonObject.put("taskId", list.get(0).getId());
        } else {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String assignee = "";
            String taskId = "";
            for (Task task : taskList) {
                EntityWrapper entityWrapper = new EntityWrapper();
                entityWrapper.where("proc_def_key={0}", processInstance.getProcessDefinitionKey()).andNew("task_def_key={0}", task.getTaskDefinitionKey()).andNew("version_={0}", processInstance.getProcessDefinitionVersion());
                TUserTask tUserTask = tUserTaskService.selectOne(entityWrapper);
                assignee += tUserTask.getCandidateIds() + ",";
                taskId += task.getId() + ",";

            }
            jsonObject.put("lastApprover", assignee.replace("_Y", ""));
            jsonObject.put("complete", 0);
            jsonObject.put("taskId", taskId);

        }
        Map map = workflowService.getVariables(processInstanceId);
        map.putAll(jsonObject);
        return renderSuccess(map);
    }

    /**
     * 任务节点详情
     *
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/22 9:42
     */
    @ResponseBody
    @SysLog("任务节点详情")
    @ApiOperation(httpMethod = "POST", value = "任务节点详情")
    @RequestMapping(value = "/rest/task/node", method = RequestMethod.POST)
    public Object getTaskNodeInfo(@ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId,
                                  @ApiParam(value = "系统应用KEY", name = "appKey") @RequestParam(required = false) Integer appKey) {
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (hisTask == null) {
            return renderError("【" + taskId + "】任务不存在");
        }

        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(hisTask.getProcessInstanceId()).singleResult();
        TaskNodeVo taskNodeVo = new TaskNodeVo();
        taskNodeVo.setProcessCreator(historicProcessInstance.getStartUserId());
        taskNodeVo.setTaskId(taskId);
        taskNodeVo.setProcessInstanceId(historicProcessInstance.getId());
        taskNodeVo.setTaskDefinitionKey(hisTask.getTaskDefinitionKey());
        taskNodeVo.setTaskDefinitionName(hisTask.getName());
        taskNodeVo.setProcessDefinitionKey(historicProcessInstance.getProcessDefinitionKey());
        taskNodeVo.setProcessDefinitionName(historicProcessInstance.getProcessDefinitionName());
        List<AssigneeVo> taskAssignee = workflowService.getTaskAssignee(hisTask, appKey);
        taskNodeVo.setAssignee(taskAssignee);

        if (CollectionUtils.isNotEmpty(taskAssignee)) {
            Set<String> assigneeSet = Sets.newHashSet();
            for (AssigneeVo assigneeVo : taskAssignee) {
                assigneeSet.add(assigneeVo.getUserCode());
            }
            taskNodeVo.setAssigneeStr(StringUtils.join(assigneeSet, ","));
        }
        taskNodeVo.setIsFirst(workflowService.isFirstNode(hisTask) ? 1 : 0);
        //获取当前任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisTask.getProcessInstanceId()).list();
        if (CollectionUtils.isNotEmpty(taskList)) {
            List<TaskResultInfo> taskResultInfoList = Lists.newArrayList();
            for (Task t : taskList) {
                TaskResultInfo taskResultInfo = new TaskResultInfo();
                taskResultInfo.setTaskId(t.getId());
                taskResultInfo.setTaskName(t.getName());
                List<AssigneeVo> taskAssignees = workflowService.getTaskAssignee(t, appKey);
                Set<String> assigneeSet = Sets.newHashSet();
                for (AssigneeVo assigneeVo : taskAssignees) {
                    assigneeSet.add(assigneeVo.getUserCode());
                }
                taskResultInfo.setAssignee(assigneeSet);
                taskResultInfoList.add(taskResultInfo);
            }

            taskNodeVo.setCurrentTask(taskResultInfoList);
        }
        return renderSuccess(taskNodeVo);
    }
}
