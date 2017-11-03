package com.hengtian.system.service;

import java.util.List;
import com.baomidou.mybatisplus.service.IService;
import com.hengtian.common.result.Tree;
import com.hengtian.system.model.SysDepartment;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author junyang.liu
 * @since 2017-08-09
 */
public interface SysDepartmentService extends IService<SysDepartment> {
	
	List<Tree> selectTree();

    List<SysDepartment> selectTreeGrid();
}
