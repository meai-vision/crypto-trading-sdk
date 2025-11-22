package com.meaivision.trading.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {

  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  public static JsonNode convertToJsonTree(String content) {
    try {
      return objectMapper.readTree(content);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Can't convert content to JSON node!", e);
    }
  }

  public static String convertToJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T convertToObject(TreeNode n, Class<T> valueType) {
    return objectMapper.convertValue(n, valueType);
  }
}
