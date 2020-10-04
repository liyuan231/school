package com.school.component.jwt;

import com.school.utils.ResponseUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.HashMap;
import java.util.Map;

@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnProperty(prefix = "jwt.config", name = "enabled")
@Configuration
public class JwtConfiguration {
    @Bean
    public JwtTokenGenerator jwtTokenGenerator(JwtProperties jwtProperties) {
        return new JwtTokenGenerator(new JwtPayloadBuilder(), jwtProperties);
    }

    @Bean
    public AuthenticationSuccessHandler jsonAuthenticationSuccessHandler(JwtTokenGenerator jwtTokenGenerator) {
        return (request, response, authentication) -> {
            Map<String, Object> map = new HashMap<>();
            UserDetails principal = (UserDetails) authentication.getPrincipal();
            JwtTokenPair jwtTokenPair = jwtTokenGenerator.jwtTokenPair(principal.getUsername(), principal.getAuthorities(), null);
            map.put("access_token", jwtTokenPair.getAccessToken());
            map.put("refresh_token", jwtTokenPair.getRefreshToken());
            String build = ResponseUtil.build(request.getRequestURI(), HttpStatus.OK.value(), "登录成功！", map);
            ResponseUtil.printlnInfo(response, build);
        };
    }

    @Bean
    public AuthenticationFailureHandler jsonAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            String build = ResponseUtil.build(request.getRequestURI(), HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误！", exception.getMessage());
            ResponseUtil.printlnInfo(response, build);
        };
    }

}
