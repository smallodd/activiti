package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

/**
 * Created by ma on 2018/4/19.
 */
@Data
@TableName("t_ask_task")
public class TAskTask {
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @TableField(value = "proc_inst_id")
    private String procInstId;
    @TableField(value = "current_task_id")
    private String currentTaskId;
    @TableField(value = "current_task_key")
    private String currentTaskKey;
    @TableField(value = "ask_task_key")
    private String askTaskKey;
    @TableField(value = "is_ask_end")
    private Integer isAskEnd;
    @TableField(value = "create_time")
    private Date createTime;
    @TableField(value = "update_time")
    private Date updateTime;
    @TableField(value = "create_id")
    private String createId;
    @TableField(value = "update_id")
    private String updateId;
    @TableField(value = "ask_user_id")
    private String askUserId;
    @TableField(value = "ask_comment")
    private String askComment;
    @TableField(value = "answer_comment")
    private String answerComment;
    @TableField(value = "asked_user_id")
    private String askedUserId;
    @TableField(value = "asked_task_id")
    private String askedTaskId;
}
