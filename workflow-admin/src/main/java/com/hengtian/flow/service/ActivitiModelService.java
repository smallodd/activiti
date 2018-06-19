package com.hengtian.flow.service;

import com.baomidou.mybatisplus.service.IService;

import com.hengtian.common.utils.PageInfo;

/**
 * 流程模型服务接口
 * @author houjinrong
 */
public interface ActivitiModelService  {

    /**
     * 查询流程模型
     * @param pageInfo
     */
    void selectActivitiModelDataGrid(PageInfo pageInfo, String name);
}
