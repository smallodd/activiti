package com.hengtian.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 我的待办列表查询-我的代理信息
 * @author houjinrong@chtwm.com
 * date 2018/8/6 14:23
 */
@Data
public class TaskAgentQueryParam {

    /**
     * 被代理人工号
     */
    @ApiModelProperty(value = "代理人工号", example = "H019233")
    private String assigneeAgent;
    /**
     * 被代理人角色编号
     */
    @ApiModelProperty(value = "被代理人角色编号",hidden = true, example = "001")
    private String agentRoleId;
    /**
     * 代理开始日期
     */
    @ApiModelProperty(value = "代理开始日期", example = "2018-01-01")
    private String agentStartDate;
    /**
     * 代理结束日期
     */
    @ApiModelProperty(value = "代理结束日期", example = "2018-08-08")
    private String agentEndDate;
}
