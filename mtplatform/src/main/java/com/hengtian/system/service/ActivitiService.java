package com.hengtian.system.service;

import java.io.InputStream;

import com.hengtian.common.utils.PageInfo;

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
	public void selectTaskDataGrid(PageInfo pageInfo);

	/**
	 * 签收任务
	 * @param userId
	 * @param taskId
	 */
	public void claimTask(String userId, String taskId);

	/**
	 * 获取流程资源文件
	 * @param resourceType
	 * @param processInstanceId
	 * @return
	 */
	public InputStream getProcessResource(String resourceType, String processInstanceId);

	/**
	 * 委派任务
	 * @param userId
	 * @param taskId
	 */
	public void delegateTask(String userId, String taskId);

	/**
	 * 转办任务
	 * @param userId
	 * @param taskId
	 */
	public void transferTask(String userId, String taskId);

	/**
	 * 跳转任务
	 * @param taskId
	 * @param taskDefinitionKey
	 */
	public void jumpTask(String taskId, String taskDefinitionKey);

	/**
	 * 我的已办任务
	 * @param pageInfo
	 */
	public void selectHisTaskDataGrid(PageInfo pageInfo);

}
