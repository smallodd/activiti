package com.hengtian.application.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.application.model.App;
import com.hengtian.application.vo.AppVo;

import java.util.List;

/**
 * 系统APP
 * @author houjinrong
 */
public interface AppService extends IService<App> {

	/**
	 * 查询系统应用
	 */
	List<AppVo> selectListGrid();

	/**
	 * 授权页面页面根据应用查询模型
	 * @return
	 */
	List<String> findModelKeyListByAppId(String id);

	/**
	 * 授权
	 * @param id
	 * @param modelKeys
	 * @return
	 */
	void updateAppModel(String appKey, String modelKeys);
}
