package com.hengtian.common.result;

import com.hengtian.flow.model.TButton;
import org.activiti.engine.task.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ma on 2018/4/18.
 */
public class TaskNodeResult implements Serializable {

    private String taskId;

    private String taskDefinedKey;

    private String formKey;

    private String name;

    private String approver;

    private Integer assignType;

    private String processInstanceId;

    /**
     * 是否需要设置下一节点审批人
     */
    private Integer needSetNext;

    private List<TButton> buttons;

    public Integer getAssignType() {
        return assignType;
    }

    public void setAssignType(Integer assignType) {
        this.assignType = assignType;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskDefinedKey() {
        return taskDefinedKey;
    }

    public void setTaskDefinedKey(String taskDefinedKey) {
        this.taskDefinedKey = taskDefinedKey;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public List<TButton> getButtonKeys() {
        return buttons;
    }

    public void setButtonKeys(List<TButton> buttons) {
        this.buttons = buttons;
    }

    public Integer getNeedSetNext() {
        return needSetNext;
    }

    public void setNeedSetNext(Integer needSetNext) {
        this.needSetNext = needSetNext;
    }

    public List<TButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<TButton> buttons) {
        this.buttons = buttons;
    }

    /**
     * 将任务列表转换成返回出参任务列表
     *
     * @param list
     * @return
     */
    public  static   List<TaskNodeResult> toTaskNodeResultList(List<Task> list) {
        List<TaskNodeResult> nodeResults = new ArrayList<>();
        TaskNodeResult taskNodeResult;
        for (Task task : list) {
            taskNodeResult = toTaskNodeResult(task);
            nodeResults.add(taskNodeResult);
        }
        return nodeResults;
    }

    /**
     * 转换成出参任务
     *
     * @param task
     * @return
     */
    public  static TaskNodeResult toTaskNodeResult(Task task) {

        TaskNodeResult taskNodeResult = new TaskNodeResult();

        taskNodeResult.setTaskId(task.getId());
        taskNodeResult.setTaskDefinedKey(task.getTaskDefinitionKey());
        taskNodeResult.setFormKey(task.getFormKey());
        taskNodeResult.setName(task.getName());
        taskNodeResult.setProcessInstanceId(task.getProcessInstanceId());

        return taskNodeResult;
    }

}
