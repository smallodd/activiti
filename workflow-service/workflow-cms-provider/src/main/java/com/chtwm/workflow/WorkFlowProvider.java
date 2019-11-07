package com.chtwm.workflow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Date: 2019/11/7
 * Time: 9:50
 * User: yangkai
 * EMail: yangkai01@chtwm.com
 */
@Slf4j
@SpringBootApplication
public class WorkFlowProvider {

    public static void main(String[] args) {
        new SpringApplicationBuilder(WorkFlowProvider.class)
                .web(WebApplicationType.NONE)
                .run(args);
        log.info("###### WorkFlowProvider Service Started !! ######");
    }
}
