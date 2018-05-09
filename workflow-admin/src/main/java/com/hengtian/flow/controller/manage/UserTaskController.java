package com.hengtian.flow.controller.manage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.enums.AssignType;
import com.hengtian.common.enums.TaskType;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.TButtonService;
import com.hengtian.flow.service.TUserTaskService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;
import java.util.List;

/**
 * 用户任务表  前端控制器
 * @author houjinrong@chtwm.com
 * date 2018/5/7 13:23
 */
@Controller
@RequestMapping("/assignee")
public class UserTaskController extends BaseController{

	Logger logger = Logger.getLogger(getClass());
    
    @Autowired 
    private TUserTaskService tUserTaskService;
    @Autowired
    private RepositoryService repositoryService;
	@Autowired
	private TButtonService tButtonService;

	/**
	 * 设定人员页面
	 * @param model
	 * @param id 流程定义ID
	 * @return
	 * @author houjinrong@chtwm.com
	 * date 2018/5/7 15:47
	 */
	@RequestMapping("/config/page/{id}")
	public String configPage(Model model, @PathVariable("id") String id) {
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
		EntityWrapper<TUserTask> wrapper = new EntityWrapper();
		wrapper.where("proc_def_key = {0}", pd.getKey()).andNew("version_={0}",pd.getVersion());

		List<TUserTask> uTasks = tUserTaskService.selectList(wrapper);
		BpmnModel bpmnModel = repositoryService.getBpmnModel(id);
		List<UserTask> userTasks = bpmnModel.getMainProcess().findFlowElementsOfType(UserTask.class);

		if(uTasks == null || uTasks.size() == 0){
			List<TUserTask> tUserTaskList = Lists.newArrayList();
			for(UserTask ut : userTasks){
				TUserTask tUserTask = new TUserTask();
				tUserTask.setProcDefKey(pd.getKey());
				tUserTask.setProcDefName(pd.getName());
				tUserTask.setTaskDefKey(ut.getId());
				tUserTask.setTaskName(ut.getName()==null?"":ut.getName());
				tUserTask.setVersion(pd.getVersion());
				tUserTaskList.add(tUserTask);
			}

			if(CollectionUtils.isNotEmpty(tUserTaskList)){
				tUserTaskService.insertBatch(tUserTaskList);
				wrapper = new EntityWrapper();
				wrapper.where("proc_def_key = {0}", pd.getKey()).andNew("version_={0}",pd.getVersion());
				uTasks = tUserTaskService.selectList(wrapper);
			}
		}
		if(CollectionUtils.isNotEmpty(uTasks)){
			JSONObject configButton = tButtonService.getConfigButton(pd.getKey());
			JSONArray array = new JSONArray();
			for(TUserTask ut : uTasks){
				JSONObject obj = new JSONObject();
				obj.put("id", ut.getId());
				obj.put("procDefKey", ut.getProcDefKey());
				obj.put("taskDefKey", ut.getTaskDefKey());
				obj.put("taskType", ut.getTaskType());
				obj.put("assignType", ut.getAssignType());
				obj.put("name", ut.getCandidateName());
				obj.put("code", ut.getCandidateIds());
				if(configButton != null && configButton.containsKey(ut.getTaskDefKey())){
					obj.put("buttonKey",configButton.getJSONObject(ut.getTaskDefKey()).getString("buttonKey"));
					obj.put("buttonName",configButton.getJSONObject(ut.getTaskDefKey()).getString("buttonName"));
				}

				array.add(obj);
			}
			String taskJson = array.toJSONString().replaceAll("\"", "&quot;");
			model.addAttribute("taskJson", taskJson);
			model.addAttribute("uTasks", uTasks);

			model.addAttribute("taskType",TaskType.getTaskTypeMap());
			model.addAttribute("assignType", AssignType.getAssignpeMap());
		}

		return "workflow/config/config_task";
	}

	/**
	 * 选择员工页面
	 * @author houjinrong@chtwm.com
	 * date 2018/5/7 15:49
	 */
    @GetMapping("/select/user")
    public String selectUserPage() {
        return "workflow/config/select_user";
    }

    /**
     * 选择部门页面
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:49
     */
    @GetMapping("/select/department")
    public String selectDepartmentPage() {
        return "workflow/config/select_department";
    }

    /**
     * 选择角色页面
     * @author houjinrong@chtwm.com
     * date 2018/5/7 15:50
     */
    @GetMapping("/select/role")
    public String selectRolePage() {
        return "workflow/config/select_role";
    }

    /**
     * 任务节点配置（包括审批人，权限按钮）
     * @param taskJson
     * @return
     */
    @RequestMapping("/config")
    @ResponseBody
    public Object config(String taskJson) {
		try {
			return tUserTaskService.config(taskJson);
		} catch (Exception e) {
			logger.error("任务节点配置失败", e);
			return renderError("任务节点配置");
		}
	}
}
