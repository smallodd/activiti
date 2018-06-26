package com.hengtian.flow.vo;

import lombok.Data;

/**
 * 审批人信息
 * @author houjinrong@chtwm.com
 * date 2018/6/9 10:36
 */
@Data
public class AssigneeVo {

    /**
     * 审批人工号
     */
    private String userCode;
    /**
     * 审批人名称
     */
    private String userName;
    /**
     * 是否审批结束 0：未结束 1：结束
     */
    private int isComplete;
}
