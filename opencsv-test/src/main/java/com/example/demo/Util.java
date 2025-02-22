package com.example.demo;

import java.util.concurrent.Callable;

public enum Util {
    ;

    public static <R> R fromThrowable(Callable<R> function) {
        try {
            return function.call();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
