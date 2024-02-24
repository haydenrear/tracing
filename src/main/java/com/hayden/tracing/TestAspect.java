package com.hayden.tracing;

import com.hayden.tracing.observation_aspects.TracingAspectSupplier;
import com.hayden.tracing_apt.Cdc;
import com.hayden.tracing_apt.Logged;
import com.hayden.tracing_apt.LoggingPattern;
import org.aspectj.lang.annotation.Before;

@Cdc(
        @LoggingPattern(
                before = @Before("hello..."),
                aspectName = "",
                aspectFunctionName = "",
                logId = "logging_id"
        )
)
public class TestAspect {
}
