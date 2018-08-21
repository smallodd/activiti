package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

/**
 * 应用流程实例对应关系
 * @author houjinrong@chtwm.com
 * date 2018/4/20 10:47
 */
@Data
@TableName("t_ru_procinst")
public class RuProcinst {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 应用key
     */
    @TableField(value = "app_key")
    private Integer appKey;
    /**
     * 流程实例ID
     */
    @TableField(value = "proc_inst_id")
    private String procInstId;
    /**
     * 流程创建人
     */
    @TableField(value = "creator")
    private String creator;
    /**
     * 流程创建人
     */
    @TableField(value = "creator_name")
    private String creatorName;
    /**
     * 流程创建人部门编号
     */
    @TableField(value = "creator_dept")
    private String creatorDept;
    /**
     * 流程创建人部门名称
     */
    @TableField(value = "creator_dept_name")
    private String creatorDeptName;
    /**
     * 流程定义KEY
     */
    @TableField(value = "proc_def_key")
    private String procDefKey;
    /**
     * 流程名称
     */
    @TableField(value = "proc_def_name")
    private String procDefName;
    /**
     * 流程实例状态：0-未完成；1-完成
     */
    @TableField(value = "proc_inst_state")
    private String procInstState;
    /**
     * 流程实例状态：0-未完成；1-完成
     */
    @TableField(value = "current_task_key")
    private String currentTaskKey;
    /**
     * 流程当前任务状态1:待办；2意见征询中
     */
    @TableField(value = "current_task_status")
    private Integer currentTaskStatus;

    public RuProcinst(){}

    public RuProcinst(Integer appKey, String procInstId, String creator, String creatorName, String creatorDept, String creatorDeptName,String procDefKey, String procDefName, String currentTaskKey) {
        this.appKey = appKey;
        this.procInstId = procInstId;
        this.creator = creator;
        this.creatorName = creatorName;
        this.creatorDeptName = creatorDeptName;
        this.creatorDept = creatorDept;
        this.procDefKey = procDefKey;
        this.procDefName = procDefName;
        this.currentTaskKey = currentTaskKey;
    }
}
