package com.hengtian.common.param;

import io.swagger.annotations.ApiModelProperty;

/**
 * 查询催办任务-入参
 * @author houjinrong@chtwm.com
 * date 2018/4/19 15:10
 */
public class TaskRemindQueryParam {
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
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称", example="任务名称")
    private String taskName;
    /**
     * 系统定义的key
     */
    @ApiModelProperty(value = "系统定义的key", example="系统定义的key")
    private String appKey;

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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
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
