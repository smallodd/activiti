package com.activiti.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class HistoryTasksVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<HistoryTaskVo> taskList; //任务列表
    private Map<String,Object> variables;    //根据传入的属性KEY获取的值

    public List<HistoryTaskVo> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<HistoryTaskVo> taskList) {
        this.taskList = taskList;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
