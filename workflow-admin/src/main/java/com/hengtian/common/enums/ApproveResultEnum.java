package com.hengtian.common.enums;

/**
 * 审批人审批结果
 * @author houjinrong@chtwm.com
 * date 2018/5/29 14:25
 */
public enum ApproveResultEnum {

    //审批人通过
    AGREE("_Y", "通过"),
    //审批人拒绝
    REFUSE("_N", "拒绝");

    public String result;

    public String desc;

    ApproveResultEnum(String result, String desc){
        this.result = result;
        this.desc = desc;
    }
}
