package com.hengtian.system.service;

import java.util.List;
import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.result.Tree;
import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.system.model.SysResource;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author junyang.liu
 * @since 2017-08-09
 */
public interface SysResourceService extends IService<SysResource> {
	
	List<SysResource> selectAll();

    List<Tree> selectAllMenu();

    List<Tree> selectAllTree();

    List<Tree> selectTree(ShiroUser shiroUser);
}
