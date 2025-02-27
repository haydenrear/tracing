package com.hayden.tracing.aspect;

import io.micrometer.common.util.StringUtils;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
public class TracingAspect {

    @Autowired
    private Tracer tracer;
    @Autowired
    private Attributes tracingConfigPropertiesAttributes;

    @Around("@annotation(traceMethodCall)")
    public Object around(ProceedingJoinPoint joinPoint, TraceMethodCall traceMethodCall) throws Throwable {
        var name = joinPoint.getSignature().getName();
        var span = tracer.spanBuilder(name).startSpan()
                .setAllAttributes(tracingConfigPropertiesAttributes);
        if (!StringUtils.isBlank(traceMethodCall.startEvent())) {
            span = span.addEvent(traceMethodCall.startEvent());
        }
        span = span.addEvent("start%s".formatted(name), Instant.now());
        var proceeded =  joinPoint.proceed();
        if (!StringUtils.isBlank(traceMethodCall.endEvent())) {
            span = span.addEvent(traceMethodCall.endEvent());
        }
        span.addEvent("end%s".formatted(name), Instant.now())
                .end();
        return proceeded;
    }

}
