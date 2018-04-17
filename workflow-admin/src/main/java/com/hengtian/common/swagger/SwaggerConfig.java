package com.hengtian.common.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableWebMvc
@Deprecated
public class SwaggerConfig {

	@Bean
    public Docket buildDocket(){
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(buildApiInf()).select()
		                .apis(RequestHandlerSelectors.basePackage("com.hengtian.extend.controller"))
		                .paths(PathSelectors.any())
		                .build();
    }

    private ApiInfo buildApiInf(){
        return new ApiInfoBuilder()
                .title("魔盾钉钉开发接口文档")
                .termsOfServiceUrl("http://localhost:8081")
                .description("接口文档")
                .contact(new Contact("liujunyang", "http://localhost:8081", "2205511679@qq.com"))
                .build();

    }
}
