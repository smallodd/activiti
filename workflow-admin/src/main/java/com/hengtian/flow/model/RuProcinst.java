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
     * 流程创建人部门
     */
    @TableField(value = "creator_dept")
    private String creatorDept;
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

    public RuProcinst(){}

    public RuProcinst(Integer appKey, String procInstId, String creator, String creatorDept, String procDefName) {
        this.appKey = appKey;
        this.procInstId = procInstId;
        this.creator = creator;
        this.creatorDept = creatorDept;
        this.procDefName = procDefName;
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

}
