package com.chtwm.workflow.config;

import com.common.seq.sql.SqlSeqUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Date: 2018/6/29
 * Time: 17:12
 * User: yangkai
 * EMail: yangkai01@chtwm.com
 */
@Slf4j
@Configuration
public class StartAfterListener implements ApplicationListener {

    /** 防止重复执行 */
    private static boolean isStart = false;

    @Override
    public void onApplicationEvent(ApplicationEvent arg0) {
        if (isStart) {
            return;
        }
        isStart = true;
        if (log.isDebugEnabled()) {
            log.debug("StartAfterListener onApplicationEvent start...");
        }

        try {
            // 初始化序列生成器
            initSetSeq();
        } catch (Exception e) {
            log.info("", e);
        }
    }

    /**
     *
     * 初始化序列生成器
     *
     */
    private void initSetSeq() {
        DataSource dataSource = MyApplicationContextAware.getBean("dataSource", DataSource.class);
        log.info(" 主键序列号生成器初始化开始--------------");
        // 主键序列号生成器初始化
        SqlSeqUtil.initSqlSeq(dataSource);
        log.info(" 主键序列号生成器初始化结束--------------");
    }
}