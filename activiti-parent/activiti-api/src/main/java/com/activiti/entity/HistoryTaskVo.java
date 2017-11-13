package com.activiti.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 工作流历史任务
 * @author houjinrong
 *
 */
public class HistoryTaskVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;   //任务ID
    private String operator; //任务操作人
    private List<String> comment;  //审核意见
    private int isLastApprove;//是否最终审批
    private Date startTime;//开始时间
    private Date endTime;  //完成时间

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<String> getComment() {
        return comment;
    }

    public void setComment(List<String> comment) {
        this.comment = comment;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getIsLastApprove() {
        return isLastApprove;
    }

    public void setIsLastApprove(int isLastApprove) {
        this.isLastApprove = isLastApprove;
    }
}
