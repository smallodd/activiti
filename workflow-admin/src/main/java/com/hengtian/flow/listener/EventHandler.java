package com.hengtian.flow.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;

/**
 * Activiti的事件处理器
 *
 * @author houjinrong@chtwm.com
 * date 2018/4/26 9:58
 */
public interface EventHandler {
    /**
     * 事件处理器
     *
     * @param event
     */
    public void handle(ActivitiEvent event);
} 