package com.hengtian.activiti.service.impl;

import java.io.InputStream;
import java.util.*;

import com.hengtian.common.utils.BeanUtils;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hengtian.activiti.service.ActivitiService;
import com.hengtian.activiti.vo.CommonVo;
import com.hengtian.activiti.vo.ProcessDefinitionVo;
import com.hengtian.activiti.vo.TaskVo;
import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.workflow.cmd.DeleteActiveTaskCmd;
import com.hengtian.common.workflow.cmd.StartActivityCmd;


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
	
	
	@Override
	public void selectProcessDefinitionDataGrid(PageInfo pageInfo) {
		List<ProcessDefinitionVo> list = new ArrayList<ProcessDefinitionVo>();
		//查询流程定义
		List<ProcessDefinition> pdList = repositoryService
				.createProcessDefinitionQuery()
				.orderByProcessDefinitionVersion()
				.asc()
				.listPage(pageInfo.getFrom(), pageInfo.getSize());
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
        pageInfo.setRows(list);
        //查询流程定义
        long count= repositoryService.createProcessDefinitionQuery().count();
        pageInfo.setTotal(Integer.parseInt(String.valueOf(count)));
	}

	@Override
	public void selectTaskDataGrid(PageInfo pageInfo,boolean isAll) {
		List<TaskVo> list = new ArrayList<TaskVo>();
		//获取Shiro中的用户信息
    	ShiroUser shiroUser= (ShiroUser)SecurityUtils.getSubject().getPrincipal();
    	
    	TaskQuery taskQuery;
    	if(!isAll){
			taskQuery=taskService.createTaskQuery().taskAssigneeLike("%"+shiroUser.getId()+"%");
		}else{
			taskQuery=taskService.createTaskQuery();
		}
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
			//vo.setProcessDefinitionKey(processDefinitionKey);
			list.add(vo);
		}
		pageInfo.setRows(list);
		pageInfo.setTotal(taskQuery.list().size());
	}

	@Override
	public void claimTask(String userId, String taskId) {
		identityService.setAuthenticatedUserId(userId);
        taskService.claim(taskId, userId);
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
	public void jumpTask(String taskId, String taskDefinitionKey) {
		TaskEntity currentTaskEntity = (TaskEntity) this.taskService.createTaskQuery().taskId(taskId).singleResult();
		ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl)this.repositoryService)
				.getDeployedProcessDefinition(currentTaskEntity.getProcessDefinitionId()); 
		ActivityImpl activity = (ActivityImpl) pde.findActivity(taskDefinitionKey);

		Command<Void> deleteCmd = new DeleteActiveTaskCmd(currentTaskEntity, "jump", true);
		Command<Void> StartCmd = new StartActivityCmd(currentTaskEntity.getExecutionId(), activity);
		managementService.executeCommand(deleteCmd);
		managementService.executeCommand(StartCmd);
	}

	@Override
	public void selectHisTaskDataGrid(PageInfo pageInfo) {
		ShiroUser shiroUser= (ShiroUser)SecurityUtils.getSubject().getPrincipal();
		HistoricTaskInstanceQuery taskQuery= historyService.createHistoricTaskInstanceQuery();
		List<HistoricTaskInstance> list= taskQuery.taskAssignee(shiroUser.getId())
										.orderByTaskCreateTime().desc()
										.listPage(pageInfo.getFrom(), pageInfo.getSize());
		List<TaskVo> tasks = new ArrayList<TaskVo>();
		for(HistoricTaskInstance his : list){
			TaskVo vo = new TaskVo();
			vo.setTaskCreateTime(his.getCreateTime());
			vo.setTaskName(his.getName());
			List<HistoricVariableInstance> results=historyService.createHistoricVariableInstanceQuery().processInstanceId(his.getProcessInstanceId()).list();
			Map<String,Object> map=new HashMap<>();
			for(HistoricVariableInstance historicVariableInstance:results){
				map.put(historicVariableInstance.getVariableName(),historicVariableInstance.getValue());
			}
			CommonVo commonVo=BeanUtils.toBean(map,CommonVo.class);
			vo.setBusinessName(commonVo.getApplyTitle());
			vo.setProcessOwner(commonVo.getApplyUserName());
			tasks.add(vo);
		}
		pageInfo.setRows(tasks);
		pageInfo.setTotal(taskQuery.list().size());
	}

	@Override
	public void sendMailService(Map<String, Object> params) {
		runtimeService.startProcessInstanceByKey(ConstantUtils.MAILKEY, params);
	}

}
