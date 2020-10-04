package com.school.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getStringField(String content, String field) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(content);
        JsonNode leaf = jsonNode.get(field);
        if (leaf == null) {
            throw new IllegalArgumentException(field + " 字段不应为空！");
        }
        return leaf.asText();
    }

    public static String parseObjectToJsonString(Object o) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(o);
    }

    public static String getStringField(InputStream inputStream, String field) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(inputStream);
        JsonNode leaf = jsonNode.get(field);
        if (leaf == null) {
            throw new NullPointerException("json中username字段不应为空！");
        }
        return leaf.asText();
    }

    public static JsonNode parseStringToJsonObject(String claims) throws JsonProcessingException {
        return objectMapper.readTree(claims);
    }

    public static JsonNode parseInputStreamToJsonNode(ServletInputStream inputStream) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(inputStream);
        return jsonNode;

    }
}
