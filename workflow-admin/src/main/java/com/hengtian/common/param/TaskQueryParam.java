package com.hengtian.common.param;

import io.swagger.annotations.ApiModelProperty;


/**
 * 查询任务-入参
 * @author houjinrong@chtwm.com
 * date 2018/4/19 15:10
 */
public class TaskQueryParam {
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example="H000000")
    private String userId;

    /**
     * 创建任务的标题
     */
    @ApiModelProperty(value = "创建任务的标题",  example="测试任务标题")
    private String title;
    /**
     * 流程定义key
     */
    @ApiModelProperty(value = "流程定义key", example="流程定义key")
    private String processDefinitionKey;
    /**
     * 系统定义的key
     */
    @ApiModelProperty(value = "系统定义的key", required = true, example="系统定义的key")
    private String appKey;
    /**
     * 业务主键，各个业务系统中唯一
     */
    @ApiModelProperty(value = "业务主键，各个业务系统中唯一", example="业务主键")
    private String bussinessKey;
    /**
     * 分页-当前页
     */
    @ApiModelProperty(value = "当前页", required = true, example="1")
    private int pageNum;
    /**
     * 分页-每页条数
     */
    @ApiModelProperty(value = "每页条数", required = true, example="10")
    private int pageSize;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
