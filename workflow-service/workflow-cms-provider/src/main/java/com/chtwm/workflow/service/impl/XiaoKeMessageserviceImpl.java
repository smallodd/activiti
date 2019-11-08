package com.chtwm.workflow.service.impl;

import com.alibaba.dubbo.config.annotation.Service;

import com.chtwm.workflow.entity.TaskNoicePO;
import com.chtwm.workflow.mapper.XiaoKeMessageProjectMapper;
import com.chtwm.workflow.service.XiaoKeMessageService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanyuexing
 * @date 2019/11/8 9:18
 */
@Service
public class XiaoKeMessageserviceImpl implements XiaoKeMessageService {

    @Resource
    private XiaoKeMessageProjectMapper xiaoKeMessageProjectMapper;

    @Override
    //查询所有未发送以及发送失败的信息
    public List<TaskNoicePO> getNeedSendTaskNotice() {
        List<TaskNoicePO> list = xiaoKeMessageProjectMapper.getAllUnSetMessage();
        return list;
    }

    @Override
    //根据主键修改纷享逍客通知状态（消息通知成功还是通知失败)
    public Integer updateNoticeState(String id, Integer state) {
        return xiaoKeMessageProjectMapper.updateThisMessageNoticeState(id,state);
    }
}
