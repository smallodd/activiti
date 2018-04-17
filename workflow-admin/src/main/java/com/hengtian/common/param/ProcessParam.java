package com.hengtian.common.param;

import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by ma on 2018/4/17.
 */
public class ProcessParam {
    /**
     * 创建人id
     */
    private String creatorId;

    /**
     * 创建任务的标题
     */
    private String title;
    /**
     * 流程定义key
     */
    private String processDefinitionKey;
    /**
     * 系统定义的key
     */
    private String appKey;
    /**
     * 业务主键，各个业务系统中唯一
     */
    private String bussinessKey;

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getBussinessKey() {
        return bussinessKey;
    }

    public void setBussinessKey(String bussinessKey) {
        this.bussinessKey = bussinessKey;
    }

    /**
     * 校验参数
     * @return
     */
    public Result validate(){
        Result result=new Result();
        result.setCode(Constant.FAIL);
        if(StringUtils.isBlank(getCreatorId())){
            result.setMsg("creatorId创建人id属于必传字段！");
        }else if(StringUtils.isBlank(getTitle())){
            result.setMsg("title标题属于必传字段！");
        }else if(StringUtils.isBlank(getProcessDefinitionKey())){
            result.setMsg("processDefinitionKey流程定义key属于必传字段！");
        }else if(StringUtils.isBlank(getAppKey())){
            result.setMsg("appKey系统定义key属于必传字段！");
        }else if(StringUtils.isBlank(getBussinessKey())){
            result.setMsg("bussinessKey业务主键属于必传字段！");
        }else{
            result.setCode(Constant.SUCCESS);
            result.setSuccess(true);
        }
        return result;
    }
}
