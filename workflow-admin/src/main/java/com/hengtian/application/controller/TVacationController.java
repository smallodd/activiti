package com.hengtian.application.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hengtian.activiti.service.ActivitiService;
import com.hengtian.activiti.vo.CommentVo;
import com.hengtian.application.model.TVacation;
import com.hengtian.application.service.TVacationService;
import com.hengtian.common.base.BaseController;
import com.hengtian.common.operlog.SysLog;
import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.common.utils.DateUtils;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.system.model.SysUser;
import com.hengtian.system.service.SysUserService;

/**
 * <p>
 * 请假表  前端控制器
 * </p>
 * @author junyang.liu
 */
@Controller
@RequestMapping("/tVacation")
public class TVacationController extends BaseController{
    
    @Autowired
	private TaskService taskService;
	@Autowired 
	private TVacationService tVacationService;
	@Autowired
    private SysUserService sysUserService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ActivitiService activitiService;
	
    /**
     * 请假列表页面
     * @return
     */
    @GetMapping("/manager")
    public String manager() {
        return "application/tVacation";
    }


    /**
     * 请假列表
     * @param tVacation
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @return
     */
    @SysLog
    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(TVacation tVacation, Integer page, Integer rows, String sort,String order) {
        EntityWrapper<TVacation> ew = new EntityWrapper<TVacation>();
        ShiroUser user= getShiroUser();
        tVacation.setUserId(user.getId());
        ew.setEntity(tVacation);
        Page<TVacation> pages = getPage(page, rows, sort, order);
        pages = tVacationService.selectPage(pages,ew);
        return pageToPageInfo(pages);
    }
    
    /**
     * 添加页面
     * @return
     */
    @GetMapping("/addPage")
    public String addPage() {
        return "application/tVacationAdd";
    }

    /**
     * 添加
     * @param 
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Object add(TVacation tVacation) {
        tVacationService.startVacation(tVacation);
    	return renderSuccess("申请成功！");
    }
    
    /**
     * 查看审批进度
     */
    @GetMapping("/tVacationGetCommentsPage")
    public String tVacationGetCommentsPage(Model model, String id) {
		TVacation vacation= tVacationService.selectById(id);
		List<CommentVo> comments = new ArrayList<CommentVo>();
		List<Comment> commentList= taskService.getProcessInstanceComments(vacation.getProcInstId());
		for(Comment comment : commentList){
			CommentVo vo = new CommentVo();
			SysUser user= sysUserService.selectById(comment.getUserId());
			vo.setCommentUser(user.getUserName());
			vo.setCommentTime(DateUtils.formatDateToString(comment.getTime()));
			vo.setCommentContent(comment.getFullMessage());
			HistoricTaskInstance his= historyService.createHistoricTaskInstanceQuery().taskId(comment.getTaskId()).singleResult();
			vo.setCommentTask(his.getName());
			if(ConstantUtils.vacationStatus.PASSED.getValue().toString().equals(comment.getType())){
				vo.setCommentResult("同意");
			}else if(ConstantUtils.vacationStatus.NOT_PASSED.getValue().toString().equals(comment.getType())){
				vo.setCommentResult("不同意");
			}else if("continue".equals(comment.getType())){
				vo.setCommentResult("重新申请");
			}else if("stop".equals(comment.getType())){
				vo.setCommentResult("结束流程");
			}else if("terminate".equals(comment.getType())){
				vo.setCommentResult("销假结束");
			}
			comments.add(vo);
		}
		model.addAttribute("comments", comments);
        return "application/tVacationGetComments";
    }
    
    /**
     * 查看流程图
     */
    @GetMapping("/tVacationGetProcessImage")
    public void tVacationGetProcessImage(String id,HttpServletResponse response){
    	try {
			TVacation tvacation = tVacationService.selectById(id);
			String procInsId = tvacation.getProcInstId();
			ProcessInstance ins = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId)
					.singleResult();
			if(ins==null){
				String processDefinitionId=historyService.createHistoricProcessInstanceQuery()
						.processInstanceId(procInsId).singleResult().getProcessDefinitionId();
				InputStream in = activitiService.getProcessResource("image", processDefinitionId);
				byte[] b = new byte[1024];
				int len = -1;
				while ((len = in.read(b, 0, 1024)) != -1) {
				    response.getOutputStream().write(b, 0, len);
				}
			}else{
				BpmnModel bpmnModel = repositoryService.getBpmnModel(ins.getProcessDefinitionId());
				List<String> highLightedActivities = runtimeService.getActiveActivityIds(procInsId);
				ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
				InputStream in = processDiagramGenerator.generateDiagram(bpmnModel,"png", highLightedActivities,
						new ArrayList<String>(),"宋体","宋体","宋体",null,1.0D);
				byte[] b = new byte[1024];
				int len = -1;
				while ((len = in.read(b, 0, 1024)) != -1) {
					response.getOutputStream().write(b, 0, len);
				} 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 调整请假申请
     */
    @SysLog(value="调整请假申请")
    @RequestMapping("/modifyTask")
    @ResponseBody
    public Object modifyTask(TVacation vacation,@RequestParam("taskId") String taskId,
    		@RequestParam("commentResult") String commentResult){
    	tVacationService.modifyTask(vacation,taskId,commentResult);
    	return renderSuccess("调整成功!");
    }
    
    /**
     * 销假任务
     */
    @SysLog(value="销假任务")
    @RequestMapping("/terminateTask")
    @ResponseBody
    public Object terminateTask(TVacation vacation,@RequestParam("taskId") String taskId){
    	tVacationService.terminateTask(vacation,taskId);
    	return renderSuccess("销假成功!");
    }
    
}
