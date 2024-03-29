package com.hengtian.system.service;

import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.system.model.SysUser;
import com.hengtian.system.vo.SysUserVo;

import java.util.List;

/**
 * 用户表 服务类
 * @author houjinrong@chtwm.com
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
