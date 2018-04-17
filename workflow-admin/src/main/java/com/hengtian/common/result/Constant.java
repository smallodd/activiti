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
     * 业务主键已经创建过任务
     */
    public static final String BUSSINESSKEY_EXIST="1001";



}
