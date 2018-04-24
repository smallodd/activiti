package com.hengtian.flow.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.param.WorkDetailParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.model.TWorkDetail;

/**
 * 操作流程详细信息
 *
 * @author chenzhangyan  on 2018/4/24.
 */
public interface TWorkDetailService extends IService<TWorkDetail> {
    /**
     * 操作流程详细信息
     *
     * @param workDetailParam
     * @return
     */
    PageInfo operateDetailInfo(WorkDetailParam workDetailParam);
}
