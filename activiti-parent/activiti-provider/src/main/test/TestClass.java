import com.activiti.service.PublishProcessService;
import com.activiti.service.WorkTaskService;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONArray;
import org.activiti.engine.history.HistoricProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ma on 2017/8/7.
 */
//指定测试用例的运行器 这里是指定了Junit4
@RunWith(SpringJUnit4ClassRunner.class)
//指定Spring的配置文件 路径相对classpath而言
@ContextConfiguration({"classpath:/springtest/applicationContext.xml"})
public class TestClass {
    @Resource
    PublishProcessService publishProcessService;
    @Resource
    WorkTaskService workTaskService;
    @Test
    public void publish(){
        publishProcessService.publish("common.bpmn");
        Map<String,Object> map=new HashMap<>();
        map.put("userCode",222);
        map.put("budget",500000);
        map.put("maketName","活动名字");
        map.put("activityId","活动主键");
        publishProcessService.startProcess("1","commonProcess","业务主键",map);
    }
    @Test
    public void complete(){
        workTaskService.completeTask("5","99931","通过","张三");
    }
@Test
    public void selectList(){
       PageInfo<HistoricProcessInstance> pageInfo= workTaskService.selectAllPassApprove(1,10);
       System.out.print(pageInfo.getTotal());
       System.out.print(JSONArray.fromObject(pageInfo.getList().toArray()).toString());
    }
}
