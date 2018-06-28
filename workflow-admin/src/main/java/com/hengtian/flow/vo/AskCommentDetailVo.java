package com.hengtian.flow.vo;

/**
 * 意见征询详情
 *
 * @author chenzhangyan  on 2018/4/28.
 */
public class AskCommentDetailVo {
    /**
     * 流程实例ID
     */
    private String procInstId;
    /**
     * 意见征询所在任务节点key
     */
    private String currentTaskKey;
    /**
     * 意见征询所在任务节点key
     */
    private String askTaskKey;
    /**
     * 意见征询详情
     */
    private String askComment;
    /**
     * 回复详情
     */
    private String answerComment;

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

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

    public String getAskComment() {
        return askComment;
    }

    public void setAskComment(String askComment) {
        this.askComment = askComment;
    }

    public String getAnswerComment() {
        return answerComment;
    }

    public void setAnswerComment(String answerComment) {
        this.answerComment = answerComment;
    }

    @Override
    public String toString() {
        return "AskCommentDetailVo{" +
                "procInstId='" + procInstId + '\'' +
                ", currentTaskKey='" + currentTaskKey + '\'' +
                ", askTaskKey='" + askTaskKey + '\'' +
                ", askComment='" + askComment + '\'' +
                ", answerComment='" + answerComment + '\'' +
                '}';
    }
}
