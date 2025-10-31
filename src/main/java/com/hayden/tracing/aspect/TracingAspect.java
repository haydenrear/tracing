package com.hayden.tracing.aspect;

import com.hayden.utilitymodule.reflection.ParameterAnnotationUtils;
import io.micrometer.common.util.StringUtils;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Aspect
@Component
public class TracingAspect {

    @Autowired
    private Tracer tracer;
    @Autowired
    private Attributes tracingConfigPropertiesAttributes;

    @Around("@annotation(traceMethodCall)")
    public Object around(ProceedingJoinPoint joinPoint, TraceMethodCall traceMethodCall) throws Throwable {

        var args = ParameterAnnotationUtils.retrieveArgsIndex(joinPoint, TraceArg.class)
                .stream().map(i -> {
                    if (Objects.equals(i.t().ty(), String.class)) {
                        return String.valueOf(joinPoint.getArgs()[i.idx()]);
                    }

                    throw new RuntimeException("Don't support anything but string in TraceArg");
                })
                .toList();
        var name = joinPoint.getSignature().getName();
        var span = doStartSpan(traceMethodCall, name, args);

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

    private Span doStartSpan(TraceMethodCall traceMethodCall, String name, List<String> args) {
        var span = tracer.spanBuilder(name).startSpan()
                .setAllAttributes(tracingConfigPropertiesAttributes);

        for (var tag : traceMethodCall.tags()) {
            span = span.addEvent(tag, Attributes.of(AttributeKey.stringKey("fn_args"), String.join(", ", args)));
        }

        if (!StringUtils.isBlank(traceMethodCall.startEvent())) {
            span = span.addEvent(traceMethodCall.startEvent(), Attributes.of(AttributeKey.stringKey("fn_args"), String.join(", ", args)));
        }

        span = span.addEvent("start%s".formatted(name), Instant.now());
        return span.addEvent("tracedMethodCall", Attributes.of(AttributeKey.stringKey("fn_args"), String.join(", ", args)));
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
