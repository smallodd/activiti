package com.activiti.listener;

import com.activiti.model.TUserTask;
import com.activiti.service.TUserTaskService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * 流程配置监听器
 * @author liu.junyang
 */

public class UserTaskListener implements TaskListener{
	private static final long serialVersionUID = 1L;
	
	@Autowired
    private RepositoryService repositoryService= (RepositoryService) SpringContextUtils.getBeanById("repositoryService");
	@Autowired
	private TUserTaskService tUserTaskService= (TUserTaskService) SpringContextUtils.getBeanById("tUserTaskService");
	
	@Override
	public void notify(DelegateTask delegateTask) {
		//获取流程定义KEY
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionId(delegateTask.getProcessDefinitionId())
				.singleResult();
		String processDefinitionKey = processDefinition.getKey();
		//获取任务KEY
		String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
		try {
			//获取流程对应的任务列表
			EntityWrapper<TUserTask> wrapper = new EntityWrapper<TUserTask>();
			wrapper.where("proc_def_key = {0}", processDefinitionKey).andNew("version_",processDefinition.getVersion());
			List<TUserTask> taskList = tUserTaskService.selectList(wrapper);
			for(TUserTask userTask : taskList){
				String taskKey = userTask.getTaskDefKey();
				String taskType = userTask.getTaskType();
				String ids = userTask.getCandidateIds();
				if(taskDefinitionKey.equals(taskKey)){
					switch (taskType){
						case "assignee" : {
							delegateTask.setAssignee(ids);
							break;
						}
						case "candidateUser" : {
							String[] userIds = ids.split(",");
							List<String> users = new ArrayList<String>();
							for(int i=0; i<userIds.length;i++){
								users.add(userIds[i]);
							}
							delegateTask.addCandidateUsers(users);
							break;
						}
						case "candidateGroup" : {
							String[] groupIds = ids.split(",");
							List<String> groups = new ArrayList<String>();
							for(int i=0; i<groupIds.length;i++){
								groups.add(groupIds[i]);
							}
							delegateTask.addCandidateGroups(groups);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
