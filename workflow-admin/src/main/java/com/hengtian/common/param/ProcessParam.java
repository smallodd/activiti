package com.hengtian.common.param;

import com.hengtian.common.result.Constant;
import com.hengtian.common.result.Result;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by ma on 2018/4/17.
 */

public class ProcessParam {
    /**
     * 创建人id
     */
    @ApiModelProperty(value = "创建人id", required = true, example="H017830")
    @NotBlank(message = "创建人不能为空")
    private String creatorId;

    /**
     * 创建任务的标题
     */
    @ApiModelProperty(value = "创建任务的标题", required = true, example="测试任务标题")
    @NotBlank(message = "任务标题不能为空")
    private String title;
    /**
     * 流程定义key
     */
    @ApiModelProperty(value = "流程定义key", required = true, example="流程定义key")
    @NotBlank(message = "流程定义key不能为空")
    private String processDefinitionKey;
    /**
     * 系统定义的key
     */
    @ApiModelProperty(value = "系统定义的key", required = true, example="系统定义的key")
    @NotNull(message = "系统定义key不能为空")
    private Integer appKey;
    /**
     * 业务主键，各个业务系统中唯一
     */
    @ApiModelProperty(value = "业务主键，各个业务系统中唯一", required = true, example="业务主键")
    @NotNull(message = "业务主键不能为空")
    private String businessKey;
    /**
     * 自定义审批人
     */
    @ApiModelProperty(value = "自定义审批人", required = true, example="false")
    private boolean customApprover=false;
    /**
     * 自定义参数
     */
    @ApiModelProperty(value = "自定义参数", example="{'code':'string'}")
    private String  jsonVariables;

    private String  deptCode;

    private String deptName;

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

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

    public Integer getAppKey() {
        return appKey;
    }

    public void setAppKey(Integer appKey) {
        this.appKey = appKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
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
        }else if(StringUtils.isBlank(getBusinessKey())){
            result.setMsg("businessKey业务主键属于必传字段！");
        }else{
            result.setCode(Constant.SUCCESS);
            result.setSuccess(true);
        }
        return result;
    }
}
