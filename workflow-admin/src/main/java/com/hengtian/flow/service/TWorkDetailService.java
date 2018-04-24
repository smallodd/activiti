package com.hengtian.flow.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.param.WorkDetailParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.model.TWorkDetail;

/**
 * 操作记录详情接口
 *
 * @author chenzhangyan  on 2018/4/24.
 */
public interface TWorkDetailService extends IService<TWorkDetail> {
    PageInfo operateDetailInfo(WorkDetailParam workDetailParam);
}
