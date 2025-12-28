package com.aeon.restrictionpoc.dto;

import lombok.Value;

import java.time.Instant;

@Value
public class ResponseMessage {
    Message message;
    Instant requestTime;
    Instant processedTime;
}
