package com.hengtian.common.result;

import com.common.common.CodeConts;

import java.io.Serializable;

/**
 * @description：操作结果集
 */
public class Result implements Serializable {

    private static final long serialVersionUID = 5576237395711742681L;


    /**
     * 成功返回值
     */
    private final static String SUCCESS_CODE = CodeConts.SUCCESS;

    /**
     * 失败默认返回值
     */
    private final static String FAILED_CODE = CodeConts.FAILURE;

    /**
     * 是否成功 ，默认false
     */
    private boolean success = false;
    /**
     * 消息
     */
    private String msg = "";
    /**
     * code编码
     */
    private String code;
    /**
     * 描述信息
     */
    private String desc;
    /**
     * 数据
     */
    private Object obj = null;
    /**
     * 任务是否结束
     */
    private boolean end;

    public Result(){}

    public Result(String msg){
        this.code = FAILED_CODE;
        this.msg = msg;
        this.success = false;
    }

    public Result(String code, String msg){
        this.code = code;
        this.msg = msg;
        this.success = false;
    }

    public Result(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public Result(boolean success,String code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
