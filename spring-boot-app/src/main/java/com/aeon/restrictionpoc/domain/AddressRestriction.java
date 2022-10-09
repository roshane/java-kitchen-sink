package com.aeon.restrictionpoc.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddressRestriction {
    private Integer id;
    private String postcode;
    private LocalDateTime from;
    private LocalDateTime to;
    private LocalDateTime createdAt;
}
