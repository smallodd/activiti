package com.hengtian.activiti.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.enums.TaskTypeEnum;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.ActivitiService;
import com.hengtian.flow.service.TUserTaskService;
import com.hengtian.flow.vo.ProcessDefinitionVo;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 功能描述: 审批人相关
 * @Author: houjinrong@chtwm.com
 * @Date: 2019/6/26 16:19
 */
@Controller
@RequestMapping("/tUserTask")
public class TUserTaskController extends BaseController{
    
    @Autowired 
    private TUserTaskService tUserTaskService;
    @Autowired 
    private ActivitiService activitiService;
    @Autowired
    private RepositoryService repositoryService;
    
   
    @GetMapping("/manager")
    public String manager() {
        return "activiti/tUserTask";
    }

   
    @GetMapping("/taskAssignee")
    public String taskAssignee() {
        return "activiti/tUserTaskAssignee";
    }
    
   
    @GetMapping("/taskCandidateUser")
    public String taskCandidateUser() {
        return "activiti/tUserTaskCandidateUser";
    }
    
   
    @GetMapping("/taskCandidateGroup")
    public String taskCandidateGroup() {
        return "activiti/tUserTaskCandidateGroup";
    }

   
    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(ProcessDefinitionVo processDefinitionVo, Integer page, Integer rows, String sort, String order, String key) {
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
     * 设定人员页面
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("/configUserPage")
    public String configUserPage(Model model, String id) {
    	ProcessDefinition pd= repositoryService.createProcessDefinitionQuery()
    			.processDefinitionId(id).singleResult();
    	EntityWrapper<TUserTask> wrapper =new EntityWrapper<TUserTask>();
		wrapper.where("proc_def_key = {0}", pd.getKey()).andNew("version_={0}",pd.getVersion());
		wrapper.orderBy("order_num",true);

		List<TUserTask> tasks= tUserTaskService.selectList(wrapper);
		BpmnModel bpmnModel = repositoryService.getBpmnModel(id);
		List<SequenceFlow> sequences = bpmnModel.getMainProcess().findFlowElementsOfType(SequenceFlow.class);
		List<UserTask> sequences1 = bpmnModel.getMainProcess().findFlowElementsOfType(UserTask.class);

		if(tasks==null || tasks.size()==0){
			for(UserTask ut : sequences1){
				TUserTask tUserTask = new TUserTask();
				tUserTask.setProcDefKey(pd.getKey());
				tUserTask.setProcDefName(pd.getName());
				tUserTask.setTaskDefKey(ut.getId());
				tUserTask.setTaskName(ut.getName()==null?"":ut.getName());
				tUserTask.setVersion(pd.getVersion());
				tUserTaskService.insert(tUserTask);
			}
			EntityWrapper<TUserTask> wrapper2 =new EntityWrapper<TUserTask>();
			wrapper2.where("proc_def_key = {0}", pd.getKey()).andNew("version_={0}",pd.getVersion());
			wrapper2.orderBy("order_num",true);
			List<TUserTask> tasks2= tUserTaskService.selectList(wrapper2);
			model.addAttribute("tasks", tasks2);
		}else{
			JSONArray array = new JSONArray();
			for(TUserTask userTask : tasks){
				JSONObject obj = new JSONObject();
				obj.put("id", userTask.getId());
				obj.put("key", userTask.getTaskDefKey());
				obj.put("type", userTask.getTaskType());
				obj.put("name", userTask.getCandidateName());
				obj.put("value", userTask.getCandidateIds());
				array.add(obj);
			}
			String taskJson=array.toJSONString().replaceAll("\"", "&quot;");
			model.addAttribute("taskJson", taskJson);
			model.addAttribute("tasks", tasks);
		}
        return "activiti/tUserTaskConfig";
    }

    /**
     * 配置用户
     * @param taskJson
     * @return
     */
    @RequestMapping("/configUser")
    @ResponseBody
    public Object configUser(String taskJson) {
    	String tasks =taskJson.replaceAll("&quot;", "'");
    	JSONArray array= JSONObject.parseArray(tasks);
    	Iterator<Object> it= array.iterator();
    	while(it.hasNext()){
    		JSONObject obj= (JSONObject)it.next();
    		String taskId= obj.get("id").toString();
    		TUserTask tUserTask= tUserTaskService.selectById(taskId);

			tUserTask.setTaskType(obj.get("type").toString());
    		if(TaskTypeEnum.COUNTERSIGN.value.equals(obj.get("type").toString())){
				if(obj.get("value").toString().split(",").length == 1){
					//会签时，任务节点审核人只有一个时转为普通任务
					tUserTask.setTaskType(TaskTypeEnum.ASSIGNEE.value);
				}
			}
    		tUserTask.setCandidateIds(obj.get("value").toString());
    		tUserTask.setCandidateName(obj.get("name").toString());
			tUserTask.setUserCountTotal(obj.get("name").toString().split(",").length);
			Integer userCountNeed = obj.getInteger("userCountNeed");
			if(userCountNeed == null){
				userCountNeed = 0;
			}
    		tUserTask.setUserCountNeed(userCountNeed);
    		boolean b = tUserTaskService.updateById(tUserTask);
    		if(!b){
    			return renderError("配置失败！");
    		}
    	}
    	return renderSuccess("配置成功！");
    }
    
}
