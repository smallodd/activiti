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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanyuexing
 * @date 2019/11/8 12:46
 */
public class XiaoKeMessageProjectTest extends BaseTest{

    @Resource
    private XiaoKeMessageserviceImpl xiaoKeMessageservice;

    @Test
    public void testGetNeedSendTaskNotice(){
        List<TaskNoticePO> list = xiaoKeMessageservice.getNeedSendTaskNotice();
        for (TaskNoticePO taskNoticePO : list) {
            System.out.println("++"+taskNoticePO.toString());
        }
    }

    @Test
    public void testUpdateNoticeState(){
        Integer updateResult = xiaoKeMessageservice.updateNoticeState(1l,5);
        System.out.println("==============================="+updateResult);
    }

    @Test
    public void testUpdateNoticeStateByListKeys(){
        List<Long> ids = new ArrayList<>();
        ids.add(1l);
        ids.add(2l);
        Integer result = xiaoKeMessageservice.updateNoticeStateByListKeys(ids,0);
        System.out.println("++++++++++++++++++++++++++++"+result);
    }


}
