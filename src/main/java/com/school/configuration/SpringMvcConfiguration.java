package com.school.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 切记不用@EnableWebMvc
 */
@Configuration
public class SpringMvcConfiguration implements WebMvcConfigurer {

    @Value("${file.path}")
    private String filePath;

    @Value("${spring.file.path}")
    private String springFilePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(springFilePath + "**")
                .addResourceLocations("file:" + filePath)
                .setCachePeriod(31556926);
    }
}
