package com.hengtian.common.enums;

/**
 * 通用类型
 * @author houjinrong@chtwm.com
 * date 2018/6/6 14:00
 */
public enum CommonEnum {

    DEFAULT(0,"默认"),
    OTHER(1,"其他");

    public Integer value;
    public String name;

    public Integer getCode() {
        return value;
    }

    public void setCode(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    CommonEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
