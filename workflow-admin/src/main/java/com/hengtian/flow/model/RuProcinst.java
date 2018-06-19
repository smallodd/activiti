package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 应用流程实例对应关系
 * @author houjinrong@chtwm.com
 * date 2018/4/20 10:47
 */
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
     * 流程当前任务状态1:待办；2问询中
     */
    @TableField(value = "current_task_status")
    private Integer currentTaskStatus;

    public RuProcinst(){}

    public RuProcinst(Integer appKey, String procInstId, String creator, String creatorName, String creatorDept, String creatorDeptName, String procDefName, String currentTaskKey) {
        this.appKey = appKey;
        this.procInstId = procInstId;
        this.creator = creator;
        this.creatorName = creatorName;
        this.creatorDeptName = creatorDeptName;
        this.creatorDept = creatorDept;
        this.procDefName = procDefName;
        this.currentTaskKey = currentTaskKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAppKey() {
        return appKey;
    }

    public void setAppKey(Integer appKey) {
        this.appKey = appKey;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorDept() {
        return creatorDept;
    }

    public void setCreatorDept(String creatorDept) {
        this.creatorDept = creatorDept;
    }

    public String getProcDefName() {
        return procDefName;
    }

    public void setProcDefName(String procDefName) {
        this.procDefName = procDefName;
    }

    public String getProcInstState() {
        return procInstState;
    }

    public void setProcInstState(String procInstState) {
        this.procInstState = procInstState;
    }

    public String getCurrentTaskKey() {
        return currentTaskKey;
    }

    public void setCurrentTaskKey(String currentTaskKey) {
        this.currentTaskKey = currentTaskKey;
    }

    public String getCreatorDeptName() {
        return creatorDeptName;
    }

    public void setCreatorDeptName(String creatorDeptName) {
        this.creatorDeptName = creatorDeptName;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Integer getCurrentTaskStatus() {
        return currentTaskStatus;
    }

    public void setCurrentTaskStatus(Integer currentTaskStatus) {
        this.currentTaskStatus = currentTaskStatus;
    }
}
