package com.chtwm.workflow.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.chtwm.workflow.entity.TaskModelVO;
import com.chtwm.workflow.mapper.QWeChatMessageProjectMapper;
import com.chtwm.workflow.service.QWeChatMessageService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanyuexing
 * @date 2020/4/21 17:51
 */
@Slf4j
@Service
public class QWeChatMessageServiceImpl implements QWeChatMessageService {

    @Resource
    private QWeChatMessageProjectMapper qWeChatMessageProjectMapper;

    /**
     * 获取企业微信所有待通知或通知失败的消息
     * @return
     */
    @Override
    public List<TaskModelVO> getNeedSendTaskNotice() {
        return qWeChatMessageProjectMapper.getAllUnSendMessage();
    }

    /**
     * 根据主键修改通知消息的状态
     * @param id 主键
     * @param state  要修改的状态
     * @return
     */
    @Override
    public Integer updateNoticeState(Long id, Integer state) {
        if (null == id || null == state){
            log.info("根据主键修改企业微信通知状态 方法  主键id为{} 待修改的状态为{}",id,state);
            return  -1;
        }
        return qWeChatMessageProjectMapper.updateThisMessageNoticeState(id,state);
    }

    /**
     * 根据主键集合修改通知消息的状态
     * @param ids 主键集合
     * @param state 要修改的状态
     * @return
     */
    @Override
    public Integer updateNoticeStateByListKeys(List<Long> ids, Integer state) {
        if (null == ids || ids.size()==0 || null == state){
            log.info("根据主键集合修改企业微信通知状态 方法 主键集合或待修改的状态为空 主键id的集合为{} 待修改的状态为{}",ids,state);
            return -1;
        }
        return qWeChatMessageProjectMapper.updateNoticeStateByListKeys(ids, state);
    }
}
