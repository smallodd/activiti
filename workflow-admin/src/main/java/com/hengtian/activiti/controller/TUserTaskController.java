package com.hengtian.activiti.controller;

import java.util.Iterator;
import java.util.List;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hengtian.activiti.model.TUserTask;
import com.hengtian.activiti.service.ActivitiService;
import com.hengtian.activiti.service.TUserTaskService;
import com.hengtian.activiti.vo.ProcessDefinitionVo;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.utils.PageInfo;


/**
 * <p>
 * 用户任务表  前端控制器
 * </p>
 * @author junyang.liu
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
    public PageInfo dataGrid(ProcessDefinitionVo processDefinitionVo, Integer page, Integer rows, String sort,String order) {
    	PageInfo pageInfo = new PageInfo(page, rows);
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

		if(tasks==null || tasks.size()==0){
			ProcessDefinitionEntity pde= (ProcessDefinitionEntity)repositoryService.getProcessDefinition(pd.getId());
			List<ActivityImpl> list= pde.getActivities();
			int orderNum = 1;
			for(ActivityImpl activity : list){
				ActivityBehavior activityBehavior = activity.getActivityBehavior();
				//是否为用户任务
				if(activityBehavior instanceof UserTaskActivityBehavior){
					UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
					TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();
					if("SVacation_Modify".equals(taskDefinition.getKey()) || "SVacation_Terminate".equals(taskDefinition.getKey())){
						continue;
					}
		            TUserTask tUserTask = new TUserTask();
					tUserTask.setProcDefKey(pd.getKey());
					tUserTask.setProcDefName(pd.getName());
					tUserTask.setTaskDefKey(taskDefinition.getKey());
					tUserTask.setTaskName(taskDefinition.getNameExpression()==null?null:taskDefinition.getNameExpression().toString());
					tUserTask.setOrderNum(orderNum++);
					tUserTask.setVersion(pd.getVersion());
					tUserTaskService.insert(tUserTask);
				}else if(activityBehavior instanceof ParallelMultiInstanceBehavior){
		            TUserTask tUserTask = new TUserTask();
					tUserTask.setProcDefKey(pd.getKey());
					tUserTask.setProcDefName(pd.getName());
					UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) ((MultiInstanceActivityBehavior) activityBehavior)
							.getInnerActivityBehavior();
					TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();
					tUserTask.setTaskDefKey(taskDefinition.getKey());
					tUserTask.setTaskName(taskDefinition.getNameExpression().toString());
					tUserTask.setOrderNum(orderNum++);
					tUserTask.setVersion(pd.getVersion());
					tUserTaskService.insert(tUserTask);
				}
			}
			EntityWrapper<TUserTask> wrapper2 =new EntityWrapper<TUserTask>();
			wrapper2.where("proc_def_key = {0}", pd.getKey()).andNew("version_={0}",pd.getVersion());
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
			if(StringUtils.isNotBlank(tasks.get(0).getCandidateIds())){
				model.addAttribute("taskJson", taskJson);
			}else{
				model.addAttribute("taskJson", "");
			}
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
    		tUserTask.setCandidateIds(obj.get("value").toString());
    		tUserTask.setCandidateName(obj.get("name").toString());
    		boolean b = tUserTaskService.updateById(tUserTask);
    		if(!b){
    			return renderError("配置失败！");
    		}
    	}
    	return renderSuccess("配置成功！");
    }
    
}
