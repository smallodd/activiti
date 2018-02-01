package com.hengtian.activiti.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hengtian.activiti.model.TUserTask;
import com.hengtian.activiti.service.ActivitiService;
import com.hengtian.activiti.service.TUserTaskService;
import com.hengtian.activiti.vo.CommonVo;
import com.hengtian.activiti.vo.ProcessDefinitionVo;
import com.hengtian.activiti.vo.TaskVo;
import com.hengtian.application.model.App;
import com.hengtian.application.service.AppService;
import com.hengtian.common.enums.TaskStatus;
import com.hengtian.common.enums.TaskType;
import com.hengtian.common.enums.TaskVariable;
import com.hengtian.common.result.Result;
import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.BeanUtils;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.common.workflow.cmd.DeleteActiveTaskCmd;
import com.hengtian.common.workflow.cmd.StartActivityCmd;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;


@Service
public class ActivitiServiceImpl implements ActivitiService{

	
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private ManagementService managementService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private TUserTaskService tUserTaskService;
	@Autowired
	private AppService appService;
	@Autowired
	ProcessEngineConfiguration processEngineConfiguration;
	Logger logger = Logger.getLogger(ActivitiServiceImpl.class);
	
	@Override
	public void selectProcessDefinitionDataGrid(PageInfo pageInfo) {
		List<ProcessDefinitionVo> list = new ArrayList<ProcessDefinitionVo>();

		List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>();
		//查询流程定义
		if(pageInfo.getCondition() != null && pageInfo.getCondition().containsKey("key")){
			pdList = repositoryService
					.createProcessDefinitionQuery()
					.orderByProcessDefinitionVersion()
					.asc().latestVersion().processDefinitionKeyLike("%"+pageInfo.getCondition().get("key")+"%")
					.listPage(pageInfo.getFrom(), pageInfo.getSize());
		}else{
			pdList = repositoryService
					.createProcessDefinitionQuery()
					.orderByProcessDefinitionVersion()
					.asc().latestVersion()
					.listPage(pageInfo.getFrom(), pageInfo.getSize());
		}

		//过滤出最新版本
		Map<String, ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();
        if(pdList!=null && pdList.size()>0){
            for(ProcessDefinition pd:pdList){
                map.put(pd.getKey(), pd);
            }
        }
        
        List<ProcessDefinition> mapList = new ArrayList<ProcessDefinition>(map.values());
        if(mapList!=null && mapList.size()>0){
        	for(ProcessDefinition pd : mapList){
    			ProcessDefinitionVo vo = new ProcessDefinitionVo(); 
    			String deploymentId = pd.getDeploymentId();
                Deployment deployment = repositoryService.createDeploymentQuery()
                		.deploymentId(deploymentId).singleResult();
                vo.setDeploymentId(deploymentId);
                vo.setDeployTime(deployment.getDeploymentTime());
                vo.setId(pd.getId());
                vo.setName(pd.getName());
                vo.setKey(pd.getKey());
                vo.setVersion(pd.getVersion());
                vo.setImageName(pd.getDiagramResourceName());
                vo.setResourceName(pd.getResourceName());
				//挂起状态(1.未挂起 2.已挂起)
                vo.setSuspended(pd.isSuspended()==true?"2":"1");
                list.add(vo);
    		}
        }
        Collections.sort(list, new Comparator<ProcessDefinitionVo>() {
			@Override
			public int compare(ProcessDefinitionVo o1, ProcessDefinitionVo o2) {
				return o2.getDeployTime().compareTo(o1.getDeployTime());
			}
		});
        pageInfo.setRows(list);
        //查询流程定义
        long count= repositoryService.createProcessDefinitionQuery().count();
        pageInfo.setTotal(Integer.parseInt(String.valueOf(count)));
	}

	@Override
	public void selectTaskDataGrid(PageInfo pageInfo,boolean isAll,TaskVo taskVo ) {
		List<TaskVo> list = new ArrayList<TaskVo>();
    	
    	TaskQuery taskQuery;
		List<Task> taskList = Lists.newArrayList();
		if(!isAll){
			//我的任务
			//获取Shiro中的用户信息
			ShiroUser shiroUser= (ShiroUser)SecurityUtils.getSubject().getPrincipal();

			NativeTaskQuery nativeTaskQuery = taskService.createNativeTaskQuery();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT DISTINCT RES.* FROM");
			String table = " ACT_RU_TASK RES INNER JOIN ACT_RU_VARIABLE A0 ON RES.ID_ = A0.TASK_ID_ ";
			String where = " WHERE ((RES.ASSIGNEE_ = #{userId}) OR (A0.NAME_ IS NOT NULL AND A0.TYPE_ = 'string' AND A0.TEXT_ = #{taskStatus} AND RES.ASSIGNEE_ LIKE #{userIdLike})) ";
			//业务主键
			if(StringUtils.isNotBlank(taskVo.getBusinessKey())){
				table = table + " INNER JOIN ACT_RU_EXECUTION E ON RES.PROC_INST_ID_ = E.ID_ ";
				where = where + " AND E.BUSINESS_KEY_ LIKE #{businessKey} ";
			}
			//标题
			if(StringUtils.isNotBlank(taskVo.getBusinessName())){
				table = table + " JOIN ACT_RU_VARIABLE A1 ON RES.PROC_INST_ID_ = A1.PROC_INST_ID_ ";
				where = where + " AND A1.TASK_ID_ IS NULL AND A1.NAME_ = 'applyTitle' AND A1.TYPE_ = 'string' AND A1.TEXT_ LIKE #{applyTitle} ";
			}
			//申请人
			if(StringUtils.isNotBlank(taskVo.getProcessOwner())){
				table = table + " INNER JOIN ACT_RU_VARIABLE A2 ON RES.PROC_INST_ID_ = A2.PROC_INST_ID_ ";
				where = where + " AND A2.TASK_ID_ IS NULL AND A2.NAME_ = 'applyUserName' AND A2.TYPE_ = 'string' AND A2.TEXT_ LIKE #{applyUserName} ";
			}
			sb.append(table);
			sb.append(where);
			sb.append(" ORDER BY RES.CREATE_TIME_ DESC ");
			taskList = nativeTaskQuery.sql(sb.toString())
					.parameter("userIdLike","%"+shiroUser.getId()+"%")
					.parameter("userId",shiroUser.getId())
					.parameter("taskStatus",shiroUser.getId() + ":" + TaskStatus.UNFINISHED.value)
					.parameter("businessKey","%"+taskVo.getBusinessKey()+"%")
					.parameter("applyTitle","%"+taskVo.getBusinessName()+"%")
					.parameter("applyUserName",taskVo.getProcessOwner())
					.listPage(pageInfo.getFrom(), pageInfo.getSize());
		}else{
			//全部任务
			taskQuery=taskService.createTaskQuery();
			//业务主键
			if(StringUtils.isNotBlank(taskVo.getBusinessKey())){
				taskQuery.processInstanceBusinessKeyLike("%"+taskVo.getBusinessKey()+"%");
			}
			//标题
			if(StringUtils.isNotBlank(taskVo.getBusinessName())){
				taskQuery.processVariableValueLike("applyTitle","%"+taskVo.getBusinessName()+"%");
			}
			//当前审批人
			if(StringUtils.isNotBlank(taskVo.getTaskAssign())){
				taskQuery.taskAssigneeLike("%"+taskVo.getTaskAssign()+"%");
			}
			//申请人
			if(StringUtils.isNotBlank(taskVo.getProcessOwner())){
				taskQuery.processVariableValueLike("applyUserName","%"+taskVo.getProcessOwner()+"%");
			}
			pageInfo.setTotal(taskQuery.list().size());
			taskList = taskQuery.orderByTaskCreateTime().desc()
					.listPage(pageInfo.getFrom(), pageInfo.getSize());
		}

		for(Task task : taskList){
			TaskVo vo = new TaskVo();
			vo.setId(task.getId());
			vo.setTaskName(task.getName());

			vo.setTaskAssign(task.getAssignee());
			vo.setTaskState(task.getAssignee()==null?"1":"2");
			vo.setTaskCreateTime(task.getCreateTime());
			ProcessInstance processInstance= runtimeService.createProcessInstanceQuery()
			.processInstanceId(task.getProcessInstanceId()).singleResult();
			Map<String,Object> map=runtimeService.getVariables(task.getExecutionId());
			CommonVo commonVo=BeanUtils.toBean(map,CommonVo.class);
			vo.setSuspended(processInstance.isSuspended()==true?"2":"1");
			vo.setProcessDefinitionId(processInstance.getProcessDefinitionId());
			vo.setBusinessName(commonVo.getApplyTitle());
			vo.setProcessOwner(commonVo.getApplyUserName());
			vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
			vo.setProcessInstanceId(task.getProcessInstanceId());
			vo.setBusinessKey(commonVo.getBusinessKey());
			list.add(vo);
		}

		pageInfo.setRows(list);

	}

	@Override
	public void claimTask(String userId, String taskId) {
		identityService.setAuthenticatedUserId(userId);
        taskService.claim(taskId, userId);
	}

	/**
	 * 办理任务
	 * @param taskId
	 * @param userId 任务审核人，管理员待审时不为空
	 * @param commentContent
	 * @param commentResult
	 */
	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
	public Result completeTask(String taskId,String userId,String commentContent,Integer commentResult){
		Result result = new Result();
		ShiroUser shiroUser= (ShiroUser)SecurityUtils.getSubject().getPrincipal();

		if(ConstantUtils.ADMIN_ID.equals(shiroUser.getId())){
			//用户ID为admin是管理员
			if(StringUtils.isBlank(userId)){
				logger.error("办理失败：管理员办理，未选择代办人");
				result.setSuccess(false);
				result.setMsg("办理失败：管理员办理，未选择代办人");
				return result;
			}
			commentContent = commentContent + "【管理员代办】";
		}else{
			userId = shiroUser.getId();
		}
		try{
			Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
			if(task == null){
				result.setSuccess(true);
				result.setMsg("任务不存在或已办理！");
				return result;
			}
			String processInstanceId = task.getProcessInstanceId();
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
			Map map = taskService.getVariables(taskId);
			int version = (int) map.get("version");
			//会签处理
			int userCountNow = 0;

			runtimeService.setVariable(processInstanceId,processInstanceId+":"+TaskVariable.LASTTASKUSER.value,userId);
			if(map != null){
				String taskTypeCurrent = map.get(task.getTaskDefinitionKey()+":"+TaskVariable.TASKTYPE.value) + "";
				if(TaskType.COUNTERSIGN.value.equals(taskTypeCurrent)){
					//会签人

					userCountNow = (int)map.get(task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTNOW.value);
					int userCountTotal = (int)map.get(task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTTOTAL.value);
					int userCountNeed = (int)map.get(task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTNEED.value);
					int userCountRefuse = (int)map.get(task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTREFUSE.value);
					String taskResult = TaskStatus.FINISHEDPASS.value;
					if(ConstantUtils.vacationStatus.NOT_PASSED.getValue().equals(commentResult)){
						taskResult = TaskStatus.FINISHEDREFUSE.value;
						userCountRefuse++;
					}
					taskService.setVariableLocal(taskId,task.getTaskDefinitionKey()+":"+userId,userId+":"+taskResult);
					taskService.setVariableLocal(taskId,task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTNOW.value,++userCountNow);
					taskService.setVariableLocal(taskId,task.getTaskDefinitionKey()+":"+TaskVariable.USERCOUNTREFUSE.value,userCountRefuse);

					int userCountAgree = userCountNow - userCountRefuse;
					if(userCountAgree >= userCountNeed){
						//------------任务完成-通过------------
						commentResult = 2;
					}else{
						if(userCountTotal - userCountNow + userCountAgree < userCountNeed){
							//------------任务完成-未通过------------
							commentResult = 3;
						}else{
							//------------任务继续------------
							//添加意见
							identityService.setAuthenticatedUserId(userId);
							taskService.addComment(task.getId(), processInstance.getId(),String.valueOf(commentResult), commentContent);

							result.setSuccess(true);
							result.setMsg("办理成功！");
							return result;
						}
					}
				}else {
					//候选人
					String taskResult = TaskStatus.FINISHEDPASS.value;
					if(ConstantUtils.vacationStatus.NOT_PASSED.getValue().equals(commentResult)){
						taskResult = TaskStatus.FINISHEDREFUSE.value;
					}
					taskService.setVariableLocal(taskId,task.getTaskDefinitionKey()+":"+userId,userId+":"+taskResult);
				}
			}

			//添加意见
			identityService.setAuthenticatedUserId(shiroUser.getId());
			taskService.addComment(task.getId(), processInstance.getId(),String.valueOf(commentResult), commentContent);
			//完成任务
			Map<String, Object> variables = new HashMap<String, Object>();
			if(ConstantUtils.vacationStatus.PASSED.getValue()==commentResult){
				variables.put("isPass", true);
				//存请假结果的变量
				runtimeService.setVariable(processInstanceId, "vacationResult", "pass");
			}else if(ConstantUtils.vacationStatus.NOT_PASSED.getValue()==commentResult){
				variables.put("isPass", false);
				//存请假结果的变量
				runtimeService.setVariable(processInstanceId, "vacationResult", "notPass");
				runtimeService.deleteProcessInstance(processInstanceId,"refuse");

				result.setSuccess(true);
				result.setMsg("办理成功！");
				return result;
			}

			// 完成委派任务
			if(DelegationState.PENDING == task.getDelegationState()){
				this.taskService.resolveTask(taskId, variables);

				result.setSuccess(true);
				result.setMsg("办理委派任务成功！");
				return result;
			}

			//完成正常办理任务
			taskService.complete(task.getId(), variables);

			List<Task> tasks=taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
			for(Task t:tasks) {
				EntityWrapper<TUserTask> wrapper = new EntityWrapper<>();
				wrapper.where("task_def_key={0}", t.getTaskDefinitionKey()).andNew("proc_def_key={0}", processInstance.getProcessDefinitionKey()).andNew("version_={0}",version);
				TUserTask tUserTask=tUserTaskService.selectOne(wrapper);

				String candidateIds = tUserTask.getCandidateIds();
				if (TaskType.CANDIDATEGROUP.value.equals(tUserTask.getTaskType())) {
					taskService.addCandidateGroup(t.getId(), tUserTask.getCandidateIds());
				} else if (TaskType.CANDIDATEUSER.value.equals(tUserTask.getTaskType())) {
					taskService.setAssignee(t.getId(), tUserTask.getCandidateIds());

					Map<String,Object> variables_ = new HashMap<String,Object>();

					for(String candidateId : candidateIds.split(",")){
						variables_.put(tUserTask.getTaskDefKey()+":"+candidateId,candidateId+":"+TaskStatus.UNFINISHED.value);
					}

					variables_.put(tUserTask.getTaskDefKey()+":"+TaskVariable.TASKTYPE.value,tUserTask.getTaskType());
					variables_.put(tUserTask.getTaskDefKey()+":"+TaskVariable.TASKUSER.value,candidateIds);

					taskService.setVariablesLocal(t.getId(),variables_);
				}else if(TaskType.COUNTERSIGN.value.equals(tUserTask.getTaskType())){
					/**
					 * 为当前任务设置属性值
					 * 把审核人信息放入属性表，多个审核人（会签/候选）多条记录
					 */
					Map<String,Object> variables_ = new HashMap<String,Object>();

					for(String candidateId : candidateIds.split(",")){
						variables_.put(tUserTask.getTaskDefKey()+":"+candidateId,candidateId+":"+TaskStatus.UNFINISHED.value);
					}
					variables_.put(tUserTask.getTaskDefKey()+":"+TaskVariable.USERCOUNTTOTAL.value,tUserTask.getUserCountTotal());
					variables_.put(tUserTask.getTaskDefKey()+":"+TaskVariable.USERCOUNTNEED.value,tUserTask.getUserCountNeed());
					variables_.put(tUserTask.getTaskDefKey()+":"+TaskVariable.USERCOUNTNOW.value,0);
					variables_.put(tUserTask.getTaskDefKey()+":"+TaskVariable.USERCOUNTREFUSE.value,0);
					variables_.put(tUserTask.getTaskDefKey()+":"+TaskVariable.TASKTYPE.value,tUserTask.getTaskType());
					variables_.put(tUserTask.getTaskDefKey()+":"+TaskVariable.TASKUSER.value,candidateIds);

					taskService.setVariablesLocal(t.getId(),variables_);
				}else{
					taskService.setVariableLocal(t.getId(),tUserTask.getTaskDefKey()+":"+candidateIds,candidateIds+":"+TaskStatus.UNFINISHED.value);
					taskService.setAssignee(t.getId(), tUserTask.getCandidateIds());
				}

			}

			result.setSuccess(true);
			result.setMsg("办理成功！");
			return result;
		} catch(Exception e){
			logger.error("办理失败",e);
			result.setSuccess(false);
			result.setMsg("办理失败！");
			return result;
		}
	}

	@Override
	public InputStream getProcessResource(String resourceType, String processDefinitionId) {
        ProcessDefinition pd = repositoryService
        		.createProcessDefinitionQuery()
        		.processDefinitionId(processDefinitionId)
                .singleResult();
        String resourceName = "";
        if (resourceType.equals("image")) {
        	BpmnModel bpmnModel=repositoryService.getBpmnModel(processDefinitionId);
			ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
			InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "PNG", new ArrayList<>(), new ArrayList<>(),
					processEngineConfiguration.getLabelFontName(),
					processEngineConfiguration.getActivityFontName(),
					processEngineConfiguration.getProcessEngineConfiguration().getClassLoader(), 1.0);
			return imageStream;

        } else if (resourceType.equals("xml")) {
            resourceName = pd.getResourceName();
        }
        InputStream in = repositoryService.getResourceAsStream(pd.getDeploymentId(), resourceName);
        return in;
    }

	@Override
	public void delegateTask(String userId, String taskId) {
		taskService.delegateTask(taskId, userId);
	}

	@Override
	public void transferTask(String userId, String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if(task != null){
			String assign = task.getAssignee();
			taskService.setAssignee(taskId, userId);
			if(StringUtils.isNotBlank(assign)) {
				taskService.setOwner(taskId, assign);
			}
		}else{
			throw new ActivitiObjectNotFoundException("任务不存在！", this.getClass());
		}
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
	public void jumpTask(String taskId, String taskDefinitionKey) {
		TaskEntity currentTaskEntity = (TaskEntity) this.taskService.createTaskQuery().taskId(taskId).singleResult();

		if(currentTaskEntity != null){
			ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl)this.repositoryService)
					.getDeployedProcessDefinition(currentTaskEntity.getProcessDefinitionId());
			ActivityImpl activity = (ActivityImpl) pde.findActivity(taskDefinitionKey);

			Command<Void> deleteCmd = new DeleteActiveTaskCmd(currentTaskEntity, "jump", true);
			Command<Void> StartCmd = new StartActivityCmd(currentTaskEntity.getExecutionId(), activity);
			managementService.executeCommand(deleteCmd);
			managementService.executeCommand(StartCmd);
			ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey(pde.getKey()).singleResult();
			//给跳转节点添加审批人
			EntityWrapper<TUserTask> wrapper = new EntityWrapper<TUserTask>();
			wrapper.where("proc_def_key={0}",pde.getKey()).andNew("task_def_key={0}",taskDefinitionKey).andNew("version_={0}",processDefinition.getVersion());
			//wrapper.where("task_def_key",taskDefinitionKey);
			Task task = taskService.createTaskQuery().processInstanceId(currentTaskEntity.getProcessInstanceId()).singleResult();
			TUserTask tUserTask = tUserTaskService.selectOne(wrapper);
			String assign = currentTaskEntity.getAssignee();
			taskService.setAssignee(task.getId(), tUserTask.getCandidateIds());
			taskService.setOwner(task.getId(), assign);
		}else{
			throw new ActivitiObjectNotFoundException("任务不存在！", this.getClass());
		}
	}

	/**
	 * 查询历史任务
	 * @param isAll true：全部历史任务；false：我的历史任务
	 * @return
	 * @author houjinrong@chtwm.com
	 * date 2018/1/26 9:27
	 */
	@Override
	public void selectHisTaskDataGrid(PageInfo pageInfo,boolean isAll,TaskVo taskVo) {

		List<TaskVo> tasks = new ArrayList<TaskVo>();
		if(!isAll){
			//我的任务
			ShiroUser shiroUser= (ShiroUser)SecurityUtils.getSubject().getPrincipal();
			NativeHistoricTaskInstanceQuery nativeTaskQuery = historyService.createNativeHistoricTaskInstanceQuery();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT DISTINCT RES.* FROM");
			String table = " ACT_HI_TASKINST RES INNER JOIN ACT_HI_VARINST A0 ON RES.ID_ = A0.TASK_ID_ ";
			String where = " WHERE ((RES.ASSIGNEE_ = #{userId} AND RES.END_TIME_ IS NOT NULL) OR ((RES.ASSIGNEE_ LIKE #{userIdLike} AND A0.NAME_ IS NOT NULL AND A0.VAR_TYPE_ = 'string' AND A0.TEXT_ LIKE #{taskStatus} AND RES.DELETE_REASON_ IS NULL) OR (RES.DELETE_REASON_ IS NOT NULL AND A0.NAME_ IS NOT NULL AND A0.VAR_TYPE_ = 'string' AND A0.TEXT_ LIKE #{userId_}))) ";
			//业务主键
			if(StringUtils.isNotBlank(taskVo.getBusinessKey())){
				table = table + " INNER JOIN ACT_HI_PROCINST HPI ON RES.PROC_INST_ID_ = HPI.ID_ ";
				where = where + " AND HPI.BUSINESS_KEY_ LIKE #{businessKey} ";
			}
			//标题
			if(StringUtils.isNotBlank(taskVo.getBusinessName())){
				table = table + " JOIN ACT_HI_VARINST A1 ON RES.PROC_INST_ID_ = A1.PROC_INST_ID_ ";
				where = where + " AND A1.TASK_ID_ IS NULL AND A1.NAME_ = 'applyTitle' AND A1.VAR_TYPE_ = 'string' AND A1.TEXT_ LIKE #{applyTitle} ";
			}
			//申请人
			if(StringUtils.isNotBlank(taskVo.getProcessOwner())){
				table = table + " INNER JOIN ACT_HI_VARINST A2 ON RES.PROC_INST_ID_ = A2.PROC_INST_ID_ ";
				where = where + " AND A2.TASK_ID_ IS NULL AND A2.NAME_ = 'applyUserName' AND A2.VAR_TYPE_ = 'string' AND A2.TEXT_ LIKE #{applyUserName} ";
			}
			sb.append(table);
			sb.append(where);
			sb.append(" ORDER BY RES.ID_ DESC ");
			List<HistoricTaskInstance> historicTaskInstances = nativeTaskQuery.sql(sb.toString())
					.parameter("userIdLike", "%" + shiroUser.getId() + "%")
					.parameter("userId", shiroUser.getId())
					.parameter("userId_", "%" + shiroUser.getId() + ":%")
					.parameter("taskStatus", "%"+shiroUser.getId() + ":finished%")
					.parameter("businessKey", "%" + taskVo.getBusinessKey() + "%")
					.parameter("applyTitle", "%" + taskVo.getBusinessName() + "%")
					.parameter("applyUserName", taskVo.getProcessOwner())
					.listPage(pageInfo.getFrom(), pageInfo.getSize());
			pageInfo.setTotal(nativeTaskQuery.list().size());

			for(HistoricTaskInstance his : historicTaskInstances){
				TaskVo vo = new TaskVo();
				vo.setTaskCreateTime(his.getEndTime());
				vo.setTaskName(his.getName());
				List<HistoricVariableInstance> results=historyService.createHistoricVariableInstanceQuery().processInstanceId(his.getProcessInstanceId()).list();
				Map<String,Object> map = Maps.newHashMap();
				Map<String,Date> dateMap = Maps.newHashMap();
				for(HistoricVariableInstance historicVariableInstance:results){
					dateMap.put(historicVariableInstance.getVariableName(),historicVariableInstance.getLastUpdatedTime());
					map.put(historicVariableInstance.getVariableName(),historicVariableInstance.getValue());
				}
				CommonVo commonVo=BeanUtils.toBean(map,CommonVo.class);
				vo.setBusinessName(commonVo.getApplyTitle());
				vo.setProcessOwner(commonVo.getApplyUserName());
				vo.setBusinessKey(commonVo.getBusinessKey());

				if(map.containsKey(his.getTaskDefinitionKey()+":"+shiroUser.getId())){
					String taskStatus = map.get(his.getTaskDefinitionKey()+":"+shiroUser.getId()) + "";
					vo.setTaskCreateTime(dateMap.get(his.getTaskDefinitionKey()+":"+shiroUser.getId()));
					if((shiroUser.getId()+":"+TaskStatus.UNFINISHED.value).equals(taskStatus)){
						vo.setTaskState("未审核");
					}else if((shiroUser.getId()+":"+TaskStatus.FINISHEDPASS.value).equals(taskStatus)){
						vo.setTaskState("通过");
					}else if((shiroUser.getId()+":"+TaskStatus.FINISHEDREFUSE.value).equals(taskStatus)){
						vo.setTaskState("拒绝");
					}
					vo.setTaskAssign(map.get(his.getProcessInstanceId()+":"+TaskVariable.LASTTASKUSER.value)+"");
				}else{
					//兼容旧数据
					HistoricTaskInstanceQuery historicTaskInstanceQuery=historyService.createHistoricTaskInstanceQuery().processInstanceId(his.getProcessInstanceId()).orderByTaskCreateTime().desc();

					if("refuse".equals(his.getDeleteReason())) {
						HistoricTaskInstance historicTaskInstance=historicTaskInstanceQuery.taskDeleteReason("refuse").list().get(0);
						vo.setTaskAssign(historicTaskInstance.getAssignee());
						vo.setTaskState("拒绝");
					}else if("completed".equals(his.getDeleteReason())){
						HistoricTaskInstance historicTaskInstance=historicTaskInstanceQuery.list().get(0);
						vo.setTaskAssign(historicTaskInstance.getAssignee());
						vo.setTaskState("通过");
					}
				}

				tasks.add(vo);
			}
			pageInfo.setRows(tasks);

		}else{
			//全部历史任务
			HistoricProcessInstanceQuery historicProcessInstanceQuery=historyService.createHistoricProcessInstanceQuery().orderByProcessInstanceStartTime().desc().finished();
			if(StringUtils.isNotBlank(taskVo.getBusinessKey())){

				historicProcessInstanceQuery.variableValueLike("businessKey","%"+taskVo.getBusinessKey()+"%");
			}
			if(StringUtils.isNotBlank(taskVo.getBusinessName())){
				historicProcessInstanceQuery.variableValueLike("applyTitle","%"+taskVo.getBusinessName()+"%");
			}

			if(StringUtils.isNotBlank(taskVo.getProcessOwner())){
				historicProcessInstanceQuery.variableValueLike("applyUserName","%"+taskVo.getProcessOwner()+"%");
			}
			historicProcessInstanceQuery.finished();
			pageInfo.setTotal(historicProcessInstanceQuery.list().size());
			List<HistoricProcessInstance> list=historicProcessInstanceQuery.listPage(pageInfo.getFrom(), pageInfo.getSize());
			for(HistoricProcessInstance his : list){

				TaskVo vo = new TaskVo();
				vo.setTaskCreateTime(his.getStartTime());
				vo.setTaskEndTime(his.getEndTime());
				vo.setTaskName(his.getName());
				List<HistoricVariableInstance> results=historyService.createHistoricVariableInstanceQuery().processInstanceId(his.getId()).list();
				Map<String,Object> map=new HashMap<>();
				for(HistoricVariableInstance historicVariableInstance:results){
					map.put(historicVariableInstance.getVariableName(),historicVariableInstance.getValue());
				}
				CommonVo commonVo=BeanUtils.toBean(map,CommonVo.class);
				vo.setBusinessName(commonVo.getApplyTitle());
				vo.setProcessOwner(commonVo.getApplyUserName());
				vo.setProcessDefinitionKey(commonVo.getModelKey());
				EntityWrapper<App> wrapper=new EntityWrapper<>();
				wrapper.where("`key`={0}",commonVo.getBusinessType());
				App app=appService.selectOne(wrapper);
				vo.setAppName(app==null?"":app.getName());
				vo.setBusinessKey(commonVo.getBusinessKey());
				if(map.containsKey(his.getId()+":"+TaskVariable.LASTTASKUSER.value)){
					vo.setTaskAssign(map.get(his.getId()+":"+TaskVariable.LASTTASKUSER.value)+"");
				}else{
					HistoricTaskInstance taskInstance=historyService.createHistoricTaskInstanceQuery().processInstanceId(his.getId()).orderByHistoricTaskInstanceEndTime().desc().finished().list().get(0);
					vo.setTaskAssign(taskInstance.getAssignee());
				}

				if("refuse".equals(his.getDeleteReason())) {
					vo.setTaskState("拒绝");
				}else if(his.getEndTime()!=null){
					vo.setTaskState("通过");
				}
				tasks.add(vo);
			}

			pageInfo.setRows(tasks);

		}
	}

	@Override
	public void sendMailService(Map<String, Object> params) {
		runtimeService.startProcessInstanceByKey(ConstantUtils.MAILKEY, params);
	}

}
