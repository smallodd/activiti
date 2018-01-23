package com.hengtian.common.enums;

public enum TaskStatus {

    //未完成
    UNFINISHED("0:unfinished"),
    //完成
    FINISHED("1:finished"),
    //转办
    TRANSFER("transferTask");

    public String value;

    TaskStatus(String value){
        this.value = value;
    }

    public static void main(String[] args) {
        System.out.println(TaskStatus.FINISHED.value);
    }
}
