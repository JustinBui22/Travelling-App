package com.example.travelingapp.util.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DataConverter {

    public static long convertStringToLong(String string) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            throw new RuntimeException("There is an error converting the string to a long", e);
        }
    }

    public static String toJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.info("There is an error converting to Json format!", e);
            return null;
            //throw new RuntimeException(e);
        }
    }
}
