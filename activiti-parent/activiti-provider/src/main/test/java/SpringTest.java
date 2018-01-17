import com.activiti.common.EmailUtil;
import com.activiti.entity.CommonVo;
import com.activiti.entity.HistoryTasksVo;
import com.activiti.entity.TaskQueryEntity;
import com.activiti.expection.WorkFlowException;
import com.activiti.service.WorkTaskService;
import com.github.pagehelper.PageInfo;
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
 * Created by ma on 2017/11/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:dubbo-server-consumer.xml"})
public class SpringTest {
    ApplicationContext act;
    WorkTaskService workTaskService;

    @Before
    public void testBefore(){
        act=new ClassPathXmlApplicationContext("dubbo-server-consumer.xml");
         workTaskService= (WorkTaskService) act.getBean("workTaskService");

    }
    //开启任务
    @Test
    public void testStart() {


        CommonVo commonVo=new CommonVo();
        commonVo.setApplyTitle("测试转办任务");
        commonVo.setApplyUserId("H000000");
        commonVo.setApplyUserName("测试人");
        commonVo.setBusinessKey("业务key");
        commonVo.setBusinessType("sys1");
        commonVo.setModelKey("ceshi");
        Map map=new HashMap();
        map.put("param",10000);
        String processId= null;
        try {
            processId = workTaskService.startTask(commonVo,map);
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
        System.out.println("返回结果为："+processId);
    }
    //审批任务
    @Test
    public void  testComplete(){
        try {
            workTaskService.completeTask("22520", "6414f0ca9eaf4ba596736eb7db0ad157","我不同意这个审批","3");
        } catch (WorkFlowException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询待审批列表
     */
    @Test
     public void queryList(){
        TaskQueryEntity taskQueryEntity= new TaskQueryEntity();
        taskQueryEntity.setBussinessType("maket");
        taskQueryEntity.setModelKey("ceshi");
      PageInfo<Task> pageInfo= workTaskService.queryByAssign("c28fb2ff582d484ea77692279ae56fff",1,10,taskQueryEntity);
      System.out.print(pageInfo.getTotal());
     }
    /**
     * 查询完成的列表
     */
    @Test
    public void queryCompleteList(){
        TaskQueryEntity taskQueryEntity= new TaskQueryEntity();
        taskQueryEntity.setBussinessType("maket");
        taskQueryEntity.setModelKey("ceshi");
        PageInfo pageInfo= workTaskService.selectMyComplete("c28fb2ff582d484ea77692279ae56fff",1,10,taskQueryEntity);
        System.out.print(pageInfo.getTotal());
    }
    @Test
    public void queryComments(){
        List<Comment> list=workTaskService.selectListComment("111");
        System.out.print(list.size());
    }

    /**
     * 根据流程实例ID查询历史任务信息
     */
    @Test
    public void testQueryHistoryTask(){
        String processInstanceId = "33395";
        List<String> variableNames = new ArrayList<String>();
        variableNames.add("applyUserName");
        variableNames.add("isPass");
        HistoryTasksVo taskHistory = workTaskService.getTaskHistoryByProcessInstanceId(processInstanceId, variableNames);
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
                "<font color='red'>can you see?ол╣Щ</font>");
        System.out.println("send out successfully");
    }

    /**
     * 测试根据应用KEY获取模型列表
     */
    @Test
    public void testGetModelListByAppKey(){
        List<Model> lgb = workTaskService.getModelListByAppKey("lgb");
        System.out.print(lgb.size());
    }

    /**
     * 测试委派任务
     */
    @Test
    public void testDelegateTask(){
        workTaskService.delegateTask("H019233","5003");
    }

    /**
     * 测试转办任务
     */
    @Test
    public void testTransferTaskTask(){
        workTaskService.transferTask("H019236","5003");
    }
}
