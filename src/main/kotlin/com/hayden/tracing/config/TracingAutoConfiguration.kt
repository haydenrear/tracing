package com.hayden.tracing.config

import com.hayden.tracing.observation_aspects.ObservabilityUtility
import com.hayden.tracing.props.TracingConfigurationProperties
import io.micrometer.observation.ObservationRegistry
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy


@Configuration
@ComponentScan(basePackageClasses = [ObservabilityUtility::class, TracingInterceptor::class])
@EnableConfigurationProperties(TracingConfigurationProperties::class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
open class TracingAutoConfiguration {


    @Bean
    open fun httpSpanExporter(): OtlpHttpSpanExporter {
        return OtlpHttpSpanExporter.getDefault()
            .toBuilder()
            .build()
    }

    @Bean
    open fun httpMetricsExporter(): OtlpHttpMetricExporter {
        return OtlpHttpMetricExporter.getDefault().toBuilder()
            .build()
    }


    @Bean
    open fun httpLoggingExporter(): OtlpHttpLogRecordExporter {
        return OtlpHttpLogRecordExporter.getDefault().toBuilder()
            .build()
    }

    @Bean
    open fun loggingSpanExporter(): LoggingSpanExporter {
        return LoggingSpanExporter.create()
    }

    @Bean
    open fun observationRegistry(): ObservationRegistry {
        return ObservationRegistry.create()
    }

}