package com.hengtian.common.enums;

import com.alibaba.fastjson.JSONObject;
import com.hengtian.common.utils.StringUtils;
import org.apache.commons.lang3.EnumUtils;

/**
 * 任务操作类型枚举
 * @author houjinrong@chtwm.com
 * date 2018/4/18 11:10
 */
public enum TaskActionEnum {

    CLAIM("claim","认领"),
    UNCLAIM("unclaim","取消认领"),
    JUMP("jump","跳转"),
    TRANSFER("transfer","转办"),
    REMIND("remind","催办"),
    ENQUIRE("enquire","问询"),
    CONFIRMENQUIRE("confirmEnquire","确认问询"),
    REVOKE("revoke","撤回"),
    ROLLBACK("rollback","驳回"),
    CANCEL("cancel","取消"),
    SUSPEND("suspend","挂起"),
    ACTIVATE("activate","激活");

    public String value;

    public String desc;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    TaskActionEnum(String value, String desc){
        this.value = value;
        this.desc = desc;
    }

    public static String getDesc(String value){
        for(TaskActionEnum actionEnum : TaskActionEnum.values()){
            if(actionEnum.getValue().equals(value)){
                return actionEnum.getDesc();
            }
        }

        return null;
    }

    /**
     * 判断某个值是否在枚举类型中
     * @author houjinrong@chtwm.com
     * date 2018/4/18 11:28
     */
    public static boolean contains(String value){
        if(StringUtils.isBlank(value)){
            return false;
        }
        return EnumUtils.isValidEnum(TaskActionEnum.class, value.toUpperCase());
    }

    public static String valuesToString(){
        JSONObject json = new JSONObject();
        for (TaskActionEnum taskActionEnum : TaskActionEnum.values()) {
            json.put(taskActionEnum.value, taskActionEnum.desc);
        }
        return json.toJSONString();
    }

    public static void main(String[] args) {
        System.out.println(getDesc("activate"));
        System.out.println(TaskActionEnum.valuesToString());
    }

    public static TaskActionEnum getCurrent(String value){
        return  EnumUtils.getEnum(TaskActionEnum.class, value);
    }
}
