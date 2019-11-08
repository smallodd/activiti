package com.chtwm.workflow.service;


import com.chtwm.workflow.entity.TaskNoticePO;

import java.util.List;

/**
 * @author fanyuexing
 * @date 2019/11/8 9:14
 */
public interface XiaoKeMessageService {

    /**
     * 查询所有未发送以及发送失败的信息
     */
    List<TaskNoticePO> getNeedSendTaskNotice();

    /**
     * 根据主键修改纷享逍客通知状态（消息通知成功还是通知失败)
     */
    Integer updateNoticeState(Long id,Integer state);

}
