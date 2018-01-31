package com.hengtian.activiti.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hengtian.activiti.model.TMailLog;
import com.hengtian.activiti.model.TUserTask;
import com.hengtian.activiti.service.ActivitiService;
import com.hengtian.activiti.service.TMailLogService;
import com.hengtian.activiti.service.TUserTaskService;
import com.hengtian.activiti.vo.CommentVo;
import com.hengtian.activiti.vo.ProcessDefinitionVo;
import com.hengtian.activiti.vo.TaskVo;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.enums.TaskStatus;
import com.hengtian.common.enums.TaskType;
import com.hengtian.common.enums.TaskVariable;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.result.Result;
import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.common.utils.DateUtils;
import com.hengtian.common.utils.MailTemplateUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.system.model.SysDepartment;
import com.hengtian.system.model.SysUser;
import com.hengtian.system.service.SysDepartmentService;
import com.hengtian.system.service.SysUserService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipInputStream;

@Controller
@RequestMapping("/activiti")
public class ActivitiController extends BaseController{
	Logger logger = Logger.getLogger(ActivitiController.class);
	
	@Autowired
	private ActivitiService activitiService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private IdentityService identityService;
	@Autowired
    private SysUserService sysUserService;
	@Autowired
    private TUserTaskService tUserTaskService;
	@Autowired
	private TMailLogService tMailLogService;
	@Autowired
	private SysDepartmentService sysDepartmentService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	ProcessEngineConfiguration processEngineConfiguration;
	@Autowired
	ProcessEngineFactoryBean processEngine;
	/**
     * 部署流程定义页
     * @return
     */
    @GetMapping("/deployPage")
    public String deployPage() {
        return "activiti/processdefDeploy";
    }
	
	/**
     * 流程部署(压缩包方式)
     * @param 
     * @return
     */
    @SysLog(value="流程部署")
    @PostMapping("/deploy")
    @ResponseBody
    public Object deployZipResource(@RequestParam(value = "file", required = false)MultipartFile deployFile) {
		try {

			repositoryService.createDeployment().name("请假流程")
					.addZipInputStream(new ZipInputStream(deployFile.getInputStream())).deploy();
			return renderSuccess("部署成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return renderError("部署失败！");
		}
    }
    
    /**
     * 流程部署管理页
     * @return
     */
    @GetMapping("/processdefManager")
    public String processdefManager() {
        return "activiti/processdef";
    }
    
    /**
     * 查询流程定义
     * @param processDefinitionVo
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @return
     */
    @SysLog(value="查询流程定义")
    @PostMapping("/processdefDataGrid")
    @ResponseBody
    public PageInfo dataGrid(ProcessDefinitionVo processDefinitionVo, Integer page, Integer rows, String sort,String order,String key) {
    	PageInfo pageInfo = new PageInfo(page, rows);
    	pageInfo.setSort(sort);
    	pageInfo.setOrder(order);
    	Map<String,Object> params = new HashMap<String,Object>();
    	if(StringUtils.isNotBlank(key)){
    		params.put("key",key.trim());
		}
		pageInfo.setCondition(params);
    	activitiService.selectProcessDefinitionDataGrid(pageInfo);
        return pageInfo;
    }
	/**
	 * 所有任务管理页
	 * @return
	 */
	@GetMapping("/allTaskManager")
	public String allTaskManager() {
		return "activiti/allTask";
	}
	/**
	 * 查询所有任务
	 * @param taskVo
	 * @param page
	 * @param rows
	 * @param sort
	 * @param order
	 * @return
	 */
	@SysLog(value="查询所有任务")
	@PostMapping("/allTaskDataGrid")
	@ResponseBody
	public PageInfo alltaskDataGrid(TaskVo taskVo, Integer page, Integer rows, String sort,String order) {
		PageInfo pageInfo = new PageInfo(page, rows);
		activitiService.selectTaskDataGrid(pageInfo,true,taskVo);
		return pageInfo;
	}
    /**
     * 我的任务管理页
     * @return
     */
    @GetMapping("/taskManager")
    public String taskManager() {
        return "activiti/task";
    }
    
    /**
     * 查询我的任务
     * @param taskVo
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @return
     */
    @SysLog(value="查询我的任务")
    @PostMapping("/taskDataGrid")
    @ResponseBody
    public PageInfo taskDataGrid(TaskVo taskVo, Integer page, Integer rows, String sort,String order) {
    	PageInfo pageInfo = new PageInfo(page, rows);
    	pageInfo.setOrder(order);
    	pageInfo.setSort(sort);
    	activitiService.selectTaskDataGrid(pageInfo,false,taskVo);
        return pageInfo;
    }
    
    /**
     * 我的已办任务管理页
     * @return
     */
    @GetMapping("/hisTaskManager")
    public String hisTaskManager() {
        return "activiti/hisTask";
    }
	/**
	 * 所有已办任务管理页
	 * @return
	 */
	@GetMapping("/allHisTaskManager")
	public String allHisTaskManager() {
		return "activiti/allHisTask";
	}
    /**
     * 查询我的已办任务(历史任务)
     * @param taskVo
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @return
     */
    @PostMapping("/hisTaskDataGrid")
    @ResponseBody
    public PageInfo hisTaskDataGrid(TaskVo taskVo, Integer page, Integer rows, String sort,String order) {
    	PageInfo pageInfo = new PageInfo(page, rows);
    	pageInfo.setSort(sort);
    	pageInfo.setOrder(order);
    	activitiService.selectHisTaskDataGrid(pageInfo,false,taskVo);
        return pageInfo;
    }
	/**
	 * 查询所有已办任务(历史任务)
	 * @param taskVo
	 * @param page
	 * @param rows
	 * @param sort
	 * @param order
	 * @return
	 */
	@PostMapping("/allHisTaskDataGrid")
	@ResponseBody
	public PageInfo allHisTaskDataGrid(TaskVo taskVo, Integer page, Integer rows, String sort,String order) {
		PageInfo pageInfo = new PageInfo(page, rows);
		pageInfo.setOrder(order);
		pageInfo.setSort(sort);
		activitiService.selectHisTaskDataGrid(pageInfo,true,taskVo);
		return pageInfo;
	}
    
    
    /**
     * 办理页面(请假业务)
     * @return
     */
    @GetMapping("/complateTaskPage")
    public String complateTaskPage(Model model,String id) {
    	Task task = taskService.createTaskQuery().taskId(id).singleResult();
    	String processInstanceId = task.getProcessInstanceId();

		List<CommentVo> comments = new ArrayList<CommentVo>();
		List<Comment> commentList= taskService.getProcessInstanceComments(processInstanceId);
		for(Comment comment : commentList){
			CommentVo vo = new CommentVo();
			SysUser user= sysUserService.selectById(comment.getUserId());
			vo.setCommentUser(user.getUserName());
			vo.setCommentTime(DateUtils.formatDateToString(comment.getTime()));
			vo.setCommentContent(comment.getFullMessage());
			comments.add(vo);
		}

		model.addAttribute("task", task);
		model.addAttribute("comments", comments);
		
    	String taskKey = task.getTaskDefinitionKey();
    	if(StringUtils.contains("SVacation_Modify", taskKey)){
    		return "application/tVacationModify";
    	}else if(StringUtils.contains("SVacation_Terminate", taskKey)){
    		return "application/tVacationTerminate";
    	}
        return "activiti/taskComplate";
    }
    
    /**
     * 办理任务(完成任务)
     * @param taskId
     * @param commentContent
     * @param commentResult
     * @return
     */
    @SysLog(value="办理任务")
    @RequestMapping("/complateTask")
    @ResponseBody
    public Object complateTask( @RequestParam("taskId") String taskId,
								@RequestParam("userId") String userId,
					    		@RequestParam("commentContent") String commentContent,
					    		@RequestParam("commentResult") Integer commentResult){
		Result result= activitiService.completeTask(taskId,userId,commentContent,commentResult);
		return JSONObject.toJSONString(result);
    }

    /**
     * 签收任务
     * @param id
     * @return
     */
    @SysLog(value="签收任务")
    @RequestMapping("/claimTask")
    @ResponseBody
    public Object claimTask(String id){
		try {
			ShiroUser user = getShiroUser();
			activitiService.claimTask(user.getId(), id);
			return renderSuccess("签收成功！");
		}catch (ActivitiObjectNotFoundException e){
			return renderError("此任务不存在！任务签收失败！");
		}catch (ActivitiTaskAlreadyClaimedException e) {
			return renderError("此任务已被其他组成员签收！请刷新页面重新查看！");
		}catch (Exception e) {
			return renderError("任务签收失败！请联系管理员！");
		} 
    }
    
    /**
     * 委派页面(与转办共用一个页面) 
     */
    @GetMapping("/taskDelegate")
    public String taskAssignee(Model model,String taskId) {
		model.addAttribute("taskId",taskId);
        return "activiti/taskDelegate";
    }
    
    /**
     * 委派任务
     * @param taskId
     * @param userId
     * @return
     */
    @SysLog(value="委派任务")
    @RequestMapping("/delegateTask")
    @ResponseBody
    public Object delegateTask(String taskId , String userId){
    	try {
			activitiService.delegateTask(userId, taskId);
			return renderSuccess("委派任务成功！");
		} catch (ActivitiObjectNotFoundException e){
			return renderError("此任务不存在！委派任务失败！");
		} catch (Exception e) {
			return renderError("委派任务失败，系统错误！");
		}
    }
    
    /**
     * 转办任务
     * @param taskId
     * @param userId
     * @return
     */
    @SysLog(value="转办任务")
    @RequestMapping("/transferTask")
    @ResponseBody
    public Object transferTask(String taskId, String userId, String transferUserId){
    	try {
			Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    		if(task == null){
				return renderError("此任务不存在！转办任务失败！");
			}
			ShiroUser user = getShiroUser();
    		if(ConstantUtils.ADMIN_ID.equals(user.getId()) || user.getId().equals(userId)){
				String taskType = taskService.getVariable(taskId, task.getTaskDefinitionKey()+":"+TaskVariable.TASKTYPE.value)+"";
				if(TaskType.COUNTERSIGN.value.equals(taskType)){
					//会签
					//修改会签人
					String candidateIds = taskService.getVariable(task.getId(), task.getTaskDefinitionKey()+":"+TaskVariable.TASKUSER.value)+"";
					if(StringUtils.contains(candidateIds, transferUserId)){
						return renderError("【"+transferUserId+"】已在当前任务中<br/>（同一任务节点同一个人最多可办理一次）");
					}

					taskService.setAssignee(task.getId(),task.getAssignee().replace(userId,transferUserId));
					//修改会签人相关属性值
					Map<String,Object> variable = Maps.newHashMap();
					variable.put(task.getTaskDefinitionKey() + ":" + userId, TaskStatus.TRANSFER.value);
					variable.put(task.getTaskDefinitionKey() + ":" + transferUserId, transferUserId+":"+TaskStatus.UNFINISHED.value);
					variable.put(task.getTaskDefinitionKey() + ":"+TaskVariable.TASKUSER.value, candidateIds.replace(userId,transferUserId));
					taskService.setVariablesLocal(taskId, variable);
				}else{
					Map<String,Object> variable = Maps.newHashMap();
					variable.put(task.getTaskDefinitionKey() + ":" + userId, TaskStatus.TRANSFER.value);
					variable.put(task.getTaskDefinitionKey() + ":" + transferUserId, transferUserId+":"+TaskStatus.UNFINISHED.value);
					variable.put(task.getTaskDefinitionKey() + ":"+TaskVariable.TASKUSER.value, transferUserId);
					taskService.setVariablesLocal(taskId, variable);
					activitiService.transferTask(transferUserId, taskId);
				}
				return renderSuccess("转办任务成功！");
			}else{
				return renderError("您所在的用户组没有权限进行该操作！");
			}
		} catch (ActivitiObjectNotFoundException e){
			return renderError("此任务不存在！转办任务失败！");
		} catch (Exception e) {
			return renderError("委派任务失败，系统错误！");
		}
    }

	/**
	 * 任务转办前-获取任务审核人员
	 * @param taskId
	 * @return
	 */
	@SysLog(value="获取任务审核人员")
	@RequestMapping("/getTaskUser")
	@ResponseBody
	public Object getTaskUser(String taskId){
		try {
			if(StringUtils.isBlank(taskId)){
				return renderError("任务ID为空！");
			}
			Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
			if(task != null){
				String candidateIds = taskService.getVariable(taskId, task.getTaskDefinitionKey() + ":" + TaskVariable.TASKUSER.value)+"";
				if(StringUtils.isNotBlank(candidateIds)){
					EntityWrapper<SysUser> wrapper =new EntityWrapper<SysUser>();
					wrapper.in("id",candidateIds.split(","));
					List<SysUser> sysUsers = sysUserService.selectList(wrapper);

					return sysUsers;
				}
			}else{
				return renderError("任务不存在！");
			}
		} catch (Exception e) {
			logger.info("获取任务审批人失败！",e);
			return renderError("获取任务审批人失败！");
		}

		return renderError("未找到任务对应的审核人员！");
	}

	/**
	 * 获取任务节点未完成任务审核人员
	 * @param taskId
	 * @return
	 */
	@SysLog(value="获取任务节点未完成任务审核人员")
	@RequestMapping("/getTaskUserWithEnd")
	@ResponseBody
	public Object getTaskUserWithEnd(String taskId){
		try {
			if(StringUtils.isBlank(taskId)){
				return renderError("任务ID为空！");
			}
			Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
			if(task != null){
				ShiroUser user = getShiroUser();
				if(!ConstantUtils.ADMIN_ID.equals(user.getId())){
					SysUser sysUser = sysUserService.selectById(user.getId());
					return Lists.newArrayList(sysUser);
				}
				Map<String, Object> variablesLocal = taskService.getVariablesLocal(taskId);
				Iterator<String> iterator = variablesLocal.keySet().iterator();
				List<String> candidateIdList = Lists.newArrayList();
				while(iterator.hasNext()){
					String key = iterator.next();
					if(StringUtils.contains(variablesLocal.get(key)+"",TaskStatus.UNFINISHED.value)){
						candidateIdList.add(key.replace(task.getTaskDefinitionKey()+":",""));
					}
				}
				if(CollectionUtils.isNotEmpty(candidateIdList)){
					EntityWrapper<SysUser> wrapper =new EntityWrapper<SysUser>();
					wrapper.in("id",candidateIdList);
					List<SysUser> sysUsers = sysUserService.selectList(wrapper);

					return sysUsers;
				}
			}else{
				return renderError("任务不存在！");
			}
		} catch (Exception e) {
			logger.info("获取任务审批人失败！",e);
			return renderError("获取任务审批人失败！");
		}

		return renderError("未找到任务对应的审核人员！");
	}
    
    /**
     * 任务跳转页面 
     */
    @GetMapping("/taskJump")
    public String taskJump(Model model,@RequestParam("taskId") String taskId) {
    	Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    	//查询流程定义
    	ProcessDefinition pd= repositoryService.createProcessDefinitionQuery()
    	.processDefinitionId(task.getProcessDefinitionId()).singleResult();
    	//根据流程定义KEY查询用户任务
    	EntityWrapper<TUserTask> wrapper =new EntityWrapper<TUserTask>();
		wrapper.where("proc_def_key = {0}", pd.getKey()).andNew("version_={0}",pd.getVersion());
		wrapper.orderBy("order_num",true);
		List<TUserTask> tasks= tUserTaskService.selectList(wrapper);
    	model.addAttribute("tasks",tasks);
		model.addAttribute("taskId",taskId);
        return "activiti/taskJump";
    }
    
    /**
     * 任务跳转
     * @param taskId
     * @param taskDefinitionKey
     * @return
     */
    @SysLog(value="任务跳转")
    @RequestMapping("/jumpTask")
    @ResponseBody
    public Object jumpTask(String taskId , String taskDefinitionKey){
    	try {
			activitiService.jumpTask(taskId, taskDefinitionKey);
			return renderSuccess("任务跳转成功！");
		} catch (Exception e) {
    		logger.info("任务跳转失败！",e);
			return renderError("任务跳转失败！");
		}
    }
    
    /**
     * 获取流程资源文件
     */
    @RequestMapping("/getProcessResource")
    public void getProcessResource(
    		@RequestParam("type") String resourceType,
    		@RequestParam("pdid") String processDefinitionId, 
    		HttpServletResponse response,HttpServletRequest request){
    	try {
    		if(resourceType.equals("image")){
    			ProcessDefinition processDefinition=repositoryService.getProcessDefinition(processDefinitionId);
    			org.activiti.engine.repository.Model model=repositoryService.createModelQuery().deploymentId(processDefinition.getDeploymentId()).deployed().singleResult();
				String contextPath = request.getSession().getServletContext().getRealPath("image");
				FileInputStream fileInputStream=new FileInputStream(contextPath+ File.separator+model.getId()+".png");
				byte[] b = new byte[1024];
				int len = -1;
				while ((len = fileInputStream.read(b, 0, 1024)) != -1) {
					response.getOutputStream().write(b, 0, len);
				}
				fileInputStream.close();
				return;
			}
			InputStream in = activitiService.getProcessResource(resourceType, processDefinitionId);
			byte[] b = new byte[1024];
			int len = -1;
			while ((len = in.read(b, 0, 1024)) != -1) {
			    response.getOutputStream().write(b, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    /**
     * 挂起流程
     * @return
     */
    @SysLog(value="挂起流程")
    @RequestMapping("/sleep")
    @ResponseBody
    public Object sleep(String id){
    	try {
    		repositoryService.suspendProcessDefinitionById(id);
			return renderSuccess("流程挂起成功！");
		} catch (Exception e) {
			return renderError("流程挂起失败！");
		}
    }
    
    /**
     * 激活流程
     * @return
     */
    @SysLog(value="激活流程")
    @RequestMapping("/active")
    @ResponseBody
    public Object active(String id){
    	try {
    		repositoryService.activateProcessDefinitionById(id);
			return renderSuccess("流程激活成功！");
		} catch (Exception e) {
			return renderError("流程激活失败！");
		}
    }

	@RequestMapping("/showTask/{processInstanceId}")
	public void showTask(HttpServletRequest request, HttpServletResponse response, @PathVariable("processInstanceId") String processInstanceId) throws IOException{

		//获取历史流程实例
		HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		//获取流程图
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
		processEngineConfiguration = processEngine.getProcessEngineConfiguration();
		Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);

		ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
		ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

		List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();

		//高亮环节id集合
		List<String> highLightedActivitis = new ArrayList<String>();

		//任务跳转时处理
		EntityWrapper<TUserTask> wrapper =new EntityWrapper<TUserTask>();
		wrapper.where("proc_def_key = {0}", definitionEntity.getKey()).andNew("version_={0}",definitionEntity.getVersion());
		wrapper.orderBy("order_num",true);
		List<TUserTask> userTaskList= tUserTaskService.selectList(wrapper);
		Map<String,Integer> taskMap = new HashMap<String,Integer>();

		if(CollectionUtils.isNotEmpty(userTaskList)){
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			for (TUserTask u:userTaskList) {
				taskMap.put(u.getTaskDefKey(),1);
				if(u.getTaskDefKey().equals(task.getTaskDefinitionKey())){
					break;
				}
			}
			Iterator<HistoricActivityInstance> it = highLightedActivitList.iterator();
			while(it.hasNext()){
				HistoricActivityInstance hai = it.next();
				if(!taskMap.containsKey(hai.getActivityId()) && "userTask".equals(hai.getActivityType())){
					it.remove();
				}
			}
		}

		//高亮线路id集合
		List<String> highLightedFlows = getHighLightedFlows(definitionEntity,highLightedActivitList);

		for(HistoricActivityInstance tempActivity : highLightedActivitList){
			String activityId = tempActivity.getActivityId();
			highLightedActivitis.add(activityId);
		}

		//中文显示的是口口口，设置字体就好了
		//生成流图片  5.18.0
		InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "PNG", highLightedActivitis, highLightedFlows,
				processEngineConfiguration.getLabelFontName(),
				processEngineConfiguration.getActivityFontName(),
				processEngineConfiguration.getProcessEngineConfiguration().getClassLoader(), 1.0);
		//5.22.0
		//InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,highLightedFlows,"宋体","宋体","宋体",null,1.0);
		//单独返回流程图，不高亮显示
//      InputStream imageStream = diagramGenerator.generatePngDiagram(bpmnModel);
		// 输出资源内容到相应对象
		byte[] b = new byte[1024];
		int len;
		while ((len = imageStream.read(b, 0, 1024)) != -1) {
			response.getOutputStream().write(b, 0, len);
		}

	}
	/**
	 * 获取需要高亮的线
	 * @param processDefinitionEntity
	 * @param historicActivityInstances
	 * @return
	 */
	private List<String> getHighLightedFlows(
			ProcessDefinitionEntity processDefinitionEntity,
			List<HistoricActivityInstance> historicActivityInstances) {

		List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
		for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
			ActivityImpl activityImpl = processDefinitionEntity
					.findActivity(historicActivityInstances.get(i)
							.getActivityId());// 得到节点定义的详细信息
			List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
			ActivityImpl sameActivityImpl1 = processDefinitionEntity
					.findActivity(historicActivityInstances.get(i + 1)
							.getActivityId());
			// 将后面第一个节点放在时间相同节点的集合里
			sameStartTimeNodes.add(sameActivityImpl1);
			for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
				HistoricActivityInstance activityImpl1 = historicActivityInstances
						.get(j);// 后续第一个节点
				HistoricActivityInstance activityImpl2 = historicActivityInstances
						.get(j + 1);// 后续第二个节点
				if (activityImpl1.getStartTime().equals(
						activityImpl2.getStartTime())) {
					// 如果第一个节点和第二个节点开始时间相同保存
					ActivityImpl sameActivityImpl2 = processDefinitionEntity
							.findActivity(activityImpl2.getActivityId());
					sameStartTimeNodes.add(sameActivityImpl2);
				} else {
					// 有不相同跳出循环
					break;
				}
			}
			List<PvmTransition> pvmTransitions = activityImpl
					.getOutgoingTransitions();// 取出节点的所有出去的线
			for (PvmTransition pvmTransition : pvmTransitions) {
				// 对所有的线进行遍历
				ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
						.getDestination();
				// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
				if (sameStartTimeNodes.contains(pvmActivityImpl)) {
					highFlows.add(pvmTransition.getId());
				}
			}
		}
		return highFlows;
	}

	/**
	 * 发送邮件
	 */
	private void sendMail(String fromUserId,String toUserId,Integer commentResult,String commentContent){
		//发送邮件
		SysUser mailToUser= sysUserService.selectById(fromUserId);
		SysUser mailFromUser= sysUserService.selectById(toUserId);
		SysDepartment dept=sysDepartmentService.selectById(mailFromUser.getDepartmentId());
		String resultStr="";
		if(ConstantUtils.vacationStatus.PASSED.getValue()==commentResult){
			resultStr="办理通过";
		}else if(ConstantUtils.vacationStatus.NOT_PASSED.getValue()==commentResult){
			resultStr="办理不通过，请在系统中我的任务栏调整申请";
		}
		Map<String,String> templateStr = new HashMap<String,String>();
		templateStr.put("deptName", dept.getDepartmentName());
		templateStr.put("complateUserName", mailFromUser.getUserName());
		templateStr.put("resultStr", resultStr);
		templateStr.put("commentContent", commentContent);
		String resultText= MailTemplateUtils.getMailTemplate(templateStr);

		Map<String,Object> params = new HashMap<String,Object>();
		params.put("from", ConstantUtils.MAIL_ADDRESS);
		params.put("to", mailToUser.getUserEmail());
		params.put("subject", "请假业务办理进度");
		params.put("text", resultText);
		activitiService.sendMailService(params);

		//添加发送邮件的记录
		TMailLog mailLog = new TMailLog();
		mailLog.setMailFrom(mailFromUser.getUserName());
		mailLog.setMailTo(mailToUser.getUserName());
		mailLog.setMailSubject("请假业务办理进度");
		mailLog.setMailText(resultText);
		mailLog.setSendTime(new Date());
		tMailLogService.insert(mailLog);
	}
}
