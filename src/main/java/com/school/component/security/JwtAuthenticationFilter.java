package com.school.component.security;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.school.component.jwt.JwtTokenGenerator;
import com.school.exception.InvalidTokenException;
import com.school.model.Role;
import com.school.model.Roletoauthorities;
import com.school.service.impl.RoleServiceImpl;
import com.school.service.impl.RoleToAuthoritiesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHENTICATION_PREFIX = "Bearer ";
    private AuthenticationEntryPoint authenticationEntryPoint = new SimpleAuthenticationEntryPoint();

    @Autowired
    @Lazy
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private RoleToAuthoritiesServiceImpl roleToAuthoritiesService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /**
         * SecurityContext上下文已有身份认证
         */
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith(AUTHENTICATION_PREFIX)) {
            String jwtToken = header.substring(AUTHENTICATION_PREFIX.length());
            if (StringUtils.hasText(jwtToken)) {
                try {
                    authenticationTokenHandle(jwtToken, request);
                } catch (AuthenticationException authenticationException) {
                    authenticationEntryPoint.commence(request, response, authenticationException);
                }
            } else {
                authenticationEntryPoint.commence(request, response, new AuthenticationCredentialsNotFoundException("token is missing!"));
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticationTokenHandle(String jwtToken, HttpServletRequest request) throws InvalidTokenException {
        JSONObject jsonObject = null;
        try {
            jsonObject = jwtTokenGenerator.decodeAndVerify(jwtToken);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("token解析错误！");
        }
        if (Objects.nonNull(jsonObject)) {
            Collection<GrantedAuthority> roles_ = new ArrayList<>();
            JSONArray authoritiesJSONArray = jsonObject.getJSONArray("roles");
            Iterator<Object> iterator = authoritiesJSONArray.iterator();

            while (iterator.hasNext()) {
                //获取token中的其中一个角色
                String next = (String) iterator.next();//这里是角色，因为我只放了角色进去
                //通过该名字找到其id
                Role role = roleService.findByName(next);
                List<Roletoauthorities> byRoleId = roleToAuthoritiesService.findByRoleId(role.getId());
                for (Roletoauthorities roletoauthorities : byRoleId) {
                    roles_.add(new SimpleGrantedAuthority(roletoauthorities.getAuthority()));
                }
                roles_.add((GrantedAuthority) () -> "ROLE_" + next);
            }

            String username = jsonObject.getString("audience");
            User user = new User(username, "[PASSWORD]", roles_);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, roles_);
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } else {
            throw new BadCredentialsException("token is invalid!");
        }
    }
}
