package com.hayden.tracing;

import com.hayden.tracing_agent.TracingAgent;
import com.hayden.tracing_agent.advice.AgentAdvice;
import com.hayden.tracing_agent.advice.ContextHolder;
import lombok.SneakyThrows;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TracingAgentCtxJavaTest {

    @SneakyThrows
    @Test
    public void setup() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://localhost:8080/test-integration", String.class);
        doTextContext();
    }


    public void doTextContext() {
        var testClass = new TestClass();
        for (int i = 0; i < 10; i++) {
            testClass.test();
        }
        for (int i = 0; i < 10; i++) {
            testClass.test();
        }
    }

}
