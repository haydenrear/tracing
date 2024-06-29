package com.hayden.tracing;

import com.hayden.tracing.observation_aspects.MonitoringTypes;
import org.aspectj.lang.annotation.Around;

@Cdc({
        @LoggingPattern(
                around = @Around("within(com.hayden.tracing.entity.*)"),
                aspectName = "TestAspect",
                aspectFunctionName = "thisAspect",
                logId = "logging_id",
                monitoringTypes = MonitoringTypes.IO
        )
})
public class TestAspect {
}
