package com.activiti.service;

import org.activiti.engine.repository.ProcessDefinition;

import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * Created by ma on 2017/7/18.
 */
public interface PublishProcessService {
    /**
     * 发布流程定义
     * @param zipInputStream
     * @return  返回流程定义id
     */
    String publish(ZipInputStream zipInputStream);

    /**
     * 开启流程任务
     * @param publishUserID 发起人唯一标识
     * @param processId  流程定义id
     * @param bussnissKey  业务主键，用于关联业务数据
     * @param map   键：userid，值：用户唯一标识
     * @return   返回业务主键表示成功
     */
    String startProcess(String publishUserID,String processId,String bussnissKey, Map<String, Object> map);

    /**
     * 查询流程定义列表
     * @param startPage
     * @param pageSize
     * @return
     */
    List<ProcessDefinition> queryList(int startPage, int pageSize);

    /**
     * 删除流程定义
     * @param processId  流程定义id
     */
    void deleteById(String processId);


    public String publish(String name);
}
