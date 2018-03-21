import com.activiti.common.EmailUtil;
import com.activiti.entity.ApproveVo;
import com.activiti.entity.CommonVo;
import com.activiti.entity.HistoryTasksVo;
import com.activiti.entity.TaskQueryEntity;
import com.activiti.expection.WorkFlowException;
import com.activiti.service.WorkTaskV2Service;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
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
        commonVo.setBusinessKey("0009");
        commonVo.setBusinessType("activity");
        commonVo.setModelKey("cgcpzr");
        commonVo.setDynamic(false);
        Map map=new HashMap();
        map.put("var1",1);
        map.put("var2",1);
        map.put("var3",1);
        map.put("var4",1);
        map.put("var5",1);
        map.put("var6",0);
        map.put("var7",1);
        map.put("var8",0);
        map.put("var9",1);
        map.put("var10",1);
        map.put("var11",1);

        String processId= null;
        try {
            processId = workTaskV2Service.startTask(commonVo,map);
            //boolean b = workTaskV2Service.setApprove(processId, "H019235,H019235");
            //System.out.println(b);
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
        System.out.println("返回结果为："+processId);

    }

    private void testSetApprover(){
        try {
            boolean b = workTaskV2Service.setApprove("17561", "H019235,H019235");
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
    }

    //审批任务
    @Test
    public void  testComplete(){
        try {
            ApproveVo approveVo=new ApproveVo();
            approveVo.setDynamic(false);
            approveVo.setProcessInstanceId("77501");
            approveVo.setCurrentUser("H015745");
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
        PageInfo<Task> pageInfo= workTaskV2Service.queryTaskByAssign("H019235",1,10,taskQueryEntity);
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
        try {
            String userId = "H000013";
            String transferUserId = "H019236";
            boolean b = workTaskV2Service.transferTask("20001", "", "");
            System.out.println(b);
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试跳转任务
     */
    @Test
    public void testJumpTask(){
        try {
            workTaskV2Service.taskJump("68142","p2","H019235");
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
        taskQueryEntity.setModelKey("hour");
        PageInfo<HistoricTaskInstance> historicTaskInstanceList = workTaskV2Service.selectMyRefuse("H019235", 1, 10, taskQueryEntity);
        System.out.println(historicTaskInstanceList);
    }

    /**
     * 测试-驳回
     */
    @Test
    public void testRollBackWorkFlow(){
        String processInstanceId = "5001";
        int type = 1;
        try {
            workTaskV2Service.rollBackProcess(processInstanceId, type);
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试-恢复驳回任务
     */
    @Test
    public void testResumeWorkFlow(){
        try {
            Map<String,Object> variables = Maps.newHashMap();
            variables.put("testRollBackWorkFlow", "哈哈");
            workTaskV2Service.resumeProcess("5001","H000014",variables);
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试-获取最后审批人
     */
    @Test
    public void getLastApprover(){
        String lastApprover = workTaskV2Service.getLastApprover("2501");
        System.out.println(lastApprover);
    }
}
