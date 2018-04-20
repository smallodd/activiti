package com.hengtian.common.param;

import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by ma on 2018/4/17.
 */

public class ProcessParam {
    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id", required = true, example="H017830")
    private String creatorId;

    /**
     * 创建任务的标题
     */
    @ApiModelProperty(value = "创建任务的标题", required = true, example="测试任务标题")
    private String title;
    /**
     * 流程定义key
     */
    @ApiModelProperty(value = "流程定义key", required = true, example="流程定义key")
    private String processDefinitionKey;
    /**
     * 系统定义的key
     */
    @ApiModelProperty(value = "系统定义的key", required = true, example="系统定义的key")
    private String appKey;
    /**
     * 业务主键，各个业务系统中唯一
     */
    @ApiModelProperty(value = "业务主键，各个业务系统中唯一", required = true, example="业务主键")
    private String bussinessKey;
    /**
     * 自定义审批人
     */
    @ApiModelProperty(value = "自定义审批人", required = true, example="false")
    private boolean customApprover=false;
    /**
     * 自定义参数
     */
    @ApiModelProperty(value = "自定义参数", required = true, example="{'code':'string'}")
    private String  jsonVariables;

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

    public boolean isCustomApprover() {
        return customApprover;
    }

    public void setCustomApprover(boolean customApprover) {
        this.customApprover = customApprover;
    }

    public String getJsonVariables() {
        return jsonVariables;
    }

    public void setJsonVariables(String jsonVariables) {
        this.jsonVariables = jsonVariables;
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
        }else if(getAppKey()==null){
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
