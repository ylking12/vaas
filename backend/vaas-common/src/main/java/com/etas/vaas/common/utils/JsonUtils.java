/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.etas.vaas.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonUtils {
    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String toStr(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        }
        catch (JsonProcessingException e) {
            log.error("Failed to convert object to json string", (Throwable)e);
            return "{}";
        }
    }

    public static <T> T toObj(String jsonStr, Class<T> clazz) {
        try {
            return (T)OBJECT_MAPPER.readValue(jsonStr, clazz);
        }
        catch (JsonProcessingException e) {
            log.error("Failed to convert json string to object", (Throwable)e);
            return null;
        }
    }

    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            log.error("Error serializing object to JSON", (Throwable)e);
            return "{}";
        }
    }

    public static <T> T parseJson(String json, TypeReference<T> typeReference) {
        try {
            return (T)OBJECT_MAPPER.readValue(json, typeReference);
        }
        catch (JsonProcessingException e) {
            log.error("Error parsing JSON: {}", json, (Object)e);
            return null;
        }
    }

    public static <T> T parseJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty((CharSequence)jsonString)) {
            return null;
        }
        try {
            return (T)OBJECT_MAPPER.readValue(jsonString, clazz);
        }
        catch (JsonProcessingException e) {
            log.warn("parse json string error:{}", jsonString, (Object)e);
            return null;
        }
    }

    public static void writeToFile(String filePath, String toWrite) {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8, true);){
            writer.write("," + toWrite);
        }
        catch (IOException e) {
            log.error("dump error: {}", e.getMessage());
        }
    }

    private JsonUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

