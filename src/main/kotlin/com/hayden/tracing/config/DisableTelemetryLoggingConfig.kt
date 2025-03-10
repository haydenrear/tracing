package com.hayden.tracing.config

import io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@ImportAutoConfiguration(
    exclude = [
        OpenTelemetryAutoConfiguration::class,
        org.springframework.boot.actuate.autoconfigure.tracing.OpenTelemetryAutoConfiguration::class, TracingAutoConfiguration::class]
)
@Profile("!telemetry-logging")
open class DisableTelemetryLoggingConfig
