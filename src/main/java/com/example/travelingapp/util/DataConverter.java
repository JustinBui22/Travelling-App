package com.example.travelingapp.util;

import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import static com.example.travelingapp.enums.CommonEnum.COMMON;
import static com.example.travelingapp.enums.ErrorCodeEnum.INTERNAL_SERVER_ERROR;

@Log4j2
public class DataConverter {

    private DataConverter() {}

    public static long convertStringToLong(String string) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            log.error("There is an error converting the string to a long", e);
            throw new BusinessException(INTERNAL_SERVER_ERROR, COMMON.name());
        }
    }

    public static String toJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.info("There is an error converting to Json format!", e);
            throw new BusinessException(INTERNAL_SERVER_ERROR, COMMON.name());
        }
    }
}
