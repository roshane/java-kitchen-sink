package com.aeon.restrictionpoc.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressRestriction {
    private String postcode;
    private LocalDate date;
    private List<AddressRestrictionInterval> restricted;
}
