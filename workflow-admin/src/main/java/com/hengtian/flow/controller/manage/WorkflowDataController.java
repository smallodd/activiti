package com.hengtian.flow.controller.manage;

import com.hengtian.common.param.TaskQueryParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.service.ActivitiService;
import com.hengtian.flow.service.WorkflowService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流程相关-数据查询
 * @author houjinrong@chtwm.com
 * date 2018/5/9 17:41
 */
@RestController
@RequestMapping("/workflow/data")
public class WorkflowDataController {

    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private WorkflowService workflowService;

    /**
     * 流程定义列表-分页
     * @param page
     * @param rows
     * @param key
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:53
     */
    @PostMapping("/processDef")
    @ResponseBody
    public PageInfo processDef(Integer page, Integer rows,String key) {
        PageInfo pageInfo = new PageInfo(page, rows);
        Map<String,Object> params = new HashMap<String,Object>();
        if(StringUtils.isNotBlank(key)){
            params.put("key",key.trim());
        }
        pageInfo.setCondition(params);
        activitiService.selectProcessDefinitionDataGrid(pageInfo);
        return pageInfo;
    }

    /**
     * 代办任务列表-页面
     * @param taskQueryParam
     * @author houjinrong@chtwm.com
     * date 2018/5/10 13:29
     */
    @PostMapping("/task")
    @ResponseBody
    public Object task(TaskQueryParam taskQueryParam,Integer page, Integer rows){
        taskQueryParam.setPageNum(page);
        taskQueryParam.setPageSize(rows);
        PageInfo pageInfo = workflowService.activeTaskList(taskQueryParam);
        return pageInfo;
    }
}
