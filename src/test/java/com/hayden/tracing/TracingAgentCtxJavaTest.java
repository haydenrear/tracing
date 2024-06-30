package com.hayden.tracing;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TracingAgentCtxJavaTest {

    @SneakyThrows
    @Test
    public void setup() {
        TestClass testClass = new TestClass();
        testClass.test();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://localhost:8080/test-integration", String.class);
        assertThat(forEntity.getStatusCode().is2xxSuccessful()).isTrue();
        doTextContext(testClass);
    }


    public void doTextContext(TestClass testClass) {
        for (int i = 0; i < 10; i++) {
            testClass.test();
        }
        for (int i = 0; i < 10; i++) {
            testClass.test();
        }
    }

}
