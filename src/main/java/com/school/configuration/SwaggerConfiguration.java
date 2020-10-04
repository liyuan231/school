package com.school.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public Docket adminDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("管理端")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.school.controller.admin"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket clientDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("客户端")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.school.controller.client"))
                .paths(PathSelectors.any())
                .build();
    }
//
    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("签约系统")
                .description("签约系统后台接口（ps:登录接口默认为post: /login ，由于被springsecurity拦截器实现了，参数普通的form表单以及json均支持，参数名分别为username以及password）")
                .version("v1.0")
                .build();
    }
}
