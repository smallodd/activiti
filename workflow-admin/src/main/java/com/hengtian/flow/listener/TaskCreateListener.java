package com.hengtian.flow.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 任务创建监听器
 * 主要用来执行人员分配，事件执行等
 *
 * @author houjinrong@chtwm.com
 * date 2018/4/26 10:00
 */
public class TaskCreateListener implements EventHandler {

    private static Log log = LogFactory.getLog(TaskCreateListener.class);

    @Override
    public void handle(ActivitiEvent event) {

        System.out.println("监听");

    }

}  