package com.chtwm.workflow.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanyuexing
 * @date 2019/11/7 17:26
 * 任务通知的实体类
 */
@Data
public class TaskNoicePO implements Serializable {

    private static final long serialVersionUID = 8545100665157685064L;

    /**
     * 主键
     */
    private String id;

    /**
     * 流程id
     */
    private String procInstId;

    /**
     * 流程名称
     */
    private String procInstName;

    /**
     * 系统主键
     */
    private String appKey;

    /**
     * 业务主键
     */
    private String businessKey;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 处理人编号
     */
    private String empNo;

    /**
     * 处理人姓名
     */
    private String empName;

    /**
     * 任务类型 0审批 1转办 2问询 3回复
     */
    private Integer type;

    /**
     * 任务状态 0未处理 1已处理
     */
    private Integer state;

    /**
     * 处理结果 1通过  2拒绝
     */
    private Integer action;

    /**
     * 处理意见
     */
    private String message;

    /**
     * 纷享逍客通知状态 0未通知 1已通知 2通知失败
     */
    private Integer xkNoticeState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人编号
     */
    private String createId;

    /**
     * 创建人姓名
     */
    private String createName;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 更新人编号
     */
    private String updateId;

    /**
     * 更新人姓名
     */
    private String updateName;

    /**
     * 是否删除 0正常 1已删除
     */
    private Integer isDelete;

}
