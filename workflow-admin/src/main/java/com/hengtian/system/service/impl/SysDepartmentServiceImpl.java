package com.hengtian.system.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.common.result.Tree;
import com.hengtian.system.dao.SysDepartmentDao;
import com.hengtian.system.model.SysDepartment;
import com.hengtian.system.service.SysDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author houjinrong@chtwm.com
 * @since 2017-08-09
 */
@Service
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentDao, SysDepartment> implements SysDepartmentService {

	@Autowired
	private SysDepartmentDao sysDepartmentDao;
	
	@Override
	public List<Tree> selectTree() {
        List<SysDepartment> departmentList = selectTreeGrid();

        List<Tree> trees = new ArrayList<Tree>();
        if (departmentList != null) {
            for (SysDepartment department : departmentList) {
                Tree tree = new Tree();
                tree.setId(department.getId());
                tree.setText(department.getDepartmentName());
                tree.setIconCls(department.getDepartmentIcon());
                tree.setPid(department.getParentId());
                trees.add(tree);
            }
        }
        return trees;
    }

	@Override
	public List<SysDepartment> selectTreeGrid() {
        EntityWrapper<SysDepartment> wrapper = new EntityWrapper<SysDepartment>();
        wrapper.orderBy("sequence", true);
        return sysDepartmentDao.selectList(wrapper);
    }
	
}
