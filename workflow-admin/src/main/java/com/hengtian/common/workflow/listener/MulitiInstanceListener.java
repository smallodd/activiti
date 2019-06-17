package com.hengtian.common.workflow.listener;

import com.hengtian.common.utils.ConstantUtils;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;


/**
 * 多实例通过条件监听器
 * @author liujunyang
 */
@Component("mulitiInstanceListener")
public class MulitiInstanceListener implements ExecutionListener,Serializable{
	private static final long serialVersionUID = 1L;
	@Autowired
	private RuntimeService runtimeService;


	@Override
	public void notify(DelegateExecution execution) throws Exception {
		//总实例数量
		double nrOfInstances= Double.parseDouble(String.valueOf(execution.getVariable("nrOfInstances")));
		boolean isPass= Boolean.parseBoolean(execution.getVariable("isPass").toString());
		Integer passCount= (Integer) runtimeService.getVariable(execution.getProcessInstanceId(), "passCount");
		if(passCount==null){
			if(isPass){
				runtimeService.setVariable(execution.getProcessInstanceId(), "passCount", 1);
			}else{
				runtimeService.setVariable(execution.getProcessInstanceId(), "passCount", 0);
			}
		}else{
			if(isPass){
				runtimeService.setVariable(execution.getProcessInstanceId(), "passCount", passCount+1);
			}
		}
		//通过的数量
		int passNum= (int) runtimeService.getVariable(execution.getProcessInstanceId(), "passCount");
		

		//判断会签是否通过
		runtimeService.setVariable(execution.getProcessInstanceId(), "completeCondition", false);
		
		//当前活动的实例
		int nrOfActiveInstances= Integer.parseInt(String.valueOf(execution.getVariable("nrOfActiveInstances")));
		if(nrOfActiveInstances>0){
			if(passNum/nrOfInstances>=ConstantUtils.PERCENT){
				//更新请假业务


	        	runtimeService.setVariable(execution.getProcessInstanceId(), "completeCondition", true);
			}
		}else{
			if(passNum/nrOfInstances<ConstantUtils.PERCENT){
				//更新请假业务


			}
		}
	}
}
