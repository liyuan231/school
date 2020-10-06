package com.school.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.LinkedList;
import java.util.List;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket adminDocket() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> parameters = new LinkedList<>();
        //带上请求头
        tokenPar.name("Authorization").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        parameters.add(tokenPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("管理端")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.school.controller.admin"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(parameters);
    }

    @Bean
    public Docket clientDocket() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> parameters = new LinkedList<>();
        //带上请求头
        tokenPar.name("Authorization").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        parameters.add(tokenPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("客户端")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.school.controller.client"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(parameters);
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
