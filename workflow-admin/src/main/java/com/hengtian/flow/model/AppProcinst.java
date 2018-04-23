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
@TableName("t_app_procinst")
public class AppProcinst {

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
    private String procinstId;

    public AppProcinst(){

    }

    public AppProcinst(Integer appKey, String procinstId) {
        this.appKey = appKey;
        this.procinstId = procinstId;
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

    public String getProcinstId() {
        return procinstId;
    }

    public void setProcinstId(String procinstId) {
        this.procinstId = procinstId;
    }
}
