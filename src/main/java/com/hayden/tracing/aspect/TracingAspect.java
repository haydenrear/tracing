package com.hayden.tracing.aspect;

import io.micrometer.common.util.StringUtils;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;

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
        var span = doStartSpan(traceMethodCall, name);

        Object proceeded ;

        try {
            proceeded = joinPoint.proceed();
        } catch (Exception e) {
            span.recordException(e);
            doEndSpan(traceMethodCall, span, name);
            throw e;
        }

        doEndSpan(traceMethodCall, span, name);
        return proceeded;
    }

    private Span doStartSpan(TraceMethodCall traceMethodCall, String name) {
        var span = tracer.spanBuilder(name).startSpan()
                .setAllAttributes(tracingConfigPropertiesAttributes);

        for (var tag : traceMethodCall.tags()) {
            span = span.addEvent(tag);
        }

        if (!StringUtils.isBlank(traceMethodCall.startEvent())) {
            span = span.addEvent(traceMethodCall.startEvent());
        }

        span = span.addEvent("start%s".formatted(name), Instant.now());
        return span.addEvent("tracedMethodCall");
    }

    private static void doEndSpan(TraceMethodCall traceMethodCall, Span span, String name) {
        if (!StringUtils.isBlank(traceMethodCall.endEvent())) {
            span = span.addEvent(traceMethodCall.endEvent());
        } else if (!StringUtils.isBlank(traceMethodCall.startEvent())) {
            span = span.addEvent(traceMethodCall.startEvent());
        }

        span.addEvent("end%s".formatted(name), Instant.now())
                .end();
    }

}
