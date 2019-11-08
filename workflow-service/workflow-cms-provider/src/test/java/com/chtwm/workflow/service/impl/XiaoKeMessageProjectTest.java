package com.chtwm.workflow.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chtwm.workflow.entity.TaskNoticePO;
import com.chtwm.workflow.service.impl.XiaoKeMessageserviceImpl;
import javafx.application.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanyuexing
 * @date 2019/11/8 12:46
 */
public class XiaoKeMessageProjectTest extends BaseTest{

    @Resource
    private XiaoKeMessageserviceImpl xiaoKeMessageservice;

    @Test
    public void a(){
        List<TaskNoticePO> list = xiaoKeMessageservice.getNeedSendTaskNotice();
        for (TaskNoticePO taskNoticePO : list) {
            System.out.println("++"+taskNoticePO.toString());
        }
    }

    @Test
    public void b(){
        Integer updateResult = xiaoKeMessageservice.updateNoticeState("1",5);
        System.out.println("==============================="+updateResult);
    }
}
