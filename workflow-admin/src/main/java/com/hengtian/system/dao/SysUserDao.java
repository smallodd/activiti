package com.hengtian.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hengtian.system.model.SysUser;
import com.hengtian.system.vo.SysUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * <p>
  * 用户表Dao 接口
 * </p>
 * @author junyang.liu
 */
public interface SysUserDao extends BaseMapper<SysUser> {
	
	SysUserVo selectSysUserVoById(@Param("id") String id);

    List<SysUserVo> selectSysUserVoPage(Pagination page, Map<String, Object> params);

    List<SysUserVo> selectSysUserByPage(Page<SysUserVo> page, Map<String, Object> condition);
}