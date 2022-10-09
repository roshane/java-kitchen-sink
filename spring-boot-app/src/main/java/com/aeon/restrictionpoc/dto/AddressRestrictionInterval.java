package com.aeon.restrictionpoc.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressRestrictionInterval {
    private LocalDateTime from;
    private LocalDateTime to;
}
