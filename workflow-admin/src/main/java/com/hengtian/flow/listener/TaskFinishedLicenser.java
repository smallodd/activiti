package com.hengtian.flow.listener;

import com.hengtian.common.utils.SpringBeanUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("taskFinishedLicenser")
@Transactional(propagation = Propagation.NOT_SUPPORTED,rollbackFor = Exception.class)
public class TaskFinishedLicenser implements ExecutionListener {

    private static final long serialVersionUID = 2105979050046650949L;

    @Autowired
    private RepositoryService repositoryService = SpringBeanUtil.getBean(RepositoryService.class);

    @Autowired
    private RuntimeService runtimeService = SpringBeanUtil.getBean(RuntimeService.class);

    @Autowired
    ProcessEngineFactoryBean processEngine;

    @Override
    public void notify(DelegateExecution execution){
        try{
            //set global flow varible
            ProcessDefinitionEntity pde = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(execution.getProcessDefinitionId());
            //执行实例
            ExecutionEntity entity = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();
            //当前实例的执行到哪个节点
            String activitiId = entity.getActivityId();
            //获得当前任务的所有节点
            List<ActivityImpl> activitiList = pde.getActivities();
            String id = null;

            for(ActivityImpl activityImpl:activitiList){
                id = activityImpl.getId();
                if(activitiId.equals(id)){
                    //new WorkflowBaseController().nextTaskDefinition(activityImpl, activityImpl.getId());
                }
            }
            execution.getEngineServices().getRuntimeService().setVariableLocal(execution.getProcessInstanceId(),"input",1);
        }catch(Exception e){
            execution.getEngineServices().getRuntimeService().setVariableLocal(execution.getProcessInstanceId(),"input",2);
        }
    }

    public void  main(String[] args) {

    }
}