package com.hengtian.application.model;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * <p>
 * 请假表
 * </p>
 *
 * @author junyang.liu
 * @since 2017-08-18
 */
@TableName("t_vacation")
public class TVacation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value="id",type = IdType.UUID)
    private String id;
    /**
     * 请假单号
     */
    @TableField(value="vacation_code")
    private String vacationCode;
    /**
     * 申请日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="apply_date")
    private java.util.Date applyDate;
    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="begin_date")
    private java.util.Date beginDate;
    /**
     * 请假天数
     */
    @TableField(value="work_days")
    private Integer workDays;
    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(value="end_date")
    private java.util.Date endDate;
    /**
     * 流程实例ID
     */
    @TableField(value="proc_inst_id")
    private String procInstId;
    /**
     * 用户ID
     */
    @TableField(value="user_id")
    private String userId;
    /**
     * 请假原因
     */
    @TableField(value="vacation_reason")
    private String vacationReason;
    /**
     * 请假状态(1.正在审批  2.审批通过  3.审批不通过)
     */
    @TableField(value="vacation_status")
    private Integer vacationStatus;
    /**
     * 请假类型(1.事假  2.病假)
     */
    @TableField(value="vacation_type")
    private Integer vacationType;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public java.util.Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(java.util.Date applyDate) {
        this.applyDate = applyDate;
    }

    public java.util.Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(java.util.Date beginDate) {
        this.beginDate = beginDate;
    }

    public Integer getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Integer workDays) {
        this.workDays = workDays;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVacationReason() {
        return vacationReason;
    }

    public void setVacationReason(String vacationReason) {
        this.vacationReason = vacationReason;
    }

	public Integer getVacationStatus() {
		return vacationStatus;
	}

	public void setVacationStatus(Integer vacationStatus) {
		this.vacationStatus = vacationStatus;
	}

	public Integer getVacationType() {
		return vacationType;
	}

	public void setVacationType(Integer vacationType) {
		this.vacationType = vacationType;
	}

	public String getVacationCode() {
		return vacationCode;
	}

	public void setVacationCode(String vacationCode) {
		this.vacationCode = vacationCode;
	}

}
