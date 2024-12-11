package com.example.travelingapp.util.common;

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
}
