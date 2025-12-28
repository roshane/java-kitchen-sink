package com.aeon.restrictionpoc.ws;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SouthboundResponse {
    SouthboundRequest request;
    Boolean ack;
    Instant processedTime;
}
