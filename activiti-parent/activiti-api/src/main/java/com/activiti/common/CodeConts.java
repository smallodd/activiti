package com.activiti.common;


/**
 * 为了保持错误状态码纯洁性，由一人管理分配<br>
 * <br>
 * 状态码<br>
 * 格式：平台+功能模块+功能<br>
 * 平台(1位)：<br>
 * W:web应用；I：对外平台提供接口<br>
 * <br>
 * 功能编号同一平台同一功能模块下编号唯一(3位数字)：<br>
 * <br>
 * 例：W01001、W01001、I01001<br>
 * <p>
 * 特殊状态码：<br>
 * 所有操作成功的状态码为0000;所有操作失败的状态码为4000
 *
 * @author zhouxy
 */

public class CodeConts {

    /**
     * 参数不合法
     */
    public final static String WORK_FLOW_PARAM_ERROR = "W1000";

    /**
     * 模型与系统关系不存在，请检查应用管理是否有此关系
     */
    public final static String WORK_FLOW_NOT_RELATION = "W1001";

    /**
     * 业务主键还在流程中
     */
    public final static String WORK_FLOW_BUSSINESS_IN_FLOW= "W1002";

    /**
     * 请不要设置重复的属性和commonVo中有的属性
     */
    public final static String WORK_FLOW_PARAM_REPART = "W1003";

    /**
     * 审批人没有配置全
     */
    public final static String WORK_FLOW_NO_APPROVER="W1004";
    /**
     * 邮件发送失败
     */
    public final static String WORK_FLOW_SEND_FAIL="W1005";
    /**
     * 审批失败，任务已完成或非法请求
     */
    public final static String WORK_FLOW_ERROR_TASK="W1006";
    /**
     * 提交失败，任务已提交
     */
    public final static String WORK_FLOW_PUBLISH_ERROR="W1007";
    /**
     * 任务为空
     */
    public final static String WORK_FLOW_TASK_IS_NULL="W1008";
    /**
     * 流程不存在或已损坏
     */
    public final static String PROCESS_ERROR="W1009";
    /**
     * 流程实例不存在
     */
    public final static String PROCESS_NOEXISTS="W1010";
}
