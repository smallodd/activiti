package com.hengtian.flow.controller;

import com.hengtian.common.enums.ResultEnum;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.param.TaskRemindQueryParam;
import com.hengtian.enquire.service.EnquireService;
import com.hengtian.flow.service.RemindTaskService;
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
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    private EnquireService enquireService;

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
    @RequestMapping(value = "/rest/task/remind/page", method = RequestMethod.POST)
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
    @RequestMapping(value = "/rest/task/reminded/page", method = RequestMethod.POST)
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
    @RequestMapping(value = "/rest/task/open/page", method = RequestMethod.POST)
    public Object openTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        return workflowService.openTaskList(taskQueryParam);
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
    @RequestMapping(value = "/rest/task/close/page", method = RequestMethod.POST)
    public Object closeTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        return workflowService.closeTaskList(taskQueryParam);
    }

    /**
     * 待处理任务列表
     * @param taskQueryParam 任务查询条件实体类
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:35
     */
    @ResponseBody
    @SysLog("待处理任务列表")
    @ApiOperation(httpMethod = "POST", value = "待处理任务列表")
    @RequestMapping(value = "/rest/task/active/page", method = RequestMethod.POST)
    public Object activeTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam){
        return workflowService.activeTaskList(taskQueryParam);
    }

    /**
     * 待签收任务列表
     * @param taskQueryParam 任务查询条件实体类
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/4/23 16:35
     */
    @ResponseBody
    @SysLog("待签收任务列表")
    @ApiOperation(httpMethod = "POST", value = "待签收任务列表")
    @RequestMapping(value = "/rest/task/claim/page", method = RequestMethod.POST)
    public Object claimTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam){
        return workflowService.claimTaskList(taskQueryParam);
    }

    /**
     * 问询任务列表
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @SysLog("问询任务列表")
    @ApiOperation(httpMethod = "POST", value = "问询任务列表")
    @RequestMapping(value = "/rest/task/enquire/page", method = RequestMethod.POST)
    public Object enquireTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        return enquireService.enquireTaskList(taskQueryParam);
    }


    /**
     * 被问询任务列表
     *
     * @param taskQueryParam 任务查询条件实体类
     * @return
     */
    @ResponseBody
    @SysLog("被问询任务列表")
    @ApiOperation(httpMethod = "POST", value = "被问询任务列表")
    @RequestMapping(value = "/rest/task/enquired/page", method = RequestMethod.POST)
    public Object enquiredTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @RequestBody TaskQueryParam taskQueryParam) {
        return enquireService.enquiredTaskList(taskQueryParam);
    }

    /**
     * 问询意见查询接口
     *
     * @param userId 操作人ID
     * @param taskId 任务ID
     * @return
     */
    @ResponseBody
    @SysLog("问询意见查询接口")
    @ApiOperation(httpMethod = "POST", value = "问询意见查询接口")
    @RequestMapping(value = "/rest/task/enquire/comment", method = RequestMethod.POST)
    public Object enquireComment(@ApiParam(value = "操作人ID", name = "userId", required = true) String userId, @ApiParam(value = "任务ID", name = "taskId", required = true) String taskId) {
        return workflowService.enquireComment(userId, taskId);
    }

    /**
     * 流程任务跟踪
     *
     * @param processInstanceId
     * @return
     */
    @SysLog("流程任务跟踪")
    @ApiOperation(httpMethod = "POST", value = "流程任务跟踪")
    @RequestMapping(value = "/rest/process/schedule/{processInstanceId}", method = RequestMethod.POST)
    public void getProcessSchedule(HttpServletResponse response,
                                   @ApiParam(value = "流程实例ID", name = "processInstanceId", required = true)  @PathVariable("processInstanceId") String processInstanceId) {
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

            ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
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
            //生成流图片  5.18.0
            /*InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "PNG", highLightedActivitis, highLightedFlows,
                    processEngineConfiguration.getLabelFontName(),
                    processEngineConfiguration.getActivityFontName(),
                    processEngineConfiguration.getProcessEngineConfiguration().getClassLoader(), 1.0);*/
            //中文显示的是口口口，设置字体就好了
            //5.22.0
            InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,highLightedFlows,
                    processEngineConfiguration.getLabelFontName(),
                    processEngineConfiguration.getActivityFontName(),
                    processEngineConfiguration.getAnnotationFontName(),
                    processEngineConfiguration.getProcessEngineConfiguration().getClassLoader(),1.0);
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
            return renderError(ResultEnum.PARAM_ERROR.msg,ResultEnum.PARAM_ERROR.code);
        }
        List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
        return renderSuccess(commentList);
    }

    /**
     * 评论列表-任务
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
            return renderError(ResultEnum.PARAM_ERROR.msg,ResultEnum.PARAM_ERROR.code);
        }
        List<Comment> commentList = taskService.getTaskComments(taskId);
        return renderSuccess(commentList);
    }
}
