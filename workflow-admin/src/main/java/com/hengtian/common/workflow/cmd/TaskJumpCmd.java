package com.hengtian.common.workflow.cmd;

import com.hengtian.common.utils.BeanUtils;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;

public class TaskJumpCmd implements Command<ExecutionEntity> {

    private String processInstanceId;
    private String executionId;
    private String activityId;
    public static final String REASION_DELETE = "deleted";

    public TaskJumpCmd(String processInstanceId, String executionId, String activityId) {
        this.processInstanceId = processInstanceId;
        this.executionId = executionId;
        this.activityId = activityId;
    }

    @Override
    public ExecutionEntity execute(CommandContext commandContext) {

        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(executionId);


        if(executionEntity == null){
            executionEntity = new ExecutionEntity();
            ExecutionEntity rootExecution = commandContext.getExecutionEntityManager().findExecutionById(processInstanceId);
            BeanUtils.copy(rootExecution, executionEntity);
            executionEntity.setId(executionId);
            executionEntity.setActive(true);
            executionEntity.setParentId(rootExecution.getId());
            executionEntity.setEnded(false);
            executionEntity.setProcessInstance(rootExecution.getProcessInstance());

            executionEntity.insert();
        }

        executionEntity.destroyScope(REASION_DELETE);
        ProcessDefinitionImpl processDefinition = executionEntity.getProcessDefinition();
        ActivityImpl activity = processDefinition.findActivity(activityId);
        executionEntity.executeActivity(activity);

        return executionEntity;
    }

}