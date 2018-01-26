package com.hengtian.application.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.application.model.TVacation;

/**
 * <p>
 * 请假表  服务类
 * </p>
 *
 * @author junyang.liu
 * @since 2017-08-18
 */
public interface TVacationService extends IService<TVacation> {

	/**
	 * 启动请假流程
	 * @param tVacation
	 */
	void startVacation(TVacation tVacation);

	/**
	 * 调整请假申请
	 * @param tVacation
	 */
	void modifyTask(TVacation vacation, String taskId, String commentResult);

	/**
	 * 销假任务
	 * @param vacation
	 * @param taskId
	 */
	void terminateTask(TVacation vacation, String taskId);
	
	

}
