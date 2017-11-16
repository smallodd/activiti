package com.activiti.entity;

import java.io.Serializable;

/**
 * 流程模型
 * @author houjinrong
 */
public class ModelVo implements Serializable{

    private static final long serialVersionUID = 1L;

    private String id;//模型ID
    private String key;//模型KEY
    private String name;//模型名称
    private String deployment;//部署ID

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeployment() {
        return deployment;
    }

    public void setDeployment(String deployment) {
        this.deployment = deployment;
    }
}
