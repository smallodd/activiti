package com.activiti.service;

import com.activiti.expection.WorkFlowException;
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
     * @return  返回部署id
     */
    String publish(ZipInputStream zipInputStream) throws WorkFlowException;

    /**
     * 开启流程任务
     * @param publishUserID 发起人唯一标识
     * @param processKey  流程定义时xml中的id
     * @param bussnissKey  业务主键，用于关联业务数据
     * @param map   键：userid，值：用户唯一标识
     * @return  true:成功;false:失败
     */
    boolean startProcess(String publishUserID,String processKey,String bussnissKey, Map<String, Object> map) throws WorkFlowException;

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
