package com.hengtian.activiti.model;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * <p>
 * 用户任务表
 * </p>
 *
 * @author junyang.liu
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
     * 候选人名称(多个)
     */
    @TableField(value="candidate_name")
    private String candidateName;
    /**
     * 候选人ID(多个)
     */
    @TableField(value="candidate_ids")
    private String candidateIds;
    /**
     * 处理人顺序
     */
    @TableField(value="order_num")
    private int orderNum;
    @TableField(value = "version_")
    private int version;


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
}
