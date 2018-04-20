package com.hengtian.common.enums;

/**
 * 任务所属不同状态  催办的状态 / 问询的状态 / 任务状态
 * @param null
 * @return 
 * @author houjinrong@chtwm.com
 * date 2018/4/20 9:51
 */
public enum TaskStatusEnum {

    /**
     * 催办状态
     */
    REMIND_UNFINISHED(0, "未完成"),
    REMIND_FINISHED(1, "完成");

    public int status;

    public String desc;

    TaskStatusEnum(int status, String desc){
        this.status = status;
        this.desc = desc;
    }
}
