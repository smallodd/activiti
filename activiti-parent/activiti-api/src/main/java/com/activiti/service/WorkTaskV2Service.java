package com.activiti.service;

import com.activiti.entity.CommonVo;
import com.activiti.entity.HistoryTasksVo;
import com.activiti.entity.TaskQueryEntity;
import com.activiti.expection.WorkFlowException;
import com.activiti.model.App;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Model;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * 工作流
 * @return
 * @author houjinrong@chtwm.com
 * date 2018/1/29 15:15
 */
public interface WorkTaskV2Service extends WorkTaskService {
    public boolean checkBusinessKeyIsInFlow(TaskQueryEntity taskQueryEntity,String businessKey) ;
}
