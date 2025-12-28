package com.aeon.restrictionpoc.ws;


import com.aeon.restrictionpoc.dto.Message;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SouthboundRequest {
    Message message;
    Instant requestTime;
}
