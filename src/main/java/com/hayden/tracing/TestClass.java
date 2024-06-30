package com.hayden.tracing;

import lombok.SneakyThrows;

import java.util.UUID;

public class TestClass {


    public long test() {
        System.out.println("test...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return UUID.randomUUID().toString().hashCode();
    }

}
