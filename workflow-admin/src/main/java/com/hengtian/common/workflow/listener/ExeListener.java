package com.hengtian.common.workflow.listener;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.TUserTaskService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 执行监听器
 * @author liujunyang
 */
@Component("exeListener")
public class ExeListener implements ExecutionListener,Serializable{
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private TUserTaskService tUserTaskService;
	@Autowired
    private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;


	@Override
	public void notify(DelegateExecution execution) throws Exception {
		//获取流程定义KEY
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionId(execution.getProcessDefinitionId())
				.singleResult();
		String processDefinitionKey = processDefinition.getKey();
		if("start".equals(execution.getEventName())){
			//如果是会签业务
			if("CounterSign".equals(processDefinitionKey)){
				//获取流程对应的任务列表
				EntityWrapper<TUserTask> wrapper = new EntityWrapper<TUserTask>();
				wrapper.where("proc_def_key = {0}", processDefinitionKey);
				List<TUserTask> taskList = tUserTaskService.selectList(wrapper);
				for(TUserTask userTask : taskList){
					String taskKey = userTask.getTaskDefKey();
					String taskType = userTask.getTaskType();
					String ids = userTask.getCandidateIds();
					if("CounterSignTask".equals(taskKey)){
						switch (taskType){
							case "counterSign" : {
								String[] userIds = ids.split(",");
								List<String> users = new ArrayList<String>();
								for(int i=0; i<userIds.length;i++){
									users.add(userIds[i]);
								}
								execution.setVariable("signUsers", users);
								break;
							}
						}
					}
				}
			}
		}else if("end".equals(execution.getEventName())){


		}
		
	}

}
