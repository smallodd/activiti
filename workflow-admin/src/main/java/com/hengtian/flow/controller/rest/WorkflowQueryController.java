package com.hengtian.flow.controller.rest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hengtian.common.enums.ApproveResultEnum;
import com.hengtian.common.enums.ResultEnum;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.param.AskTaskParam;
import com.hengtian.common.param.ProcessInstanceQueryParam;
import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.param.TaskRemindQueryParam;
import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.workflow.activiti.CustomDefaultProcessDiagramGenerator;
import com.hengtian.flow.controller.WorkflowBaseController;
import com.hengtian.flow.model.ProcessInstanceResult;
import com.hengtian.flow.service.RemindTaskService;
import com.hengtian.flow.service.TAskTaskService;
import com.hengtian.flow.service.TWorkDetailService;
import com.hengtian.flow.service.WorkflowService;
import com.rbac.entity.RbacRole;
import com.rbac.service.PrivilegeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
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
import java.util.Map;

/**
 * 所有列表查询都放这里
 * @author mayunliang@chtwm.com
 * date 2018/4/17 9:38
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
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private RuntimeService runtimeService;

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
    public Object processInstanceList(@ApiParam(value = "流程查询条件", name = "processInstanceQueryParam", required = true) @ModelAttribute @Validated ProcessInstanceQueryParam processInstanceQueryParam) {
        logger.info("----------------查询获取父级任务节点开始,入参 taskId：{}----------------", processInstanceQueryParam.toString());
        PageInfo pageInfo = new PageInfo(processInstanceQueryParam.getPage(), processInstanceQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(processInstanceQueryParam));
        workflowService.processInstanceList(pageInfo);

        return renderSuccess(pageInfo);
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
    public Object remindTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskRemindQueryParam taskRemindQueryParam) {
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
    public Object remindedTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskRemindQueryParam taskRemindQueryParam) {
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
    public Object openTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskQueryParam taskQueryParam) {
        if(StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() == null){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(taskQueryParam));

        setAssigneeAndRole(pageInfo, taskQueryParam.getAssignee(), taskQueryParam.getAppKey());
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
    public Object closeTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskQueryParam taskQueryParam) {
        if(StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() == null){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        if(taskQueryParam.getTaskState() == null){
            taskQueryParam.setTaskState("");
        }else if (ApproveResultEnum.AGREE.result.equals(taskQueryParam.getTaskState()) || ApproveResultEnum.REFUSE.result.equals(taskQueryParam.getTaskState())){
            return renderError(ResultEnum.PARAM_ERROR.msg+"必须为_Y或_N或为''", ResultEnum.PARAM_ERROR.code);
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
    @ApiOperation(httpMethod = "POST", value = "待处理任务列表")
    @RequestMapping(value = "/rest/task/active", method = RequestMethod.POST)
    public Object activeTaskList(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskQueryParam taskQueryParam) {
        if(StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() == null){
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
        if(StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() == null){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        PageInfo pageInfo = new PageInfo(taskQueryParam.getPage(), taskQueryParam.getRows());
        pageInfo.setCondition(new BeanMap(taskQueryParam));

        setAssigneeAndRole(pageInfo, taskQueryParam.getAssignee(), taskQueryParam.getAppKey());

        workflowService.claimTaskList(pageInfo);

        return renderSuccess(pageInfo);
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
    public Object enquireTaskList(@ApiParam(value = "任务查询条件", name = "taskEnquireParam", required = true) @ModelAttribute AskTaskParam taskEnquireParam) {
        if (StringUtils.isBlank(taskEnquireParam.getCreateId())) {
            return new Result(false, "createId不能为空");
        }
        return renderSuccess(tAskTaskService.enquireTaskList(taskEnquireParam));
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
    public Object enquiredTaskList(@ApiParam(value = "任务查询条件", name = "taskEnquireParam", required = true) @ModelAttribute AskTaskParam taskEnquireParam) {
        if (StringUtils.isBlank(taskEnquireParam.getAskUserId())) {
            return new Result(false, "askUserId不能为空");
        }
        return renderSuccess(tAskTaskService.enquiredTaskList(taskEnquireParam));
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
     * @param processInstanceId 流程实例ID
     * @param businessKey 业务主键
     * @return
     */
    @ResponseBody
    @SysLog("操作流程详细信息")
    @ApiOperation(httpMethod = "POST", value = "操作流程详细信息")
    @RequestMapping(value = "/rest/process/operate/detail", method = RequestMethod.POST)
    public Object operateDetailInfo(@ApiParam(value = "流程实例ID", name = "processInstanceId") @RequestParam(required = false) String processInstanceId,
                                    @ApiParam(value = "业务主键", name = "businessKey", required = true) @RequestParam String businessKey) {
        return renderSuccess(tWorkDetailService.operateDetailInfo(processInstanceId, null,businessKey));
    }

    /**
     * 流程实例详情
     *
     * @param processInstanceId 流程实例ID
     * @param businessKey 业务主键
     * @return
     */
    @ResponseBody
    @SysLog("流程实例详情")
    @ApiOperation(httpMethod = "POST", value = "流程实例详情")
    @RequestMapping(value = "/rest/process/detail", method = RequestMethod.POST)
    public Object processDetail(@ApiParam(value = "流程实例ID", name = "processInstanceId") @RequestParam(required = false) String processInstanceId,
                                @ApiParam(value = "业务主键", name = "businessKey", required = true) @RequestParam String businessKey) {
        logger.info("入参processInstanceId{0} businessKey{1}", processInstanceId, businessKey);
        if(StringUtils.isNotBlank(processInstanceId)){
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if(processInstance == null){
                return renderError("流程实例ID【"+processInstanceId+"】无对应的流程实例");
            }
            JSONObject result = new JSONObject();
            result.put("processInstanceId", processInstance.getProcessInstanceId());
            result.put("processDefinitionId", processInstance.getProcessDefinitionId());
            result.put("processDefinitionName", processInstance.getProcessDefinitionName());
            return renderSuccess(result);
        }else if(StringUtils.isNotBlank(businessKey)){
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
            if(processInstance == null){
                return renderError("业务主键【"+businessKey+"】无对应的流程实例");
            }
            JSONObject result = new JSONObject();
            result.put("processInstanceId", processInstance.getProcessInstanceId());
            result.put("processDefinitionId", processInstance.getProcessDefinitionId());
            result.put("processDefinitionName", processInstance.getProcessDefinitionName());
            return renderSuccess(result);
        }else{
            return renderError("参数异常");
        }
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
    public Object processCommentList(@ApiParam(value = "流程实例ID", name = "processInstanceId", required = true) @RequestParam String processInstanceId) {
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
    public Object taskCommentList(@ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId) {
        logger.info("----------------查询审批意见列表开始,入参 taskId：{}----------------", taskId);
        if (StringUtils.isBlank(taskId)) {
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        List<Comment> commentList = taskService.getTaskComments(taskId);
        return renderSuccess(commentList);
    }

    /**
     * 获取父级任务节点
     * @param taskId 任务ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/29 14:38
     */
    @ResponseBody
    @SysLog("获取父级任务节点")
    @ApiOperation(httpMethod = "POST", value = "获取父级任务节点")
    @RequestMapping(value = "/rest/node/before", method = RequestMethod.POST)
    public Object getBeforeNodes(@ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId,
                                 @ApiParam(value = "操作人ID", name = "userId", required = true) @RequestParam String userId,
                                 @ApiParam(value = "是否递归获取父级节点", name = "isAll", required = true) @RequestParam(defaultValue = "1") Integer isAll) {
        logger.info("----------------查询获取父级任务节点开始,入参 taskId：{}----------------", taskId);
        return workflowService.getBeforeNodes(taskId, userId, isAll != 0,false);
    }
    /**
     * 获取可问询任务节点
     * @param taskId 任务ID
     * @return
     *
     * date 2018/5/29 14:38
     */
    @ResponseBody
    @SysLog("获取可问询任务节点")
    @ApiOperation(httpMethod = "POST", value = "获取可问询任务节点")
    @RequestMapping(value = "/rest/askNodes", method = RequestMethod.POST)
    public Object getAskNodes(@ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId,
                                 @ApiParam(value = "操作人ID", name = "userId", required = true) @RequestParam String userId) {
        logger.info("----------------获取可问询任务节点,入参 taskId：{}----------------", taskId);
        return workflowService.getBeforeNodes(taskId, userId, true,true);
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
    @SysLog("待处理任务总数")
    @ApiOperation(httpMethod = "POST", value = "待处理任务总数")
    @RequestMapping(value = "/rest/task/active/count", method = RequestMethod.POST)
    public Object activeTaskCount(@ApiParam(value = "任务查询条件", name = "taskQueryParam", required = true) @ModelAttribute TaskQueryParam taskQueryParam) {
        if(StringUtils.isBlank(taskQueryParam.getAssignee()) || taskQueryParam.getAppKey() == null){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }

        Map<String,Object> paraMap = Maps.newHashMap();
        BeanMap beanMap = new BeanMap(taskQueryParam);
        paraMap.putAll(beanMap);

        List<RbacRole> roles = privilegeService.getAllRoleByUserId(taskQueryParam.getAppKey(), taskQueryParam.getAssignee());
        String roleId = null;

        for(RbacRole role : roles){
            roleId = roleId == null?role.getId()+"":roleId+""+role.getId();
        }

        if(StringUtils.isNotBlank(roleId)){
            paraMap.put("roleId", roleId);
        }
        Long count = workflowService.activeTaskCount(paraMap);
        return renderSuccess(count);
    }

    /**
     * 查询用户某个任务是否审批过
     * @param userCode  用户主键
     * @param businessKey  业务主键
     * @param appKey   appKey
     * @return
     */
    @SysLog("查询用户是否已经审批过某个任务")
    @ApiOperation(httpMethod = "POST", value = "查询用户是否已经审批过某个任务")
    @RequestMapping(value = "/rest/task/checkUserApproved", method = RequestMethod.POST)
    @ResponseBody
    public Object checkUserApproved(@ApiParam(value = "用户code", name = "userCode", required = true) @RequestParam("userCode") String userCode,
                                    @ApiParam(value = "业务主键", name = "businessKey", required = true) @RequestParam("businessKey")String businessKey,
                                    @ApiParam(value = "系统键值", name = "appKey",  required = true) @RequestParam("appKey")String appKey){
        Result result=new Result();
        if(StringUtils.isBlank(userCode)||StringUtils.isBlank(businessKey)||StringUtils.isBlank(appKey)){

            result.setSuccess(false);
            result.setCode(Constant.PARAM_ERROR);
            result.setMsg("参数错误！");
            return result;
        }
        List<HistoricTaskInstance> list=   historyService.createHistoricTaskInstanceQuery().processInstanceBusinessKey(businessKey).processVariableValueEquals("appKey", appKey).taskAssigneeLike("%"+userCode+"").orderByTaskCreateTime().desc().list();

        if(list!=null&&list.size()>0){
            HistoricTaskInstance historicTaskInstance=list.get(0);
            if(historicTaskInstance.getEndTime()!=null){
                result.setMsg("用户已经审批过");
                result.setSuccess(true);
                result.setCode(Constant.SUCCESS);
                return result;
            }else{
                result.setMsg("用户未审批过");
                result.setSuccess(false);
                result.setCode(Constant.SUCCESS);
                return result;
            }
        }else{
            result.setMsg("任务信息不存在");
            result.setSuccess(false);
            result.setCode(Constant.TASK_NOT_EXIT);
            return result;
        }
    }

    /**
     * 任务详情
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
                             @ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId){
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(taskId)){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        return workflowService.taskDetail(userId, taskId);
    }

    /**
     * 下步节点审批人
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
                                  @ApiParam(value = "任务ID", name = "taskId", required = true) @RequestParam String taskId){
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(taskId)){
            return renderError(ResultEnum.PARAM_ERROR.msg, ResultEnum.PARAM_ERROR.code);
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task == null){
            return renderError("taskId无效");
        }

        if(!validateTaskAssignee(task, userId)){
            return renderError("【"+userId+"】没有权限");
        }

        JSONArray result = workflowService.getNextAssigneeWhenRoleApprove(task);
        return renderSuccess(result);
    }
}
