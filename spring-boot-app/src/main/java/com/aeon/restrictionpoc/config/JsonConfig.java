package com.aeon.restrictionpoc.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class JsonConfig {
    public static final SimpleModule jacksonModule;

    static {
        jacksonModule = new SimpleModule("ISO_DATE_TIME_MODULE");
        jacksonModule.addSerializer(LocalDate.class, new LocalDateSerializer(ISO_LOCAL_DATE));
        jacksonModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(ISO_LOCAL_DATE));
    }
}
