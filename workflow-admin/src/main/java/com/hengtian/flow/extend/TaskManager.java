package com.hengtian.flow.extend;

import com.hengtian.common.param.TaskActionParam;
import com.hengtian.common.result.Result;

public interface TaskManager {

    Result taskAction(TaskActionParam taskActionParam);
}
