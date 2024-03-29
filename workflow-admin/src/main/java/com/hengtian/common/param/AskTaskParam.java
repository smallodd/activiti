package com.hengtian.common.param;

import io.swagger.annotations.ApiModelProperty;

/**
 * 意见征询任务接受参数
 *
 * @author chenzhangyan  on 2018/4/24.
 */
public class AskTaskParam {

    //当前意见征询的任务节点key
    @ApiModelProperty(value = "意见征询所在任务节点key", example = "意见征询所在任务节点key")
    private String currentTaskKey;

    //要意见征询的节点key
    @ApiModelProperty(value = "被意见征询的节点key", example = "H00001")
    private String askTaskKey;

    //意见征询是否结束
    @ApiModelProperty(value = "意见征询是否结束", example = "0")
    private Integer askEnd;

    //意见征询人id
    @ApiModelProperty(value = "被意见征询的人id", example = "H00001")
    private String askUserId;

    //创建人id
    @ApiModelProperty(value = "意见征询人id", example = "H00001")
    private String createId;

    /**
     * 分页-当前页
     */
    @ApiModelProperty(value = "当前页", required = true, example = "1")
    private int pageNum = 1;
    /**
     * 分页-每页条数
     */
    @ApiModelProperty(value = "每页条数", required = true, example = "10")
    private int pageSize = 10;

    public String getCurrentTaskKey() {
        return currentTaskKey;
    }

    public void setCurrentTaskKey(String currentTaskKey) {
        this.currentTaskKey = currentTaskKey;
    }

    public String getAskTaskKey() {
        return askTaskKey;
    }

    public void setAskTaskKey(String askTaskKey) {
        this.askTaskKey = askTaskKey;
    }

    public Integer getAskEnd() {
        return askEnd;
    }

    public void setAskEnd(Integer askEnd) {
        this.askEnd = askEnd;
    }

    public String getAskUserId() {
        return askUserId;
    }

    public void setAskUserId(String askUserId) {
        this.askUserId = askUserId;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
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

    @Override
    public String toString() {
        return "AskTaskParam{" +
                "currentTaskKey='" + currentTaskKey + '\'' +
                ", askTaskKey='" + askTaskKey + '\'' +
                ", askEnd=" + askEnd +
                ", askUserId='" + askUserId + '\'' +
                ", createId='" + createId + '\'' +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}
