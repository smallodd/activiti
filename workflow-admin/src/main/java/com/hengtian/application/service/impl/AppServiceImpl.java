package com.hengtian.application.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.application.dao.AppDao;
import com.hengtian.application.model.App;
import com.hengtian.application.service.AppService;
import com.hengtian.application.vo.AppVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统APP
 * @author houjinrong
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppDao, App> implements AppService {

    @Autowired private AppDao appDao;

	/**
	 * 查询系统应用
	 */
	@Override
	public List<AppVo> selectListGrid(){
		//EntityWrapper<App> wrapper = new EntityWrapper<App>();
		//wrapper.orderBy("update_time", false);
		//return appDao.selectList(wrapper);

		return appDao.selectAppList();
	}

	/**
	 * 授权页面页面根据应用查询模型
	 * @return
	 */
	@Override
	public List<String> findModelKeyListByAppId(String id){
		return appDao.findModelKeyListByAppId(id);
	}
}
