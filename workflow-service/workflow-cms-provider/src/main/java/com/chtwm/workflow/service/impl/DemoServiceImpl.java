package com.chtwm.workflow.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.chtwm.workflow.service.DemoService;

/**
 * Date: 2019/11/7
 * Time: 12:00
 * User: yangkai
 * EMail: yangkai01@chtwm.com
 */

@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public String demo(String name) {
        return "hello " + name;
    }
}
