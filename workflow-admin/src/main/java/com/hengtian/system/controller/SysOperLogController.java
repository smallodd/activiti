package com.hengtian.system.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.system.model.SysOperLog;
import com.hengtian.system.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * <p>
 * 系统操作日志  前端控制器
 * </p>
 * @author junyang.liu
 */
@Controller
@RequestMapping("/sysOperLog")
public class SysOperLogController extends BaseController {
    
    @Autowired
    private SysOperLogService sysOperLogService;
    
    @GetMapping("/manager")
    public String manager() {
        return "system/sysOperLog";
    }

    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(SysOperLog sysOperLog, Integer page, Integer rows, String sort,String order) {
        EntityWrapper<SysOperLog> wrapper = new EntityWrapper<SysOperLog>();
        if(StringUtils.isNotBlank(sysOperLog.getOperUserName())){
        	wrapper.where("oper_user_name = {0}", sysOperLog.getOperUserName());
        }
        if(sysOperLog.getOperStatus()!=null && sysOperLog.getOperStatus()!= 0){
        	wrapper.where("oper_status = {0}", sysOperLog.getOperStatus());
        }
        Page<SysOperLog> pages = getPage(page, rows, sort, order);
        pages = sysOperLogService.selectPage(pages,wrapper);
        return pageToPageInfo(pages);
    }
}
