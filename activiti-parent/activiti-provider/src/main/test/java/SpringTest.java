import com.activiti.entity.CommonVo;
import com.activiti.service.WorkTaskService;
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
    @Test
    public void testStart() {


        CommonVo commonVo=new CommonVo();
        commonVo.setApplyTitle("开始任务");
        commonVo.setApplyUserId("H000000");
        commonVo.setApplyUserName("来啊了");
        commonVo.setBusinessKey("业zxc务ssszxcsssssssssss");
        commonVo.setBusinessType("ddcecfb0-c516-11e7-ab9c-4ccc6ac949f4");
        commonVo.setModelKey("ceshitiaojian");
        Map map=new HashMap();
        map.put("param",10000);
        String processId=workTaskService.startTask(commonVo,map);
        System.out.print("返回结果为："+processId);
    }
    @Test
    public void  testComplete(){
        workTaskService.completeTask("30015","cf42f07adc69455b94e82f8ce06de09e","3","我同意这个审批");
    }
}
