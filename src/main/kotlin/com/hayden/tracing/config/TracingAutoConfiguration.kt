package com.hayden.tracing.config

import com.hayden.tracing.observation_aspects.AnnotationRegistrarObservabilityUtility
import com.hayden.tracing.props.TracingConfigurationProperties
import io.micrometer.context.ContextRegistry
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.transport.SenderContext
import io.micrometer.tracing.Tracer
import io.micrometer.tracing.handler.PropagatingSenderTracingObservationHandler
import io.micrometer.tracing.propagation.Propagator
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporterBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy


@Configuration
@ComponentScan(basePackageClasses = [AnnotationRegistrarObservabilityUtility::class, TracingInterceptor::class])
@EnableConfigurationProperties(TracingConfigurationProperties::class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
open class TracingAutoConfiguration {


    @Bean
    open fun httpSpanExporter(): OtlpHttpSpanExporter {
        return OtlpHttpSpanExporter.builder()
            .build()
    }

    @Bean
    open fun httpMetricsExporter(): OtlpHttpMetricExporter {
        return OtlpHttpMetricExporter.builder()
            .build()
    }


    @Bean
    open fun httpLoggingExporter(): OtlpHttpLogRecordExporter {
        return OtlpHttpLogRecordExporter.builder()
            .build()
    }

    @Bean
    open fun loggingSpanExporter(): LoggingSpanExporter {
        return LoggingSpanExporter.create()
    }

    @Bean
    open fun observationRegistry(): ObservationRegistry {
        val create = ObservationRegistry.create()
        return create
    }

}