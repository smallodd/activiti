import com.activiti.entity.CommonVo;
import com.activiti.service.WorkTaskService;
import com.github.pagehelper.PageInfo;
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
        commonVo.setApplyTitle("测试dubbo接口");
        commonVo.setApplyUserId("H000000");
        commonVo.setApplyUserName("测试人员");
        commonVo.setBusinessKey("业务key");
        commonVo.setBusinessType("ddcecfb0-c516-11e7-ab9c-4ccc6ac949f4");
        commonVo.setModelKey("ceshitiaojian");
        Map map=new HashMap();
        map.put("param",10000);
        String processId=workTaskService.startTask(commonVo,map);
        System.out.print("返回结果为："+processId);
    }
    //审批任务
    @Test
    public void  testComplete(){
        workTaskService.completeTask("30015","cf42f07adc69455b94e82f8ce06de09e","3","我同意这个审批");
    }

    /**
     * 查询待审批列表
     */
    @Test
     public void queryList(){
      PageInfo<Task> pageInfo= workTaskService.queryByAssign("cf42f07adc69455b94e82f8ce06de09e",1,10,"ddcecfb0-c516-11e7-ab9c-4ccc6ac949f4");
      System.out.print(pageInfo.getTotal());
     }
}
