package com.hengtian.flow.service;



import com.hengtian.common.result.Result;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.workflow.exception.WorkFlowException;
import com.hengtian.flow.vo.TaskVo;

import java.io.InputStream;
import java.util.Map;

/**
 * 工作流服务接口
 * @author liujunyang
 */
public interface ActivitiService {

	/**
	 * 查询流程定义
	 * @param pageInfo
	 */
	public void selectProcessDefinitionDataGrid(PageInfo pageInfo);

	/**
	 * 查询我的待办任务
	 * @param pageInfo
	 */
	public void selectTaskDataGrid(PageInfo pageInfo, boolean isAll, TaskVo taskVo);

	/**
	 * 签收任务
	 * @param userId
	 * @param taskId
	 */
	 void claimTask(String userId, String taskId);

	/**
	 * 办理任务
	 * @param taskId
	 * @param commentContent
	 * @param commentResult
	 */
	Result completeTask(String taskId, String userId, String commentContent, Integer commentResult);

	/**
	 * 获取流程资源文件
	 * @param resourceType
	 * @param processInstanceId
	 * @return
	 */
	 InputStream getProcessResource(String resourceType, String processInstanceId);

	/**
	 * 委派任务
	 * @param userId
	 * @param taskId
	 */
	 void delegateTask(String userId, String taskId);

	/**
	 * 转办任务
	 * @param userId
	 * @param taskId
	 */
	 void transferTask(String userId, String taskId);

	/**
	 * 跳转任务
	 * @param taskId
	 * @param taskDefinitionKey
	 */
	 void jumpTask(String taskId, String taskDefinitionKey) throws WorkFlowException;

	/**
	 * 我的已办任务
	 * @param pageInfo
	 */
	 void selectHisTaskDataGrid(PageInfo pageInfo, boolean flag, TaskVo taskVo);
	
	
	/**
	 * 提供公共的发送邮件服务
	 */
	 void sendMailService(Map<String, Object> params);

}
