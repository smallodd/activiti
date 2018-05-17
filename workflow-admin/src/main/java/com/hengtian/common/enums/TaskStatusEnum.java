package com.hengtian.common.enums;

/**
 * 任务所属不同状态  催办的状态 / 问询的状态 / 任务状态
 * @author houjinrong@chtwm.com
 * date 2018/4/20 9:51
 */
public enum TaskStatusEnum {

    /**
     * 催办状态
     */
    REMIND_UNFINISHED(0, "未完成"),
    REMIND_FINISHED(1, "完成"),

    /**
     * 任务办理状态
     */
    //审批通过
    COMPLETE_AGREE(1, "completed"),
    //审批拒绝
    COMPLETE_REFUSE(2, "refused"),

    /**
     * 任务状态
     */
    BEFORESIGN(-1, "待签收"),
    OPEN(0, "待处理"),
    AGREE(1, "同意"),
    REFUSE(2, "拒绝"),
    SKIP(3, "略过");

    public int status;

    public String desc;

    TaskStatusEnum(int status, String desc){
        this.status = status;
        this.desc = desc;
    }

    public static String getCloseStatus(){
        return AGREE.status + "," + REFUSE.status;
    }

    public static String getCOMPLETEStatus(){
        return COMPLETE_AGREE.desc + "," + COMPLETE_REFUSE.desc;
    }
}
