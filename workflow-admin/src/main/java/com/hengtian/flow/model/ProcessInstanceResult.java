package com.hengtian.flow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hengtian.flow.vo.TaskNodeVo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class ProcessInstanceResult {

    /**
     * 流程编号
     */
    private String processInstanceId;
    /**
     * 标题
     */
    private String processInstanceName;
    /**
     * 流程名称
     */
    private String processDefinitionName;
    /**
     * 流程状态 0-进行中；1-通过；2-拒绝
     */
    private String processInstanceState;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 当前节点信息
     */
    private List<TaskNodeVo> currentTaskNode;
    /**
     * 业务主键
     */
    private String businessKey;
    /**
     * 系统标识
     */
    @JsonIgnore
    private Integer appKey;
    /**
     * 发起时间
     */
    private Date startTime;
    /**
     * 完成时间
     */
    private Date endTime;
}
