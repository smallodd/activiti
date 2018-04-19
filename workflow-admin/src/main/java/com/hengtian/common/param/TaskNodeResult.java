package com.hengtian.common.param;


import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ma on 2018/4/18.
 */
public class TaskNodeResult implements Serializable {

    private String taskId;

    private String taskDefinedKey;

    private String formKey;

    private String name;
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskDefinedKey() {
        return taskDefinedKey;
    }

    public void setTaskDefinedKey(String taskDefinedKey) {
        this.taskDefinedKey = taskDefinedKey;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
