package com.hengtian.activiti.service.impl;

import com.hengtian.activiti.service.ActivitiModelService;
import com.hengtian.common.utils.PageInfo;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程模型服务接口
 * @author houjinrong
 */
@Service
public class ActivitiModelServiceImpl implements ActivitiModelService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RepositoryService repositoryService;

    /**
     * 查询流程模型
     * @param pageInfo
     */
    @Override
    public void selectActivitiModelDataGrid(PageInfo pageInfo,String name){
        if(name != null){
            name = "%"+name+"%";
            List<Model> modelList = repositoryService.createModelQuery().modelNameLike(name).listPage(pageInfo.getFrom(), pageInfo.getSize());
            pageInfo.setRows(modelList);
            long count= repositoryService.createModelQuery().modelNameLike(name).count();
            pageInfo.setTotal(Integer.parseInt(String.valueOf(count)));
        }else{
            List<Model> modelList = repositoryService.createModelQuery().listPage(pageInfo.getFrom(), pageInfo.getSize());
            pageInfo.setRows(modelList);
            long count= repositoryService.createModelQuery().count();
            pageInfo.setTotal(Integer.parseInt(String.valueOf(count)));
        }
    }
}
