package com.aeon.restrictionpoc.dto;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    UUID id;
    String message;
}
