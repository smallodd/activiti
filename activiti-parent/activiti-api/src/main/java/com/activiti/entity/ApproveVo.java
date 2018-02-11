package com.activiti.entity;

import java.io.Serializable;

/**
 * Created by ma on 2018/1/30.
 * 此方法用于审批时参数传递的类；
 * 审批时，此类中的所有属性必须设置，否则会抛出参数非法
 * 注意：尤其注意一点是动态审批时isDynamic一定设置成true
 */
public class ApproveVo extends ApproveInterface implements Serializable {

    public ApproveVo(String processInstanceId,String commentContent,String commentResult,String currentUser,boolean dynamic){
        setCommentContent(commentContent);
        setCommentResult(commentResult);
        setProcessInstanceId(processInstanceId);
        setCurrentUser(currentUser);
        setDynamic(dynamic);
    }
    public ApproveVo(String processInstanceId,String commentContent,String commentResult,String currentUser){
        setCommentContent(commentContent);
        setCommentResult(commentResult);
        setProcessInstanceId(processInstanceId);
        setCurrentUser(currentUser);

    }
    public ApproveVo(){};

    /**
     * 流程实例的id
     */
    private  String processInstanceId;
    /**
     * 当前审批人信息（工号）
     */
    private String currentUser;
    /**
     * 审批意见
     */
    private String commentContent;
    /**
     * 审批类型
     *         2  审批通过
     *         3 审批拒绝
     */
    private String commentResult;

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentResult() {
        return commentResult;
    }

    public void setCommentResult(String commentResult) {
        this.commentResult = commentResult;
    }
}
