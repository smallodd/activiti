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
}
