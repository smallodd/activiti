package com.hengtian.application.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.application.dao.AppDao;
import com.hengtian.application.dao.AppModelDao;
import com.hengtian.application.model.App;
import com.hengtian.application.model.AppModel;
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

	@Autowired private AppModelDao appModelDao;

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

	@Override
	public void updateAppModel(String appKey, String modelKeys){
		AppModel appModel = new AppModel();
		appModel.setAppKey(appKey);
		EntityWrapper<AppModel> wrapper = new EntityWrapper<AppModel>(appModel);
		wrapper.isNotNull("app_key");
		appModelDao.delete(wrapper);
		String[] modelKeyArray = modelKeys.split(",");
		for (String modelKey : modelKeyArray) {
			appModel.setModelKey(modelKey);
			appModelDao.insert(appModel);
		}
	}
}
