package com.hengtian.common.enums;

public enum TaskStatus {

    //未完成
    UNFINISHED("unfinished"),
    //审核完成-通过
    FINISHEDPASS("finished_pass"),
    //审核完成-未通过
    FINISHEDREFUSE("finished_refuse"),
    //转办
    TRANSFER("transferTask");

    public String value;

    TaskStatus(String value){
        this.value = value;
    }

}
