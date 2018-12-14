package com.hengtian.flow.controller.manage;

import com.google.common.collect.Maps;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import com.rbac.entity.RbacRole;

import com.rbac.service.PrivilegeService;

import com.user.entity.org.Org;
import com.user.service.emp.EmpService;
import com.user.service.org.OrgService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 员工系统（通过dubbo服务从emp系统获取数据）
 *
 * @author houjinrong@chtwm.com
 * date 2018/5/7 14:07
 */
@Controller
@RequestMapping("/emp")
public class EmpController {

    Logger logger = Logger.getLogger(getClass());

    @Autowired
    private OrgService orgService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private EmpService empService;

    /**
     * 获取用户列表-分页
     *
     * @param code 员工编号
     * @param name 用户名
     * @param page 起始页
     * @param rows 每页条目数
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:10
     */
    @PostMapping("/user")
    @ResponseBody
    public Object queryUser(String code, String name, Integer page, Integer rows){
        code = StringUtils.isBlank(code)?null:code.trim();
        name = StringUtils.isBlank(name)?null:name.trim();
        Map<String,Object> map = empService.selectByDeptCodeOrCodeOrName(code,name,null,null,page,rows);
        PageInfo pageInfo = new PageInfo();
        Map<String,Object> condition = Maps.newHashMap();
        condition.put("code",code);
        condition.put("name",name);

        pageInfo.setTotal(Integer.valueOf(map.get("total").toString()));
        pageInfo.setCondition(condition);
        pageInfo.setRows((List) map.get("list"));
        pageInfo.setNowpage(page);
        pageInfo.setPagesize(rows);
        //transferPageInfo(userPageInfo, pageInfo, condition);

        return pageInfo;
    }

    /**
     * 获取部门列表-分页
     * @param code 部门编号
     * @param name 部门名称
     * @param page 起始页
     * @param rows 每页条目数
     * @return 分页数据
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:10
     */
    @PostMapping("/department")
    @ResponseBody
    public Object queryDepartment(String code, String name, Integer page, Integer rows){
        code = StringUtils.isBlank(code)?null:code.trim();
        name = StringUtils.isBlank(name)?null:name.trim();
        com.github.pagehelper.PageInfo<Org> deptPageInfo = orgService.getOrgPageInfo(page, rows, code, name);
        PageInfo pageInfo = new PageInfo();
        Map<String,Object> condition = Maps.newHashMap();
        condition.put("code",code);
        condition.put("name",name);
        transferPageInfo(deptPageInfo, pageInfo, condition);
        return pageInfo;
    }

    /**
     * 获取角色列表
     *
     * @param system 系统编号
     * @return 分页数据
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:10
     */
    @PostMapping("/role/{system}")
    @ResponseBody
    public Object queryRole(@PathVariable("system")Integer system){
        List<RbacRole> allRoleBySystem = privilegeService.getAllRoleBySystem(system);
        return allRoleBySystem;
    }

    private <T> void transferPageInfo(com.github.pagehelper.PageInfo<T> githubPageInfo, PageInfo pageInfo, Map<String,Object> condition){
        if(githubPageInfo != null){
            pageInfo.setTotal((int)githubPageInfo.getTotal());
            pageInfo.setRows(githubPageInfo.getList());
            pageInfo.setNowpage(githubPageInfo.getPageNum());
            pageInfo.setPagesize(githubPageInfo.getPageSize());
            pageInfo.setSize(githubPageInfo.getSize());
            pageInfo.setFrom(githubPageInfo.getStartRow());
            pageInfo.setCondition(condition);
        }
    }
}
