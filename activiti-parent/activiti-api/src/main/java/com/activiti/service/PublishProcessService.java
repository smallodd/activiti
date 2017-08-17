package com.activiti.service;

import com.activiti.expection.WorkFlowException;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * 此类只用于处理流程定义
 * Created by ma on 2017/7/18.
 */
public interface PublishProcessService {
    /**
     * 发布流程定义
     *
     * @param zipInputStream  bpmn文件和图片的压缩文件
     * @return 返回部署id
     */
    String publish(ZipInputStream zipInputStream) throws WorkFlowException;

    /**
     * 开启流程任务
     *
     * @param publishUserID 发起人唯一标识
     * @param processKey    流程定义时xml中的id
     * @param bussnissKey   业务主键，用于关联业务数据
     * @param map           键：userid，值：用户唯一标识
     * @return 返回流程实例ID
     */
    String startProcess(String publishUserID, String processKey, String bussnissKey, Map<String, Object> map) throws WorkFlowException;

    /**
     * 查询流程定义列表
     * @param startPage  开始页
     * @param pageSize  每页显示数
     * @return  返回流程定义列表
     */
    List<ProcessDefinition> queryList(int startPage, int pageSize);

    /**
     * 删除流程定义
     *
     * @param processId 流程定义id
     */
    void deleteById(String processId);

    /**
     * 通过流程主键查询流程定义key
     * @param processId  流程中的流程id
     * @return  返回流程定义中xml中的key
     */
     String selectProcessKey(String processId);

    /**
     * 通过名字发布流程定义
     * @param name   流程定义名字，包含后缀名
     * @return   返回发布后的id
     */
    String publish(String name);
}
