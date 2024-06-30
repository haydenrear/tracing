package com.hayden.tracing;


import java.util.UUID;

public class TestClass {

    int i = 0;

    public long test() {
        i += 1;
        System.out.printf("test... %s%n", i);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return UUID.randomUUID().toString().hashCode();
    }

}
