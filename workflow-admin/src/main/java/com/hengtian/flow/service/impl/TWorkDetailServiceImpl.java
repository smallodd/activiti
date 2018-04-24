package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.common.param.WorkDetailParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.dao.TWorkDetailDao;
import com.hengtian.flow.model.TWorkDetail;
import com.hengtian.flow.service.TWorkDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chenzhangyan  on 2018/4/24.
 */
@Service
public class TWorkDetailServiceImpl extends ServiceImpl<TWorkDetailDao, TWorkDetail> implements TWorkDetailService {
    TWorkDetailDao tWorkDetailDao;
    @Override
    public PageInfo operateDetailInfo(WorkDetailParam workDetailParam) {
        PageInfo pageInfo = new PageInfo(workDetailParam.getPageNum(), workDetailParam.getPageSize());
        Page<TWorkDetail> page = new Page(workDetailParam.getPageNum(), workDetailParam.getPageSize());
        List<TWorkDetail> list = tWorkDetailDao.operateDetailList(page, workDetailParam);
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }
}
