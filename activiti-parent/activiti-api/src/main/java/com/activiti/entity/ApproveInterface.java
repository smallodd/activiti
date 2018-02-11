package com.activiti.entity;

import java.io.Serializable;

/**
 * Created by ma on 2018/2/1.
 * 用于提供动态审批和上级审批的参数封装；
 * 动态审批一定设置isDynamic为true;
 * 默认不是动态设置审批人
 */
public abstract class ApproveInterface implements Serializable {
    /**
     * 是否是动态传审批人
     */
    private boolean isDynamic = false;
    /**
     * 是否是上级审批
     */
    private boolean isSuperior = false;

    public boolean isDynamic() {
        return isDynamic;
    }

    public void setDynamic(boolean dynamic) {
        isDynamic = dynamic;
    }

    public boolean isSuperior() {
        return isSuperior;
    }

    public void setSuperior(boolean superior) {
        isSuperior = superior;
    }
}
