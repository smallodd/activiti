package com.hengtian.common.result;

/**
 * Created by ma on 2018/4/17.
 * 常量code编码
 * 规则： 所有涉及到操作类的如转办，跳转，审批，创建等，编码开头都是1，
 * 所有涉及到列表错误编码都是2开头，
 * 涉及到其他类型的3开头，
 * 所有成功或失败编码都是0000或9999
 */
public class Constant {
    /**
     * 成功编码
     */
    public static final String SUCCESS="0000";
    /**
     * 失败编码
     */
    public static final String FAIL="9999";
    /**
     * 参数不合法
     */
    public static final String PARAM_ERROR="3999";
    /**
     * 业务主键已经创建过任务
     */
    public static final String BUSSINESSKEY_EXIST="1001";
    /**
     * 系统与流程定义之间关系不存在
     */
    public static final String RELATION_NOT_EXIT="1002";
    /**
     * 任务类型不正确
     */
    public static final String TASK_TYPE_ERROR="1003";
    /**
     * 审批人类型不存在
     */
    public static final String ASSIGN_TYPE_ERROR="1004";
    /**
     * 任务不存在
     */
    public static final String TASK_NOT_EXIT="1005";
    /**
     * 意见征询的任务不能继续审批
     */
    public static final String ASK_TASK_EXIT="1006";
    /**
     * 任务不属于该用户
     */
    public static final String TASK_NOT_BELONG_USER="1007";
    /**
     * 审批人未设置
     */
    public static final String TASK_NOT_SET_APPROVER="1008";

    /**
     * 委派时重复
     */
    public static final String AGENT_HAVE_EXIST="2001";

}
