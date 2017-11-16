import com.activiti.common.EmailUtil;
import com.activiti.entity.CommonVo;
import com.activiti.entity.HistoryTasksVo;
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
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
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
        commonVo.setApplyTitle("测试在线换审批人");
        commonVo.setApplyUserId("H000000");
        commonVo.setApplyUserName("测试111111111111");
        commonVo.setBusinessKey("业务key");
        commonVo.setBusinessType("yingxiao");
        commonVo.setModelKey("test");
        Map map=new HashMap();
        map.put("param",10000);
        String processId=workTaskService.startTask(commonVo,map);
        System.out.print("返回结果为："+processId);
    }
    //审批任务
    @Test
    public void  testComplete(){
        workTaskService.completeTask("5034","c28fb2ff582d484ea77692279ae56fff","我不同意这个审批","3");
    }

    /**
     * 查询待审批列表
     */
    @Test
     public void queryList(){
      PageInfo<Task> pageInfo= workTaskService.queryByAssign("c28fb2ff582d484ea77692279ae56fff",1,10,"key");
      System.out.print(pageInfo.getTotal());
     }
    /**
     * 查询完成的列表
     */
    @Test
    public void queryCompleteList(){
        PageInfo pageInfo= workTaskService.selectMyComplete("c28fb2ff582d484ea77692279ae56fff",1,10,"key");
        System.out.print(pageInfo.getTotal());
    }
    @Test
    public void queryComments(){
        List<Comment> list=workTaskService.selectListComment("111");
        System.out.print(list.size());
    }

     @Test
     public void testQueryHistoryTask(){
         String taskId = "2513";
         List<String> variableNames = new ArrayList<String>();
         variableNames.add("applyUserName");
         variableNames.add("isPass");
         HistoryTasksVo taskHistory = workTaskService.getTaskHistoryBytaskId(taskId, variableNames);
         System.out.print(taskHistory);
     }
    @Test
    public void testMailSend() throws Exception {
        EmailUtil emailUtil=EmailUtil.getEmailUtil();
        emailUtil.sendEmail(
                "13601094934@163.com",
                "sender name",
                "577415138@qq.com",
                "mail subject: how are you?",
                "<font color='red'>can you see?ол╣Щ</font>");
        System.out.println("send out successfully");
    }

    @Test
    public void testGetModelListByAppKey(){
        List<Model> lgb = workTaskService.getModelListByAppKey("lgb");
        System.out.print(lgb.size());
    }
}
