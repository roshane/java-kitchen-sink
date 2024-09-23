package com.example.demo;

public interface ThrowableFunction<T, R> {

    R apply(T t) throws Exception;
}
