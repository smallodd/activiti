package com.hengtian.common.workflow.cmd;

import org.activiti.engine.impl.db.PersistentObject;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class DestoryExecutionCmd implements Command<Void> {

    private List<ExecutionEntity> executionEntityList;

    public DestoryExecutionCmd(List<ExecutionEntity> executionEntityList) {
        this.executionEntityList = executionEntityList;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if(CollectionUtils.isNotEmpty(executionEntityList)){
            for(PersistentObject persistentObject : executionEntityList){
                commandContext.getExecutionEntityManager().delete(persistentObject);
            }
        }
        return null;
    }

}
