package com.school.controller;

import com.school.utils.IpUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {

    @GetMapping("/test")
    public Object test(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr;
//        return IpUtil.retrieveIp(request);
    }
}
