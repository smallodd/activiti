package com.chtwm.workflow.service.impl;

import com.alibaba.dubbo.config.annotation.Service;

import com.chtwm.workflow.entity.TaskModelVO;
import com.chtwm.workflow.entity.TaskNoticePO;
import com.chtwm.workflow.mapper.XiaoKeMessageProjectMapper;
import com.chtwm.workflow.service.XiaoKeMessageService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanyuexing
 * @date 2019/11/8 9:18
 */
@Service
@Slf4j
public class XiaoKeMessageserviceImpl implements XiaoKeMessageService {

    @Resource
    private XiaoKeMessageProjectMapper xiaoKeMessageProjectMapper;

    /**
     * 查询所有未发送以及发送失败的信息
     */
    @Override
    public List<TaskModelVO> getNeedSendTaskNotice() {
        return xiaoKeMessageProjectMapper.getAllUnSendMessage();
    }

    /**
     * 根据主键修改纷享逍客通知状态（消息通知成功还是通知失败)
     */
    @Override
    public Integer updateNoticeState(Long id, Integer state) {
        if (null == id || null == state){
            log.info("根据主键修改纷享逍客通知状态 方法  主键id为{} 待修改的状态为{}",id,state);
            return -1;
        }
        return xiaoKeMessageProjectMapper.updateThisMessageNoticeState(id,state);
    }

    /**
     * 根据主键集合修改纷享逍客通知状态（消息通知成功还是通知失败)
     */
    @Override
    public Integer updateNoticeStateByListKeys(List<Long> ids, Integer state) {
        if (null == ids || ids.size()==0 || null == state){
            log.info("根据主键集合修改纷享逍客通知状态 方法 主键集合或待修改的状态为空 主键id的集合为{} 待修改的状态为{}",ids,state);
            return -1;
        }
        return xiaoKeMessageProjectMapper.updateNoticeStateByListKeys(ids,state);
    }
}
