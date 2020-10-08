package com.mass3d.system.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public class JacksonUtils {

  private final static ObjectMapper jsonMapper = new ObjectMapper();

  static {
    jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  public static <T> T fromJson(byte[] src, Class<T> clazz) {
    try {
      return jsonMapper.readValue(src, clazz);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  public static <T> T fromJson(String string, Class<T> clazz) {
    try {
      return jsonMapper.readValue(string, clazz);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  public static <T> String toJson(T object) {
    try {
      return jsonMapper.writeValueAsString(object);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  public static <T, U> Map<T, U> fromJsonToMap(String object)
      throws IOException {
    TypeReference<HashMap<T, U>> typeRef = new TypeReference<HashMap<T, U>>() {
    };

    return jsonMapper.readValue(object, typeRef);
  }

  public static void fromObjectToReponse(HttpServletResponse response, Object clazz) {
    response.setStatus(HttpServletResponse.SC_ACCEPTED);
    response.setContentType("application/json");

    PrintWriter jsonResponse;
    try {
      jsonResponse = response.getWriter();
      jsonResponse.print(toJson(clazz));
      jsonResponse.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
