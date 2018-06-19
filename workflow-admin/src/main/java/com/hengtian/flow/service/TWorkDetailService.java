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
}
