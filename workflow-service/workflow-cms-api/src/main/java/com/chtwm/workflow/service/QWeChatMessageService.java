package com.chtwm.workflow.service;

import com.chtwm.workflow.entity.TaskModelVO;

import java.util.List;

/**
 * @author fanyuexing
 * @date 2020/4/21 17:32
 */
public interface QWeChatMessageService {

    /**
     * 查询所有未发送以及发送失败的信息（企业微信）
     */
    List<TaskModelVO> getNeedSendTaskNotice();

    /**
     * 根据主键修改企业微信通知状态（消息通知成功还是通知失败)
     * @param id 主键
     * @param state  要修改的状态
     * @return
     */
    Integer updateNoticeState(Long id,Integer state);

    /**
     * 根据主键集合修改企业微信通知状态（消息通知成功还是通知失败)
     * @param ids 主键集合
     * @param state 要修改的状态
     * @return
     */
    Integer updateNoticeStateByListKeys(List<Long> ids,Integer state);

}
