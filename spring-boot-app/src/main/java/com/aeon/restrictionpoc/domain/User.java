package com.aeon.restrictionpoc.domain;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
@Getter
@Setter(AccessLevel.PRIVATE)
public class User {
    private String id;
    private LocalDate dob;
    private String firstName;
    private String lastName;
    private String email;
}
