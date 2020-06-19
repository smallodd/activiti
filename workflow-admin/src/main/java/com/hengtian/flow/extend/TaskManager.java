package com.hengtian.flow.extend;

import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.result.Result;
import com.hengtian.flow.service.WorkflowService;

public interface TaskManager {

    Result taskAction(TaskActionParam taskActionParam, WorkflowService workflowService);
}
