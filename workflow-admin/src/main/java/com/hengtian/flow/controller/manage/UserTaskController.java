package com.hengtian.flow.controller.manage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hengtian.application.model.AppModel;
import com.hengtian.application.service.AppModelService;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.enums.AssignTypeEnum;
import com.hengtian.common.enums.TaskTypeEnum;
import com.hengtian.flow.model.TUserTask;
import com.hengtian.flow.service.TButtonService;
import com.hengtian.flow.service.TUserTaskService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
	@Autowired
	private AppModelService appModelService;

	/**
	 * 设定人员页面
	 * @param model
	 * @param processDefinitionId 流程定义ID
	 * @param type 设置方式：1-标准设置；2-快速设置（从历史中匹配）
	 * @return
	 * @author houjinrong@chtwm.com
	 * date 2018/5/7 15:47
	 */
	@GetMapping("/config/page/{processDefinitionId}")
	public String configPage(Model model, @PathVariable("processDefinitionId") String processDefinitionId, int type) {
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).latestVersion().singleResult();
		EntityWrapper<TUserTask> wrapper = new EntityWrapper();
		wrapper.where("proc_def_key = {0}", pd.getKey()).andNew("version_={0}",pd.getVersion());

		List<TUserTask> uTasks = tUserTaskService.selectList(wrapper);
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		List<UserTask> userTasks = bpmnModel.getMainProcess().findFlowElementsOfType(UserTask.class);

		if(CollectionUtils.isEmpty(uTasks)){
			Map<String, TUserTask> userTaskMap = Maps.newHashMap();
			if(2 == type){
				//获取历史最高版本的配置
				if(pd.getVersion() >1){
					wrapper = new EntityWrapper();
					wrapper.where("proc_def_key = {0}", pd.getKey()).andNew("version_={0}",pd.getVersion()-1);
					uTasks = tUserTaskService.selectList(wrapper);
				}

				if(CollectionUtils.isNotEmpty(uTasks)){
					for(TUserTask ut : uTasks){
						userTaskMap.put(ut.getTaskDefKey(), ut);
					}
				}
			}

			List<TUserTask> tUserTaskList = Lists.newArrayList();
			for(UserTask ut : userTasks){
				if(userTaskMap.containsKey(ut.getId())){
					userTaskMap.get(ut.getId()).setVersion(pd.getVersion());
					userTaskMap.get(ut.getId()).setId(null);
					tUserTaskList.add(userTaskMap.get(ut.getId()));
				}else{
					TUserTask tUserTask = new TUserTask();
					tUserTask.setProcDefKey(pd.getKey());
					tUserTask.setProcDefName(pd.getName());
					tUserTask.setTaskDefKey(ut.getId());
					tUserTask.setTaskName(ut.getName()==null?"":ut.getName());
					tUserTask.setVersion(pd.getVersion());
					tUserTask.setTaskType(TaskTypeEnum.ASSIGNEE.getValue());
					tUserTask.setAssignType(AssignTypeEnum.PERSON.getCode());
					tUserTaskList.add(tUserTask);
				}
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

			model.addAttribute("taskType", TaskTypeEnum.getTaskTypeMap());
			model.addAttribute("assignType", AssignTypeEnum.getAssignpeMap());
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
    public String selectRolePage(Model model, String procDefKey) {
    	if(StringUtils.isNotBlank(procDefKey)){
    		EntityWrapper<AppModel> wrapper = new EntityWrapper<>();
    		wrapper.eq("model_key", procDefKey);
			AppModel appModel = appModelService.selectOne(wrapper);
			if(appModel != null){
				model.addAttribute("appKey", appModel.getAppKey());
			}
		}

        return "workflow/config/select_role";
    }

    /**
     * 任务节点配置（包括审批人，权限按钮）
     * @param taskJson
     * @return
     */
    @PostMapping("/config")
    @ResponseBody
    public Object config(String taskJson) {
		try {
			return tUserTaskService.config(taskJson);
		} catch (Exception e) {
			logger.error("任务节点配置失败", e);
			return renderError("任务节点配置");
		}
	}

	/**
	 * 任务节点配置（包括审批人，权限按钮）
	 * @param processDefinitionId
	 * @return
	 */
	@PostMapping("/config/type")
	@ResponseBody
	public Object configType(String processDefinitionId) {
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).latestVersion().singleResult();
		EntityWrapper<TUserTask> wrapper = new EntityWrapper();
		wrapper.where("proc_def_key = {0}", pd.getKey()).andNew("version_={0}",pd.getVersion());
		int i = tUserTaskService.selectCount(wrapper);
		return i;
	}
}
