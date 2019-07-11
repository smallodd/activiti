package com.hengtian.application.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.application.dao.AppDao;
import com.hengtian.application.dao.AppModelDao;
import com.hengtian.application.model.App;
import com.hengtian.application.model.AppModel;
import com.hengtian.application.service.AppService;
import com.hengtian.application.vo.AppVo;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统APP
 * @author houjinrong
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppDao, App> implements AppService {

    @Autowired private AppDao appDao;

	@Autowired private AppModelDao appModelDao;
	@Autowired
	private RepositoryService repositoryService;
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
	public Map findModelKeyListByAppId(String id){
		List<String> list=appDao.findModelKeyListByAppId(id);

		Map map=new HashMap();
		if(list!=null&&list.size()>0) {
			for (String key : list) {
				Model model=repositoryService.createModelQuery().deployed().modelKey(key).singleResult();
				if (model == null) {
					map.put(key, false);
				} else {
					map.put(key, true);
				}
			}
		}
		return map;
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
