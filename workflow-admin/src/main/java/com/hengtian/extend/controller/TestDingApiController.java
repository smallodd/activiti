package com.hengtian.extend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hengtian.system.model.SysUser;
import com.hengtian.system.service.SysResourceService;
import com.hengtian.system.service.SysUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/dingapi")
@Api(value="测试接口")
public class TestDingApiController {
	@Autowired
    private SysUserService sysUserService;
	@Autowired
    private SysResourceService resourceService;
	
	/**
	 * 用户信息查询
	 */
	@RequestMapping(value="/loginName/{name}",method=RequestMethod.GET)
	@ApiOperation(value="用户信息查询",notes="根据登录名进行查询")
    @ResponseBody
    public Object selectByLoginName(@PathVariable String name){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("login_name", name);
		List<SysUser> list = sysUserService.selectByMap(map);
		return list;
	}
    
	
	/**
     * 资源管理列表
     */
    @PostMapping("/treeGrid")
    @ApiOperation(value="资源管理列表",notes="资源管理列表")
    @ResponseBody
    public Object treeGrid() {
        return resourceService.selectAll();
    }
    
    
    /**
     * 测试Vue数据接口
     */
    @RequestMapping("/data")
    @ResponseBody
    public Object fetchData(){
    	List<TData> datas = new ArrayList<TData>();
    	
    	TData data1 = new TData();
    	data1.setDate("2016-05-02");
    	data1.setAddress("上海市普陀区金沙江路 1518 弄");
    	data1.setName("王小虎");
    	datas.add(data1);
    	
    	TData data2 = new TData();
    	data2.setDate("2016-05-03");
    	data2.setAddress("上海市普陀区金沙江路 1518 弄");
    	data2.setName("王小虎");
    	datas.add(data2);
    	
    	TData data3 = new TData();
    	data3.setDate("2016-05-04");
    	data3.setAddress("上海市普陀区金沙江路 1518 弄");
    	data3.setName("王小虎");
    	datas.add(data3);
    	
    	return datas;
    }
	

}
