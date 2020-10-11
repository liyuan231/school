package com.school.component.jwt;

import com.school.component.security.UserServiceImpl;
import com.school.exception.UserNotFoundException;
import com.school.model.User;
import com.school.service.impl.UserToRoleServiceImpl;
import com.school.utils.IpUtil;
import com.school.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnProperty(prefix = "jwt.config", name = "enabled")
@Configuration
public class JwtConfiguration {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserToRoleServiceImpl userToRoleService;

    @Bean
    public JwtTokenGenerator jwtTokenGenerator(JwtProperties jwtProperties) {
        return new JwtTokenGenerator(new JwtPayloadBuilder(), jwtProperties);
    }

    @Bean
    public AuthenticationSuccessHandler jsonAuthenticationSuccessHandler(JwtTokenGenerator jwtTokenGenerator) {
        return (request, response, authentication) -> {
            Map<String, Object> map = new HashMap<>();
            UserDetails principal = (UserDetails) authentication.getPrincipal();
            User userInDb = userService.findByUsername(principal.getUsername());
            Integer levelInDB = userToRoleService.retrieveUserToRoleByUser(userInDb);
            String level = request.getParameter("level").trim();
            if (!levelInDB.toString().equals(level)) {
                String build = ResponseUtil.build(HttpStatus.BAD_GATEWAY.value(), "用户名登录错地方！", null);
                ResponseUtil.printlnInfo(response, build);
                return;
            }
            try {
                //获取ip时好像一直有个问题
                userInDb.setLastloginip(request.getRemoteAddr());
//                System.out.println(request.getA);
                LocalDateTime now = LocalDateTime.now();
                userInDb.setLastlogintime(now);
                userInDb.setLocation(IpUtil.retrieveCity(userInDb.getLastloginip()));
                userInDb.setPassword(null);//密码会被加密，因此此处需设置为空
                userService.update(userInDb);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
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
