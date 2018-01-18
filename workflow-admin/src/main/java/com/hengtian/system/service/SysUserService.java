package com.hengtian.system.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.system.model.SysUser;
import com.hengtian.system.vo.SysUserVo;

/**
 * <p>
 * 用户表 服务类
 * </p>
 * @author junyang.liu
 */
public interface SysUserService extends IService<SysUser> {
	
	List<SysUser> selectByLoginName(SysUserVo userVo);

    void insertByVo(SysUserVo userVo);

    SysUserVo selectVoById(String id);

    void updateByVo(SysUserVo userVo);

    void updatePwdByUserId(String userId, String md5Hex);

    void selectDataGrid(PageInfo pageInfo);

    void deleteUserById(String id);

    void selectDataGridAlert(PageInfo pageInfo);
}
