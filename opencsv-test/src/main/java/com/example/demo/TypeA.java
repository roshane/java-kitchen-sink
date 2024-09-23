package com.example.demo;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
public class TypeA implements Root {
    String name;
    Integer id;
}
