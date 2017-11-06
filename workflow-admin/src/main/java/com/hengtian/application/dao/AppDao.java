package com.hengtian.application.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hengtian.application.model.App;
import com.hengtian.application.vo.AppVo;

import java.util.List;

/**
 * 系统APP
 * @author houjinrong
 */
public interface AppDao extends BaseMapper<App> {

    List<AppVo> selectAppList();
}