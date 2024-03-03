package com.hayden.tracing.config

import com.hayden.tracing.observation_aspects.AnnotationRegistrarObservabilityUtility
import com.hayden.tracing.props.TracingConfigurationProperties
import io.micrometer.observation.ObservationRegistry
import io.micrometer.tracing.exporter.SpanExportingPredicate
import io.micrometer.tracing.exporter.SpanFilter
import io.micrometer.tracing.exporter.SpanReporter
import io.micrometer.tracing.handler.DefaultTracingObservationHandler
import io.micrometer.tracing.otel.bridge.CompositeSpanExporter
import io.micrometer.tracing.otel.bridge.OtelTracer
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.MeterProvider
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.context.propagation.TextMapPropagator
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.extension.trace.propagation.OtTracePropagator
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.actuate.autoconfigure.tracing.SpanExporters
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Primary
import java.time.Duration


@Configuration
@ImportAutoConfiguration(value=[
    org.springframework.boot.actuate.autoconfigure.tracing.OpenTelemetryAutoConfiguration::class,
    io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration::class
])
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
    open fun tracingConfigPropertiesAttributes(tracingConfigurationProperties: TracingConfigurationProperties): Attributes {
        return Attributes.builder()
            .put("service.name", tracingConfigurationProperties.serviceId)
            .put("service.instance-id", tracingConfigurationProperties.serviceInstanceId)
            .build()
    }

    @Bean
    open fun resource(attributes: List<Attributes>): Resource {
        val attributesBuilder = Attributes.builder();
        attributes.stream()
            .forEach { attributesBuilder.putAll(it) }

        return Resource.create(attributesBuilder.build())
    }

    @Bean
    open fun spanExporters(): SpanExporters {
        return SpanExporters.of(httpSpanExporter())
    }

    @Bean
    open fun contextPropagators(): ContextPropagators {
        return ContextPropagators.create(
            TextMapPropagator.composite(
                W3CBaggagePropagator.getInstance(),
                OtTracePropagator.getInstance()
        ))
    }

    @Bean
    open fun observationRegistry(otelTracer: OtelTracer): ObservationRegistry {
        val create = ObservationRegistry.create()
        create.observationConfig().observationHandler(DefaultTracingObservationHandler(otelTracer))
        return create
    }


}