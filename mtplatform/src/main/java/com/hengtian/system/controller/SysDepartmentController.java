package com.hengtian.system.controller;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.utils.AutoCreateCodeUtil;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.system.model.SysDepartment;
import com.hengtian.system.service.SysDepartmentService;


/**
 * <p>
 * 部门表 前端控制器
 * </p>
 * @author junyang.liu
 */
@Controller
@RequestMapping("/sysDepartment")
public class SysDepartmentController  extends BaseController{

    @Autowired
    private SysDepartmentService departmentService;

    /**
     * 部门管理主页
     * @return
     */
    @GetMapping(value = "/manager")
    public String manager() {
        return "system/department";
    }

    /**
     * 部门资源树
     * @return
     */
    
    @PostMapping(value = "/tree")
    @ResponseBody
    public Object tree() {
        return departmentService.selectTree();
    }

    /**
     * 部门列表
     * @return
     */
    @SysLog(value="查询部门列表")
    @RequestMapping("/treeGrid")
    @ResponseBody
    public Object treeGrid() {
        return departmentService.selectTreeGrid();
    }

    /**
     * 添加部门页
     * @return
     */
    @RequestMapping("/addPage")
    public String addPage() {
        return "system/departmentAdd";
    }

    /**
     * 添加部门
     * @param department
     * @return
     */
    @SysLog(value="添加部门")
    @RequestMapping("/add")
    @ResponseBody
    public Object add(SysDepartment department) {
    	EntityWrapper<SysDepartment> wrapper =new EntityWrapper<SysDepartment>();
        wrapper.isNotNull("department_code").orderBy("department_code", false);
        SysDepartment sysDepartment= departmentService.selectList(wrapper).get(0);
        String departmentCode = AutoCreateCodeUtil.autoCreateSysCode(ConstantUtils.prefixCode.NO.getValue(),sysDepartment.getDepartmentCode());
        department.setDepartmentCode(departmentCode);
        department.setCreateTime(new Date());
        departmentService.insert(department);
        return renderSuccess("添加成功！");
    }

    /**
     * 编辑部门页
     * @param request
     * @param id
     * @return
     */
    @GetMapping("/editPage")
    public String editPage(Model model, String id) {
    	SysDepartment department = departmentService.selectById(id);
        model.addAttribute("department", department);
        return "system/departmentEdit";
    }

    /**
     * 编辑部门
     * @param department
     * @return
     */
    @SysLog(value="编辑部门")
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(SysDepartment department) {
    	departmentService.updateById(department);
        return renderSuccess("编辑成功！");
    }

    /**
     * 删除部门
     * @param id
     * @return
     */
    @SysLog(value="删除部门")
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(String id) {
    	departmentService.deleteById(id);
        return renderSuccess("删除成功！");
    }
}
