package com.hengtian.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName CommonConfiguration
 * @Description
 * @Author hour
 * @Date2019/6/12 9:29
 * @Version V1.0
 */
@Configuration
public class CommonConfiguration {

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public StrongUuidGenerator uuidGenerator(){
        return new StrongUuidGenerator();
    }

}
