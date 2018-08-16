package com.hengtian.flow.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.flow.model.TWorkDetail;

import java.util.List;

/**
 * 操作流程详细信息
 *
 * @author chenzhangyan  on 2018/4/24.
 */
public interface TWorkDetailService extends IService<TWorkDetail> {
    /**
     * 操作流程详细信息
     *
     * @param processInstanceId 流程实例
     * @param operator 操作人
     * @return
     */
    List<TWorkDetail> operateDetailInfo(String processInstanceId, String operator,String businessKey);

    /**
     * 获取最新的操作记录
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/16 17:18
     */
    TWorkDetail queryLastInfo(String processInstanceId);
}
