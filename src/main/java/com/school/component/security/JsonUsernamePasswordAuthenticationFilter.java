package com.school.component.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * json登录
 */
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    /**
     * json登录接口及方式
     *
     * @param
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String contentType = request.getContentType().toLowerCase();
        if (contentType.equals("application/json") || contentType.equals("application/json;charset=utf-8")) {
            String username = "";
            String password = "";
            ObjectMapper objectMapper = new ObjectMapper();
            UsernamePasswordAuthenticationToken authenticationToken = null;
            try (InputStream inputStream = request.getInputStream()) {
                JsonNode body = objectMapper.readTree(inputStream);
                JsonNode usernameJsonNode = body.get("username");
                if (usernameJsonNode != null) {
                    username = usernameJsonNode.asText();
                }
                JsonNode passwordJsonNode = body.get("password");
                if (passwordJsonNode != null) {
                    password = passwordJsonNode.asText();
                }
                authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            } catch (IOException e) {

            } finally {
                setDetails(request, authenticationToken);
                return this.getAuthenticationManager().authenticate(authenticationToken);
            }
        } else {
            return super.attemptAuthentication(request, response);
        }
    }

}
