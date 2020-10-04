package com.school;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableWebSecurity
@MapperScan("com.school.dao")
@EnableTransactionManagement
@EnableSwagger2
//@EnableWebMvc //该注解会覆盖原有的springmvc的配置，需注意！！(完全没必要重写,springboot配的已经很好了)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
