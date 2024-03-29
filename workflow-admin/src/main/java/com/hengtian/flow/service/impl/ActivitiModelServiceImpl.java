package com.hengtian.flow.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.flow.service.ActivitiModelService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.NativeModelQuery;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 流程模型服务接口
 * @author houjinrong
 */
@Slf4j
@Service
public class ActivitiModelServiceImpl implements ActivitiModelService {


    @Autowired
    RepositoryService repositoryService;

    /**
     * 查询流程模型
     * @param pageInfo
     */
    @Override
    public void selectActivitiModelDataGrid(PageInfo pageInfo,String name, String appKey){
        if(StringUtils.isNotBlank(appKey)){
            NativeModelQuery nativeModelQuery = repositoryService.createNativeModelQuery();
            nativeModelQuery.parameter("appKey", appKey);
            StringBuffer sb = new StringBuffer();
            sb.append(" FROM act_re_model AS arm LEFT JOIN t_app_model AS tam ON arm.KEY_=tam.model_key WHERE tam.app_key=#{appKey} ");

            if(StringUtils.isNotBlank(name)){
                sb.append("arm.NAME_ LIKE #{name}");
                nativeModelQuery.parameter("name", name);
            }

            long count = nativeModelQuery.sql("SELECT COUNT(*)" + sb.toString()).count();
            List<Model> modelList = nativeModelQuery.sql("SELECT arm.*" + sb.toString() + "ORDER BY arm.CREATE_TIME_ DESC").listPage(pageInfo.getFrom(), pageInfo.getSize());
            pageInfo.setRows(modelList);
            pageInfo.setTotal(Integer.parseInt(String.valueOf(count)));
        }else{
            ModelQuery modelQuery = repositoryService.createModelQuery();
            if(StringUtils.isNotBlank(name)){
                modelQuery = modelQuery.modelNameLike("%"+name+"%");
            }
            List<Model> modelList = modelQuery.orderByCreateTime().desc().listPage(pageInfo.getFrom(), pageInfo.getSize());
            long count= modelQuery.count();
            pageInfo.setRows(modelList);
            pageInfo.setTotal(Integer.parseInt(String.valueOf(count)));
        }
    }

    /**
     * 导出模型
     * @param modelIds 模型ID
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/20 17:09
     */
    @Override
    public JSONArray exportModel(String[] modelIds) {
        Model model = null;
        JSONArray result = new JSONArray();
        try {
            for(String modelId : modelIds){
                model = repositoryService.getModel(modelId);
                if(model == null){
                    log.info("模型ID【"+modelId+"】没有对应的模型存在");
                }
                byte[] modelEditorSource = repositoryService.getModelEditorSource(modelId);
                byte[] modelEditorSourceExtra = repositoryService.getModelEditorSourceExtra(modelId);
                JSONObject modelJson = new JSONObject();
                String modelEditorSourceStr = new String(modelEditorSource, "utf-8");
                String modelEditorSourceExtraStr = new String(Base64.encodeBase64(modelEditorSourceExtra));
                modelJson.put("modelEditorSource", modelEditorSourceStr);
                modelJson.put("modelEditorSourceExtra", modelEditorSourceExtraStr);
                modelJson.put("name", model.getName());
                modelJson.put("key", model.getKey());
                modelJson.put("mateInfo", model.getMetaInfo());
                modelJson.put("version", model.getVersion());

                result.add(modelJson);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {

        }

        return result;
    }

    /**
     * 导出模型
     * @param file 模型数据文件
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/6/20 17:09
     */
    @Override
    public Result importModel(MultipartFile file) {
        return null;
    }
}
