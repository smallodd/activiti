package com.activiti.entity;

import java.io.Serializable;

/**
 * 工作流中的审批意见VO
 * @author Administrator
 */
public class CommentVo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 评论人
	 */
	private String commentUser;
	
	/**
	 * 评论结果
	 */
	private String commentResult;
	
	/**
	 * 评论内容
	 */
	private String commentContent;
	
	/**
	 * 评论任务节点
	 */
	private String commentTask;
	
	/**
	 * 评论时间
	 */
	private String commentTime;

	/**
	 *
	 * @return  审批人
	 */
	public String getCommentUser() {
		return commentUser;
	}

	public void setCommentUser(String commentUser) {
		this.commentUser = commentUser;
	}

	/**
	 *
	 * @return 审批意见
	 */
	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	/**
	 *
	 * @return  审批时间
	 */
	public String getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}

	/**
	 *
	 * @return  审批结果
	 *              2：通过
	 *              3：拒绝
	 */
	public String getCommentResult() {
		return commentResult;
	}

	public void setCommentResult(String commentResult) {
		this.commentResult = commentResult;
	}

	/**
	 *
	 * @return  任务节点
	 */
	public String getCommentTask() {
		return commentTask;
	}

	public void setCommentTask(String commentTask) {
		this.commentTask = commentTask;
	}
}
