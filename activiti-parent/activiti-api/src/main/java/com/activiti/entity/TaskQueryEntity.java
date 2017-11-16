package com.activiti.entity;

import java.io.Serializable;

/**
 * Created by ma on 2017/11/16.
 */
public class TaskQueryEntity implements Serializable {
    /**
     * 业务系统类型
     */
    private  String bussinessType;
    /**
     * 模型的key
     */
    private  String modelKey;

    public  void setBussinessType(String bussinessType){

        this.bussinessType=bussinessType;

    }
    public  void setModelKey(String modelKey){

        this.modelKey=modelKey;

    }

    public  String getBussinessType() {
        return bussinessType;
    }

    public  String getModelKey() {
        return modelKey;
    }

    @Override
    public String toString() {
        return "TaskQueryEntity{bussinessType："+getBussinessType()+",modelKey:"+getModelKey()+"}";
    }




}
