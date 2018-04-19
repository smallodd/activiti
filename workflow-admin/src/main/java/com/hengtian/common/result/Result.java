package com.hengtian.common.result;

import java.io.Serializable;

/**
 * @description：操作结果集
 */
public class Result implements Serializable {



    private static final long serialVersionUID = 5576237395711742681L;
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
     * 数据
     */
    private Object obj = null;

    public Result() {
    }

    public Result(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
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

}
