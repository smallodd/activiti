package com.hengtian.common.enums;

/**
 * 请求返回code和提示信息
 * @author houjinrong@chtwm.com
 * date 2018/4/19 13:20
 */
public enum ResultEnum {

    /*********************系统（0***）********************/
    SUCCESS("0000", "成功"),
    FAIL("9999","失败"),
    PARAM_ERROR("0001","参数不合法"),
    PERMISSION_DENY("0002","没有权限"),
    ILLEGAL_REQUEST("0003","非法请求"),

    /*********************流程（1***）********************/
    BUSSINESSKEY_EXIST("1001","业务主键已经创建过任务"),
    RELATION_NOT_EXIT("1002","系统与流程定义之间关系不存在"),
    PROCINST_NOT_EXIT("1003","流程实例不存在"),

    /*********************任务（2***）********************/
    TASK_TYPE_ERROR("2001","任务类型不正确"),
    ASSIGN_TYPE_ERROR("2002","审批人类型不存在"),
    TASK_NOT_EXIT("2003","任务不存在");


    public String code;

    public String msg;

    ResultEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
