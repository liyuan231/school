package com.school.configuration;

import com.school.component.jwt.JwtTokenGenerator;
import com.school.component.security.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
//    private static final String LOGIN_PROCESSING_URL = "/login";

    @Bean
    @Primary
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();//前端加密和后端无关
    }

//    @Bean
//    public PasswordEncoder md5PasswordEncoder() {
//        return new MessageDigestPasswordEncoder("MD5");
//    }

    @Resource
    JwtTokenGenerator jwtTokenGenerator;

    @Resource
    AuthenticationFailureHandler jsonLoginFailureHandler;

    @Resource
    AuthenticationSuccessHandler jsonLoginSuccessHandler;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors(withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new SimpleAuthenticationEntryPoint())
                .accessDeniedHandler(new SimpleAccessDeniedHandler())
                .and()
//                .authorizeRequests().antMatchers("/login.html", "/test").permitAll()
//                .and()
                .authorizeRequests().anyRequest().permitAll()
                //这里开放所有接口，注解模式来处理权限问题
//                .withObjectPostProcessor(filterSecurityInterceptorObjectPostProcessor())//动态权限配置
                .and()
//                .and()
//                .addFilterBefore(new PreLoginFilter(LOGIN_PROCESSING_URL, loginPostProcessors), UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                .loginPage("/login.html")
//                .loginProcessingUrl(LOGIN_PROCESSING_URL)
//                .successForwardUrl("/login/success")
//                .failureForwardUrl("/login/failure")
//                .successHandler(jsonLoginSuccessHandler) 此处自定义了jsonUsernamePasswordAuthenticationFilter，因此须在给类中重新设置
//                .failureHandler(jsonLoginFailureHandler)
                .and().logout().logoutSuccessHandler(new SimpleLogoutSuccessHandler());

//        http.addFilterBefore(new JsonUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenGenerator), UsernamePasswordAuthenticationFilter.class);
        //替换掉该UsernamePasswordAuthenticationFilter
        http.addFilterAt(jsonUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/v2/api-docs",//swagger api json
//                "/swagger-resources/configuration/ui",//用来获取支持的动作
//                "/swagger-resources",//用来获取api-docs的URI
//                "/swagger-resources/configuration/security",//安全选项
//                "/swagger-ui.html");//        super.configure(web);
//    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter() throws Exception {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter = new JsonUsernamePasswordAuthenticationFilter();
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(jsonLoginSuccessHandler);
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(jsonLoginFailureHandler);
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        //登录默认为 POST ->"/login" 也可在此处重写
        return jsonUsernamePasswordAuthenticationFilter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;

    }


}
