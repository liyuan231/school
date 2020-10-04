package com.school.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    public static String build(String uri,
                               int httpStatus,
                               String message,
                               Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", System.currentTimeMillis());
        map.put("uri", uri);
        map.put("code", httpStatus);
        map.put("message", message);
        map.put("data", data);
        return JSONObject.toJSONString(map);
    }
    public static String build(HttpServletResponse response,
                               int httpStatus,
                               String message,
                               Object data) {
        response.setCharacterEncoding("utf-8");
        return build(httpStatus,message,data);
    }

    public static String build(int httpStatus, String message, Object data) {
        return build("uri", httpStatus, message, data);
    }

    public static String build(String uri,
                               int httpStatus,
                               String message)  {
        return build(uri, httpStatus, message, null);
    }

    public static String build(String uri, int httpStatus)  {
        return build(uri, httpStatus, "");
    }

    public static void printlnInfo(HttpServletResponse response, String build) throws IOException {
        //此步须在         PrintWriter printWriter = response.getWriter();
        //之前，因为一旦获取了PrintWriter，对其的设置便不会在生效！
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.println(build);
        printWriter.close();
    }
}
