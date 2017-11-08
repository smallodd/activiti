import com.activiti.entity.CommonVo;
import com.activiti.service.WorkTaskService;
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

    @Test
    public void testGetUserByCode() {
        ApplicationContext act=new ClassPathXmlApplicationContext("dubbo-server-consumer.xml");
        WorkTaskService workTaskService= (WorkTaskService) act.getBean("workTaskService");
        CommonVo commonVo=new CommonVo();
        commonVo.setApplyTitle("开始任务");
        commonVo.setApplyUserId("H000000");
        commonVo.setApplyUserName("来啊了");
        commonVo.setBusinessKey("业zxc务ssszxcsssssssssss");
        commonVo.setBusinessType("测试zxczxc系统ssssssssssssssssss");
        commonVo.setProDefinedKey("testlliuchengtiaojian");
        Map map=new HashMap();
        map.put("param",10000);
        workTaskService.startTask(commonVo,map);
    }
}
