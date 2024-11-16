package com.hayden.tracing.config

import com.hayden.utilitymodule.telemetry.log.LoggingConfig
import com.hayden.utilitymodule.telemetry.log.TelemetryAttributes
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile

@Configuration
@Import(LoggingConfig::class)
@ComponentScan(basePackages = ["com.hayden.tracing", "com.hayden.tracing_apt.props"], basePackageClasses = [TelemetryAttributes::class])
@Profile("telemetry-logging")
open class TelemetryLoggingConfig
