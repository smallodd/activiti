package com.hengtian.flow.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.hengtian.common.enums.AssignTypeEnum;

import java.io.Serializable;

/**
 * 用户任务表
 * @author houjinrong@chtwm.com
 * date 2018/6/22 16:36
 */
@TableName("t_user_task")
public class TUserTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value="id",type = IdType.UUID)
    private String id;
    /**
     * 流程定义KEY
     */
    @TableField(value="proc_def_key")
    private String procDefKey;
    /**
     * 流程定义名称
     */
    @TableField(value="proc_def_name")
    private String procDefName;
    /**
     * 任务定义KEY
     */
    @TableField(value="task_def_key")
    private String taskDefKey;
    /**
     * 任务名称
     */
    @TableField(value="task_name")
    private String taskName;
    /**
     * 任务类型
     */
    @TableField(value="task_type")
    private String taskType;
    /**
     * 审批人名称(多个)
     */
    @TableField(value="candidate_name")
    private String candidateName;
    /**
     * 审批人ID(多个)
     */
    @TableField(value="candidate_ids")
    private String candidateIds;
    /**
     * 处理人顺序
     */
    @TableField(value="order_num")
    private int orderNum;
    /**
     * 版本号
     */
    @TableField(value = "version_")
    private int version;
    /**
     * 有权限审批的人数
     */
    @TableField(value = "user_count_total")
    private int userCountTotal;
    /**
     * 审批通过需要的人数
     */
    @TableField(value = "user_count_need")
    private int userCountNeed;
    /**
     * '审批人类型 1 :部门；2：角色：3：人员；4：组'
     */
    @TableField(value = "assign_type")
    private Integer assignType= AssignTypeEnum.PERSON.code;
    /**
     * 完成百分比，小数0-1
     */
    @TableField(value = "percentage")
    private Double percentage;
    /**
     * 审批人为角色时 是否需要签收：0不需要；1需要
     */
    @TableField(value = "need_sign")
    private Integer needSign;
    /**
     * 是否需要指定下一节点审批人 0:不需要；1需要
     */
    @TableField(value = "need_set_next")
    private Integer needSetNext;
    /**
     * 表达式
     */
    @TableField(value = "expr")
    private String expr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public String getProcDefName() {
        return procDefName;
    }

    public void setProcDefName(String procDefName) {
        this.procDefName = procDefName;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public void setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateIds() {
        return candidateIds;
    }

    public void setCandidateIds(String candidateIds) {
        this.candidateIds = candidateIds;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getUserCountTotal() {
        return userCountTotal;
    }

    public void setUserCountTotal(int userCountTotal) {
        this.userCountTotal = userCountTotal;
    }

    public int getUserCountNeed() {
        return userCountNeed;
    }

    public void setUserCountNeed(int userCountNeed) {
        this.userCountNeed = userCountNeed;
    }

    public Integer getAssignType() {
        return assignType;
    }

    public void setAssignType(Integer assignType) {
        this.assignType = assignType;
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Integer getNeedSign() {
        return needSign;
    }

    public void setNeedSign(Integer needSign) {
        this.needSign = needSign;
    }

    public Integer getNeedSetNext() {
        return needSetNext;
    }

    public void setNeedSetNext(Integer needSetNext) {
        this.needSetNext = needSetNext;
    }
}
