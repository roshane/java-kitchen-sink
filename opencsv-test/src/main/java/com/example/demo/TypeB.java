package com.example.demo;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
public class TypeB implements Root {
    String name;
    Integer id;
    Entity entity;
}
