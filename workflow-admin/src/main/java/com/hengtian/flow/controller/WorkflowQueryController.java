package com.hengtian.flow.controller;

import com.google.common.collect.Lists;
import com.hengtian.common.base.BaseResponse;
import com.hengtian.common.enums.ResultEnum;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.*;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.workflow.activiti.CustomDefaultProcessDiagramGenerator;
import com.hengtian.flow.service.RemindTaskService;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.TWorkDetailService;
import com.hengtian.flow.service.WorkflowService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ma on 2018/4/17.
 * 所有列表查询都放这里
 */
@Controller
public class WorkflowQueryController extends WorkflowBaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
    @RequestMapping(value = "/rest/procInst", method = RequestMethod.POST)
    public BaseResponse processInstanceList(@ApiParam(value = "流程查询条件", name = "processInstanceQueryParam", required = true) @RequestBody @Validated ProcessInstanceQueryParam processInstanceQueryParam) {
        logger.info("----------------查询获取父级任务节点开始,入参 taskId：{}----------------", processInstanceQueryParam.toString());
        PageInfo pageInfo = new PageInfo(processInstanceQueryParam.getPage(), processInstanceQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(processInstanceQueryParam));
        workflowService.processInstanceList(pageInfo);

        return BaseResponse.successCustom().setData(pageInfo).build();
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
    public Object remindTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskRemindQueryParam taskRemindQueryParam) {
        return remindTaskService.remindTaskList(taskRemindQueryParam);
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
    public Object remindedTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskRemindQueryParam taskRemindQueryParam) {
        return remindTaskService.remindedTaskList(taskRemindQueryParam);
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
    public Object openTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        if(StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() != null){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(taskQueryParam));
        workflowService.openTaskList(pageInfo);

        return pageInfo;
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
    public Object closeTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        if(StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() != null){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(taskQueryParam));
        workflowService.closeTaskList(pageInfo);

        return pageInfo;
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
    @ApiOperation(httpMethod = "POST", value = "待处理任务列表")
    @RequestMapping(value = "/rest/task/active", method = RequestMethod.POST)
    public Object activeTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        if(StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() != null){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }

        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(taskQueryParam));

        setAssigneeAndRole(pageInfo, taskQueryParam.getAssignee(), taskQueryParam.getAppKey());

        workflowService.activeTaskList(pageInfo);

        return pageInfo;
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
    public Object claimTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        if(StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() != null){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(taskQueryParam));

        setAssigneeAndRole(pageInfo, taskQueryParam.getAssignee(), taskQueryParam.getAppKey());

        workflowService.claimTaskList(pageInfo);

        return pageInfo;
    }

    /**
     * 问询任务列表
     *
     * @param taskEnquireParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @SysLog("问询任务列表")
    @ApiOperation(httpMethod = "POST", value = "问询任务列表")
    @RequestMapping(value = "/rest/task/enquire", method = RequestMethod.POST)
    public Object enquireTaskList(@ApiParam(value = "任务查询条件", name = "taskEnquireParam", required = true) @RequestBody AskTaskParam taskEnquireParam) {
        if (StringUtils.isBlank(taskEnquireParam.getCreateId())) {
            return new Result(false, "createId不能为空");
        }
        return tAskTaskService.enquireTaskList(taskEnquireParam);
    }


    /**
     * 被问询任务列表
     *
     * @param taskEnquireParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @SysLog("被问询任务列表")
    @ApiOperation(httpMethod = "POST", value = "被问询任务列表")
    @RequestMapping(value = "/rest/task/enquired", method = RequestMethod.POST)
    public Object enquiredTaskList(@ApiParam(value = "任务查询条件", name = "taskEnquireParam", required = true) @RequestBody AskTaskParam taskEnquireParam) {
        if (StringUtils.isBlank(taskEnquireParam.getAskUserId())) {
            return new Result(false, "askUserId不能为空");
        }
        return tAskTaskService.enquiredTaskList(taskEnquireParam);
    }

    /**
     * 问询意见查询接口
     *
     * @param userId 操作人ID
     * @param askId  问询id
     * @return
     */
    @ResponseBody
    @SysLog("问询意见查询接口")
    @ApiOperation(httpMethod = "POST", value = "问询意见查询接口")
    @RequestMapping(value = "/rest/task/enquire/comment", method = RequestMethod.POST)
    public Object enquireComment(@ApiParam(value = "操作人ID", name = "userId", required = true) @RequestParam String userId,
                                 @ApiParam(value = "问询id", name = "askId", required = true) @RequestParam String askId) {
        return workflowService.askComment(userId, askId);
    }

    /**
     * 操作流程详细信息
     *
     * @param workDetailParam 任务查询条件
     * @return
     */
    @ResponseBody
    @SysLog("操作流程详细信息")
    @ApiOperation(httpMethod = "POST", value = "操作流程详细信息")
    @RequestMapping(value = "/rest/task/operateDetailInfo", method = RequestMethod.POST)
    public Object operateDetailInfo(@ApiParam(value = "任务查询条件", name = "workDetailParam", required = true) @RequestBody WorkDetailParam workDetailParam) {
        return tWorkDetailService.operateDetailInfo(workDetailParam);
    }

    /**
     * 流程任务跟踪
     *
     * @param processInstanceId
     * @return
     */
    @SysLog("流程任务跟踪")
    @ApiOperation(httpMethod = "GET", value = "流程任务跟踪")
    @RequestMapping(value = "/rest/process/schedule/{processInstanceId}", method = RequestMethod.GET)
    public void getProcessSchedule(HttpServletResponse response,
                                   @ApiParam(value = "流程实例ID", name = "processInstanceId", required = true) @PathVariable("processInstanceId") String processInstanceId) {
        logger.info("----------------获取流程跟踪图开始,入参 processInstanceId：{}----------------", processInstanceId);
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
            for(Task task : taskList){
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
            logger.error("获取流程任务跟踪标识图失败", e);
        }
        logger.info("----------------获取流程跟踪图开始结束----------------", processInstanceId);
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
    public Object processCommentList(@ApiParam(value = "流程实例ID", name = "processInstanceId", required = true) String processInstanceId) {
        logger.info("----------------查询审批意见列表开始,入参 processInstanceId：{}----------------", processInstanceId);
        if (StringUtils.isBlank(processInstanceId)) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
        return renderSuccess(commentList);
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
    public Object taskCommentList(@ApiParam(value = "任务ID", name = "taskId", required = true) String taskId) {
        logger.info("----------------查询审批意见列表开始,入参 taskId：{}----------------", taskId);
        if (StringUtils.isBlank(taskId)) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        List<Comment> commentList = taskService.getTaskComments(taskId);
        return renderSuccess(commentList);
    }


    /**
     * 获取父级任务节点
     *
     * @param taskId 任务ID
     * @return
     */
    @ResponseBody
    @SysLog("获取父级任务节点")
    @ApiOperation(httpMethod = "POST", value = "获取父级任务节点")
    @RequestMapping(value = "/rest/parentNode", method = RequestMethod.POST)
    public Object getParentNodes(@ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId, @ApiParam(value = "操作人ID", name = "userId", required = true) @RequestParam String userId,
                                 @ApiParam(value = "是否递归获取父级节点", name = "isAll", required = true) @RequestParam(defaultValue = "1") Integer isAll) {
        logger.info("----------------查询获取父级任务节点开始,入参 taskId：{}----------------", taskId);
        return renderSuccess(workflowService.getParentNodes(taskId, userId, isAll != 0));
    }

}
