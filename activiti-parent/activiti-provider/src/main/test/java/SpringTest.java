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
        commonVo.setApplyTitle("测试");
        commonVo.setApplyUserId("H000012");
        commonVo.setApplyUserName("测试人");
        commonVo.setBusinessKey("业务主键0901");
        commonVo.setBusinessType("测试系统");
        commonVo.setProDefinedKey("process");
        workTaskService.startTask(commonVo);
    }
}
