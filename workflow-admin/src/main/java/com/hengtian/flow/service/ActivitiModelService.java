package com.hengtian.flow.service;

import com.alibaba.fastjson.JSONArray;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 导出模型
     * @param modelIds 模型ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/20 17:09
     */
    JSONArray exportModel(String[] modelIds);

    /**
     * 导出模型
     * @param file 模型数据文件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/20 17:09
     */
    Result importModel(MultipartFile file);
}
