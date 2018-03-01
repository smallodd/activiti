import com.activiti.common.EmailUtil;
import com.activiti.entity.ApproveVo;
import com.activiti.entity.CommonVo;
import com.activiti.entity.HistoryTasksVo;
import com.activiti.entity.TaskQueryEntity;
import com.activiti.expection.WorkFlowException;
import com.activiti.service.WorkTaskV2Service;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Model;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author houjinrong@chtwm.com
 * date 2018/2/5 16:49
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:dubbo-server-consumer.xml"})
public class TestWorkFlowV2 {
    ApplicationContext act;
    WorkTaskV2Service workTaskV2Service;

    @Before
    public void testBefore(){
        act=new ClassPathXmlApplicationContext("dubbo-server-consumer.xml");
         workTaskV2Service= (WorkTaskV2Service) act.getBean("workTaskV2Service");
    }

    //开启任务
    @Test
    public void testStart() {

        CommonVo commonVo=new CommonVo();
        commonVo.setApplyTitle("测试动态任务");
        commonVo.setApplyUserId("H000000");
        commonVo.setApplyUserName("mayl");
        commonVo.setBusinessKey("0001");
        commonVo.setBusinessType("activity");
        commonVo.setModelKey("hour");
        commonVo.setDynamic(false);
        Map map=new HashMap();
        map.put("param",10000);
        String processId= null;
        try {
            processId = workTaskV2Service.startTask(commonVo,map);
            //workTaskV2Service.setApprove(processId,null);
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
        System.out.println("返回结果为："+processId);

    }
    //审批任务
    @Test
    public void  testComplete(){
        try {
            ApproveVo approveVo=new ApproveVo();
            approveVo.setDynamic(false);
            approveVo.setProcessInstanceId("2501");
            approveVo.setCurrentUser("H000033");
            approveVo.setCommentResult("2");
            approveVo.setCommentContent("【同意】");
            workTaskV2Service.completeTask(approveVo,null);
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询待审批列表
     */
    @Test
     public void queryByAssign() throws WorkFlowException {
        TaskQueryEntity taskQueryEntity= new TaskQueryEntity();
        taskQueryEntity.setBussinessType("activity");
        taskQueryEntity.setModelKey("hour");
        PageInfo<Task> pageInfo= workTaskV2Service.queryTaskByAssign("H000016",1,10,taskQueryEntity);
        System.out.print(pageInfo.getTotal());
     }
    /**
     * 查询完成的列表
     */
    @Test
    public void queryCompleteList(){
        TaskQueryEntity taskQueryEntity= new TaskQueryEntity();
        taskQueryEntity.setBussinessType("activity");
        taskQueryEntity.setModelKey("hour");
        PageInfo pageInfo= workTaskV2Service.selectMyComplete("H000016",1,10,taskQueryEntity);
        System.out.print(pageInfo.getTotal());
    }
    @Test
    public void queryComments(){
        List<Comment> list=workTaskV2Service.selectCommentList("52501");
        System.out.print(list.size());
    }

    /**
     * 根据流程实例ID查询历史任务信息
     */
    @Test
    public void testQueryHistoryTask(){
        String processInstanceId = "2501";
        List<String> variableNames = new ArrayList<String>();
        variableNames.add("applyUserName");
        variableNames.add("isPass");
        HistoryTasksVo taskHistory = workTaskV2Service.getTaskHistoryByProcessInstanceId(processInstanceId, variableNames);
        System.out.print(taskHistory);
    }

    @Test
    public void testMailSend() throws Exception {
        EmailUtil emailUtil=EmailUtil.getEmailUtil();
        emailUtil.sendEmail(
                "mayl@myl888.xyz",
                "sender name",
                "577415138@qq.com",
                "mail subject: how are you?",
                "<font color='red'>can you see bug</font>");
        System.out.println("send out successfully");
    }

    /**
     * 测试根据应用KEY获取模型列表
     */
    @Test
    public void testGetModelListByAppKey(){
        List<Model> lgb = workTaskV2Service.getModelListByAppKey("lgb");
        System.out.print(lgb.size());
    }

    /**
     * 测试委派任务
     */
    @Test
    public void testDelegateTask(){
        workTaskV2Service.delegateTask("H019233","5003");
    }

    /**
     * 测试转办任务
     */
    @Test
    public void testTransferTask(){
        workTaskV2Service.transferTask("62502","H019236", "H019236");
    }

    /**
     * 测试跳转任务
     */
    @Test
    public void testJumpTask(){
        try {
            workTaskV2Service.taskJump("12501","p2","H019235");
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试-查询我拒绝的任务
     */
    @Test
    public void testSelectMyRefuse(){
        TaskQueryEntity taskQueryEntity = new TaskQueryEntity();
        taskQueryEntity.setBussinessType("activity");
        PageInfo<HistoricTaskInstance> historicTaskInstanceList = workTaskV2Service.selectMyRefuse("H000006", 1, 10, taskQueryEntity);
        System.out.println(historicTaskInstanceList);
    }

    /**
     * 测试-驳回
     */
    @Test
    public void testRollBackWorkFlow(){
        workTaskV2Service.rollBackWorkFlow("2501");
    }

    /**
     * 测试-恢复驳回任务
     */
    @Test
    public void testResumeWorkFlow(){
        try {
            workTaskV2Service.resumeWorkFlow("2501",0,null,null);
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试-查询某一任务节点评论
     */
    @Test
    public void testSelectComment(){
        Comment comment = null;
        try {
            comment = workTaskV2Service.selectComment("5005", "H000013");
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
        System.out.println(JSONObject.toJSONString(comment));
    }
}
