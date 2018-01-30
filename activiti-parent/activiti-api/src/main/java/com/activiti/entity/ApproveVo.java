package com.activiti.entity;

import java.io.Serializable;

/**
 * Created by ma on 2018/1/30.
 */
public class ApproveVo implements Serializable {
    /**
     * 是否是动态传审批人
     */
    private boolean isDynamic=false;
    /**
     * 是否是上级审批
     */
    private boolean isSuperior=false;

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
