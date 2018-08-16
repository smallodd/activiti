package com.hengtian.flow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hengtian.common.param.WorkDetailParam;
import com.hengtian.flow.model.TWorkDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chenzhangyan  on 2018/4/24.
 */
public interface TWorkDetailDao extends BaseMapper<TWorkDetail> {
    List<TWorkDetail> operateDetailList(Page<TWorkDetail> page, WorkDetailParam workDetailParam);

    /**
     * 获取最新的操作记录
     * @param processInstanceId 流程实例ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/8/16 17:18
     */
    TWorkDetail queryLastInfo(@Param("processInstanceId") String processInstanceId);
}
