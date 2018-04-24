package com.hengtian.flow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hengtian.common.param.WorkDetailParam;
import com.hengtian.flow.model.TWorkDetail;

import java.util.List;

/**
 * @author chenzhangyan  on 2018/4/24.
 */
public interface TWorkDetailDao extends BaseMapper<TWorkDetail> {
    List<TWorkDetail> operateDetailList(Page<TWorkDetail> page, WorkDetailParam workDetailParam);
}
