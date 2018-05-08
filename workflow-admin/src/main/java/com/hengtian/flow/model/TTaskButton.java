package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * Created by ma on 2018/5/8.
 */
@TableName("t_task_button")
public class TTaskButton implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(value="button_key")
    private String buttonKey;
    @TableField(value="proc_def_key")
    private String procDefKey;
    @TableField(value="task_def_key")
    private String taskDefKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getButtonKey() {
        return buttonKey;
    }

    public void setButtonKey(String buttonKey) {
        this.buttonKey = buttonKey;
    }

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public void setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
    }
}
