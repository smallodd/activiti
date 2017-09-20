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
     * 操作成功
     **/
    public final static String SUCCESS = "0000";

    /**
     * 操作成功,数据为空
     **/
    public final static String DATA_IS_NUll = "1000";

    /**
     * 操作失败
     **/
    public final static String FAILURE = "4000";

    /**
     * 参数不合法:非法请求过来参数
     **/
    public final static String PARAM_LEGAL = "4001";

    /**
     * 入库失败
     **/
    public final static String INSERT_DB_ERR = "4002";

    /**
     * 无更新数据
     **/
    public final static String NOT_UPDATE_DATA = "4003";

    /**
     * 存在敏感词汇
     **/
    public final static String EXISTS_SENSITIVE_WORD = "4004";

    /**
     * 推送失败
     **/
    public final static String MSG_SEND_FAIL_ERR = "4005";

    /**
     * 其他异常
     **/
    public final static String INSERT_DB_AFTER_ERR = "4006";

    /**
     * 登录失败
     **/
    public final static String LOGIN_FAILURE = "4007";

    /**
     * 返回值错误
     **/
    public final static String RESULT_FAIURE = "4008";

    /**
     * 系统异常
     **/
    public final static String SYS_ERR = "4009";


    /**
     * 验证码不能为空
     **/
    public final static String AUCD_NOT_NULL = "W00001";

    /**
     * 获取验证码失败
     **/
    public final static String AUCD_FAIURE = "W00002";

    /**
     * 验证码错误次数超出限制
     **/
    public final static String AUCD_ERROR_LIMIT = "W00003";

    /**
     * 验证码过期
     **/
    public static final String AUCD_TIMEOUT = "W00004";

    /**
     * 验证码错误
     **/
    public final static String AUCD_ERROR = "W00005";

    /**
     * 审批流错误代码
     * 部署失败报的错误
     */
    public final static String WORK_FLOW_PUBLISH_ERROR = "W1001";

    /**
     * 申请人为空
     */
    public final static String WORK_FLOW_APPLY_USER = "W1002";

    /**
     * 流程定义id不能为空
     */
    public final static String WORK_FLOW_DEFINED_ERROR = "W1003";

    /**
     * 业务id不能为空
     */
    public final static String WORK_FLOW_BUSSINESS_KEY_ERROR = "W1004";

    /**
     * 参数不合法
     */
    public final static String WORK_FLOW_PARAM_ERROR = "W1005";
    /**
     * 此流程还有节点，请传下一审批人
     */
    public final static String WORK_FLOW_IS_NOT_FINISH="W1006";
}
