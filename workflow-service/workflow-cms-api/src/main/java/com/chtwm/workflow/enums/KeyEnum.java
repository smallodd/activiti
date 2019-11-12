package com.chtwm.workflow.enums;

/**
 * @author fanyuexing
 * @date 2019/11/12 21:08
 */
public enum KeyEnum {

    TEXT(2,"测试"),
    PRODUCT(13,"产品活动"),
    MARKET(1,"营销活动"),
    INTERNATIONAL_CAPITAL(4,"国际资本");

    public Integer key;
    public String value;

    KeyEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public java.lang.Integer getKey() {
        return key;
    }

    public void setKey(java.lang.Integer key) {
        this.key = key;
    }
}
