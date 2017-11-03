package com.hengtian.application.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.activiti.vo.CommonVo;
import com.hengtian.application.dao.TVacationDao;
import com.hengtian.application.model.TVacation;
import com.hengtian.application.service.TVacationService;
import com.hengtian.common.shiro.ShiroUser;
import com.hengtian.common.utils.AutoCreateCodeUtil;
import com.hengtian.common.utils.ConstantUtils;
import com.hengtian.common.utils.DateUtils;

/**
 * <p>
 * 请假表  服务实现类
 * </p>
 * @author junyang.liu
 * @since 2017-08-18
 */
@Service
public class TVacationServiceImpl extends ServiceImpl<TVacationDao, TVacation> implements TVacationService {
    @Autowired private TVacationDao tVacationDao;
    @Autowired private RuntimeService runtimeService;
    @Autowired private TaskService taskService;
    @Autowired private IdentityService identityService;
    
	@Override
	public void startVacation(TVacation tVacation) {
		//请假单号字段赋值
		EntityWrapper<TVacation> wrapper =new EntityWrapper<TVacation>();
        wrapper.isNotNull("vacation_code").orderBy("vacation_code", false);
        TVacation mvacation= this.selectList(wrapper).get(0);
        String vacationCode = AutoCreateCodeUtil.autoCreateSysCode(ConstantUtils.prefixCode.SN.getValue(),mvacation.getVacationCode());
        tVacation.setVacationCode(vacationCode);
		//获取Shiro中的用户信息
    	ShiroUser shiroUser= (ShiroUser)SecurityUtils.getSubject().getPrincipal();
		identityService.setAuthenticatedUserId(shiroUser.getId());
		//1.新增请假
    	tVacation.setApplyDate(new Date());
    	
    	tVacation.setUserId(shiroUser.getId());
    	tVacation.setVacationStatus(ConstantUtils.vacationStatus.APPROVING.getValue());
        tVacationDao.insert(tVacation);
        //2.添加流程变量
        CommonVo vo = new CommonVo();
    	vo.setBusinessType(ConstantUtils.VACATION);
    	vo.setBusinessKey(ConstantUtils.VACATION);
    	vo.setApplyUserId(shiroUser.getId());
    	vo.setApplyUserName(shiroUser.getName());
    	vo.setApplyTitle(shiroUser.getName()+"于 "+DateUtils.formatDateToString(tVacation.getApplyDate())+" 的请假申请，请假单号为:"+vacationCode);
    	Map<String,Object> variables=new HashMap<String,Object>();
		variables.put(ConstantUtils.MODEL_KEY, vo);
		//设置流程变量请假天数
		variables.put("days", tVacation.getWorkDays());
		//3.启动流程实例
		ProcessInstance processInstance= runtimeService.startProcessInstanceByKey(ConstantUtils.VACATION,tVacation.getId(),variables);
		tVacation.setProcInstId(processInstance.getId());
		tVacationDao.updateById(tVacation);
		//启动完流程之后设置为null
		identityService.setAuthenticatedUserId(null);
	}

	@Override
	public void modifyTask(TVacation vacation, String taskId, String commentResult) {
		//添加批注数据
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    	String processInstanceId = task.getProcessInstanceId();
    	ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    	ShiroUser shiroUser= (ShiroUser)SecurityUtils.getSubject().getPrincipal();
    	identityService.setAuthenticatedUserId(shiroUser.getId());
		//更新请假数据
		tVacationDao.updateById(vacation);
		//完成当前任务
		Map<String,Object> variables = new HashMap<String,Object>();
		if("continue".equals(commentResult)){
    		taskService.addComment(task.getId(), processInstance.getId(),"continue", "发起人重新申请");
    		variables.put("isPass", true);
    	}else if("stop".equals(commentResult)){
    		taskService.addComment(task.getId(), processInstance.getId(),"stop", "发起人结束流程");
    		variables.put("isPass", false);
    	}
		taskService.complete(taskId, variables);
	}

	@Override
	public void terminateTask(TVacation vacation, String taskId) {
		//添加批注数据
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    	String processInstanceId = task.getProcessInstanceId();
    	ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    	ShiroUser shiroUser= (ShiroUser)SecurityUtils.getSubject().getPrincipal();
    	identityService.setAuthenticatedUserId(shiroUser.getId());
    	taskService.addComment(task.getId(), processInstance.getId(),"terminate", "发起人销假结束");
		//更新请假数据
		TVacation tvacation = tVacationDao.selectById(vacation.getId());
		tvacation.setBeginDate(vacation.getBeginDate());
		tvacation.setEndDate(vacation.getEndDate());
		tVacationDao.updateById(tvacation);
		//完成当前任务
		taskService.complete(taskId);
	}

    
    
}
