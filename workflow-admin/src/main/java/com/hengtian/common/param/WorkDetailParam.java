package com.hengtian.common.param;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author chenzhangyan  on 2018/4/24.
 */
public class WorkDetailParam {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "H000000")
    private String userId;
    /**
     * 分页-当前页
     */
    @ApiModelProperty(value = "当前页", required = true, example = "1")
    private int pageNum;
    /**
     * 分页-每页条数
     */
    @ApiModelProperty(value = "每页条数", required = true, example = "10")
    private int pageSize;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
