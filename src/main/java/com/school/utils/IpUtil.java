package com.school.utils;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

public class IpUtil {
    private static final String key = "P5EBZ-ITH3I-5A3GT-5QIXG-36QW5-O2FS7";
    private static final String url = "https://apis.map.qq.com/ws/location/v1/ip";

    public static String retrieveCity(String ip) {
        String requestUrl = url + "?ip=" + ip + "&key=" + key;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(requestUrl, JSONObject.class);
        String city = null;
        try {
            city = responseEntity.getBody().getJSONObject("result").getJSONObject("ad_info").getString("city");
        } catch (Exception e) {
            return "ip无法定位！";
        }
        return city == null ? "ip无法定位" : city;
    }

    public static String retrieveIp(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (!isValidIp(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (!isValidIp(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip != null && !isValidIp(ip)) {
                if (ip.equalsIgnoreCase("127.0.0.1")) {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ip = inetAddress.getHostAddress();
                }
            }
            if (ip != null && ip.length() > 15) {
                ip = ip.substring(0, ip.indexOf(","));
            }
            if (StringUtils.isEmptyOrWhitespaceOnly(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ip = "";
        }
        return ip;
    }


    private static boolean isValidIp(String ip) {
        if (StringUtils.isEmptyOrWhitespaceOnly(ip) || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }
        return true;
    }

}
