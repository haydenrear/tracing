package com.hayden.tracing.config

import com.hayden.utilitymodule.otel.DisableOtelConfiguration
import io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile

@Configuration
@ImportAutoConfiguration(
    exclude = [
        OpenTelemetryAutoConfiguration::class,
        org.springframework.boot.actuate.autoconfigure.tracing.OpenTelemetryTracingAutoConfiguration::class,
        TracingAutoConfiguration::class]
)
@Import(DisableOtelConfiguration::class)
@Profile("!telemetry-logging")
open class DisableTelemetryLoggingConfig
