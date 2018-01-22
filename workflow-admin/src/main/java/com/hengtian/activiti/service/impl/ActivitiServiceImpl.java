package com.hengtian.activiti.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.hengtian.activiti.model.TUserTask;
import com.hengtian.activiti.service.ActivitiService;
import com.hengtian.activiti.service.TUserTaskService;
import com.hengtian.activiti.vo.CommonVo;
import com.hengtian.activiti.vo.ProcessDefinitionVo;
import com.hengtian.activiti.vo.TaskVo;
import com.hengtian.application.model.App;
import com.hengtian.application.service.AppService;
import com.hengtian.common.result.Result;
import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.*;
import com.hengtian.common.workflow.cmd.DeleteActiveTaskCmd;
import com.hengtian.common.workflow.cmd.StartActivityCmd;
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
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
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
                vo.setSuspended(pd.isSuspended()==true?"2":"1");//挂起状态(1.未挂起 2.已挂起)
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
		//获取Shiro中的用户信息
    	
    	TaskQuery taskQuery;
    	if(!isAll){
			ShiroUser shiroUser= (ShiroUser)SecurityUtils.getSubject().getPrincipal();
			taskQuery=taskService.createTaskQuery().taskAssigneeLike("%"+shiroUser.getId()+"%");
			taskQuery.taskVariableValueEquals("0:unfinished");
			//taskQuery.processVariableValueEquals("0:unfinished");
		}else{
			taskQuery=taskService.createTaskQuery();
		}
		if(StringUtils.isNotBlank(taskVo.getBusinessKey())){
    		taskQuery.processInstanceBusinessKeyLike("%"+taskVo.getBusinessKey()+"%");
		}
		if(StringUtils.isNotBlank(taskVo.getBusinessName())){
			taskQuery.processVariableValueLike("applyTitle","%"+taskVo.getBusinessName()+"%");
		}
		if(isAll&&StringUtils.isNotBlank(taskVo.getTaskAssign())){
			taskQuery.taskAssigneeLike("%"+taskVo.getTaskAssign()+"%");
		}
		if(StringUtils.isNotBlank(taskVo.getProcessOwner())){
			taskQuery.processVariableValueLike("applyUserName","%"+taskVo.getProcessOwner()+"%");
		}
		pageInfo.setTotal(taskQuery.list().size());
		List<Task> taskList = taskQuery.orderByTaskCreateTime().desc()
				.listPage(pageInfo.getFrom(), pageInfo.getSize());
		
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
//			CommonVo commonVo= (CommonVo)runtimeService.getVariable(task.getExecutionId(), ConstantUtils.MODEL_KEY);
			vo.setSuspended(processInstance.isSuspended()==true?"2":"1");
			vo.setProcessDefinitionId(processInstance.getProcessDefinitionId());
			vo.setBusinessName(commonVo.getApplyTitle());
			vo.setProcessOwner(commonVo.getApplyUserName());
			vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
			vo.setProcessInstanceId(task.getProcessInstanceId());
			vo.setBusinessKey(commonVo.getBusinessKey());
			//vo.setProcessDefinitionKey(processDefinitionKey);
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
	 * @param commentContent
	 * @param commentResult
	 */
	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor = Exception.class)
	public Result complateTask(String taskId,String commentContent,Integer commentResult){
		Result result = new Result();
		try{
			ShiroUser shiroUser= (ShiroUser)SecurityUtils.getSubject().getPrincipal();
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
			int userCountCurrent = 0;
			if(map != null){
				if("counterSign".equals(map.get(task.getTaskDefinitionKey()+":"+"taskType"))){
					//会签人
					EntityWrapper<TUserTask> wrapper_ =new EntityWrapper<TUserTask>();
					wrapper_.where("proc_def_key = {0}", processInstance.getProcessDefinitionKey()).andNew("version_={0}",version).andNew("task_def_key={0}",task.getTaskDefinitionKey());
					TUserTask tUserTask = tUserTaskService.selectOne(wrapper_);
					String[] tUserTaskArray = tUserTask.getCandidateIds().split(",");
					//int userCountTotal = (int)map.get(task.getTaskDefinitionKey()+":"+"userCountTotal");
					int userCountNeed = (int)map.get(task.getTaskDefinitionKey()+":"+"userCountNeed");
					for(String candidateId: tUserTaskArray){
						if("1:finished".equals((String)map.get(task.getTaskDefinitionKey()+":"+candidateId))){
							userCountCurrent++;
						}
					}
					taskService.setVariable(taskId,task.getTaskDefinitionKey()+":"+shiroUser.getId(),"1:finished");
					if(userCountCurrent + 1 <= userCountNeed){
						result.setSuccess(true);
						result.setMsg("办理成功！");
						return result;
					}
				}else if("candidateUser".equals(map.get(task.getTaskDefinitionKey()+":"+"taskType"))){
					//候选人
					taskService.setVariable(taskId,task.getTaskDefinitionKey()+":"+shiroUser.getId(),"1:finished");
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
			for(Task task1:tasks) {
				EntityWrapper<TUserTask> wrapper = new EntityWrapper<>();
				wrapper.where("task_def_key={0}", task1.getTaskDefinitionKey()).andNew("proc_def_key={0}", map.get("proDefinedKey").toString()).andNew("version_={0}",version);
				TUserTask tUser=tUserTaskService.selectOne(wrapper);
				if ("candidateGroup".equals(tUser.getTaskType())) {
					taskService.addCandidateGroup(task1.getId(), tUser.getCandidateIds());
				} else if ("candidateUser".equals(tUser.getTaskType())) {
					taskService.addCandidateUser(task1.getId(), tUser.getCandidateIds());
				} else {
					if("counterSign".equals(tUser.getTaskType())){

						taskService.setVariable(task1.getId(),"counterSign",tUser.getCandidateIds());
					}
					taskService.setAssignee(task1.getId(), tUser.getCandidateIds());
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
            resourceName = pd.getDiagramResourceName();
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
			taskService.setOwner(taskId, assign);
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

	@Override
	public void selectHisTaskDataGrid(PageInfo pageInfo,boolean flag,TaskVo taskVo) {

		List<TaskVo> tasks = new ArrayList<TaskVo>();
		if(!flag){
			HistoricTaskInstanceQuery taskQuery= historyService.createHistoricTaskInstanceQuery();
			ShiroUser shiroUser= (ShiroUser)SecurityUtils.getSubject().getPrincipal();
			taskQuery.taskAssignee(shiroUser.getId());
			if(StringUtils.isNotBlank(taskVo.getBusinessKey())){
				taskQuery.processInstanceBusinessKeyLike("%"+taskVo.getBusinessKey()+"%");
			}
			if(StringUtils.isNotBlank(taskVo.getBusinessName())){
				taskQuery.processVariableValueLike("applyTitle","%"+taskVo.getBusinessName()+"%");
			}
			if(StringUtils.isNotBlank(taskVo.getProcessOwner())){
				taskQuery.processVariableValueLike("applyUserName","%"+taskVo.getProcessOwner()+"%");
			}
			pageInfo.setTotal(taskQuery.list().size());
			List<HistoricTaskInstance> list= taskQuery
					.orderByTaskCreateTime().desc()
					.listPage(pageInfo.getFrom(), pageInfo.getSize());
			for(HistoricTaskInstance his : list){
				if(his.getEndTime() == null){
					continue;
				}
				TaskVo vo = new TaskVo();
				vo.setTaskCreateTime(his.getEndTime());
				vo.setTaskName(his.getName());
				List<HistoricVariableInstance> results=historyService.createHistoricVariableInstanceQuery().processInstanceId(his.getProcessInstanceId()).list();
				Map<String,Object> map=new HashMap<>();
				for(HistoricVariableInstance historicVariableInstance:results){
					map.put(historicVariableInstance.getVariableName(),historicVariableInstance.getValue());
				}
				CommonVo commonVo=BeanUtils.toBean(map,CommonVo.class);
				vo.setBusinessName(commonVo.getApplyTitle());
				vo.setProcessOwner(commonVo.getApplyUserName());
				vo.setBusinessKey(commonVo.getBusinessKey());
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
				tasks.add(vo);
			}
			pageInfo.setRows(tasks);

		}else{
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
			pageInfo.setTotal(historicProcessInstanceQuery.list().size());
			List<HistoricProcessInstance> list=historicProcessInstanceQuery.listPage(pageInfo.getFrom(), pageInfo.getSize());
			for(HistoricProcessInstance his : list){
				if(his.getEndTime() == null){
					continue;
				}
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
				HistoricTaskInstance taskInstance=historyService.createHistoricTaskInstanceQuery().processInstanceId(his.getId()).orderByHistoricTaskInstanceEndTime().desc().finished().list().get(0);
				vo.setTaskAssign(taskInstance.getAssignee());
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
