package com.hayden.tracing.config

import com.hayden.tracing.handler.DelegatingCdcObservationHandler
import com.hayden.tracing.observation_aspects.AnnotationRegistrarObservabilityUtility
import com.hayden.tracing.props.TracingConfigurationProperties
import com.hayden.tracing.repository.EventRepository
import com.hayden.tracing_agent.config.TracingProperties
import io.micrometer.observation.ObservationRegistry
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.sdk.resources.Resource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.autoconfigure.tracing.SpanExporters
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.*
import org.springframework.data.jdbc.core.convert.*
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext
import org.springframework.data.relational.core.dialect.Dialect
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations


@Configuration
@Import(value=[
    org.springframework.boot.actuate.autoconfigure.tracing.OpenTelemetryAutoConfiguration::class,
    io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration::class,
    DatabaseConfiguration::class,
    LiquibaseAutoConfiguration::class
])
@ComponentScan(basePackageClasses = [
    AnnotationRegistrarObservabilityUtility::class,
    TracingInterceptor::class,
    DelegatingCdcObservationHandler::class,
    EventRepository::class
], basePackages = ["com.hayden.tracing"])
@PropertySource(value = ["classpath:application.yml"], factory = YamlPropertySourceFactory::class)
open class TracingAutoConfiguration {

    companion object {
        val log: Logger = LoggerFactory.getLogger(TracingAutoConfiguration::class.java.name)
    }

    @Bean
    @Primary
    open fun dataAccessStrategy(operations: NamedParameterJdbcOperations,
                                jdbcConverter: JdbcConverter,
                                context: JdbcMappingContext,
                                dialect: Dialect): DataAccessStrategy {
        val sqlGeneratorSource = SqlGeneratorSource(context,
            jdbcConverter, dialect);
        val factory  = DataAccessStrategyFactory(
            sqlGeneratorSource,
            jdbcConverter,
            operations,
            SqlParametersFactoryImpl(context, jdbcConverter),
            InsertStrategyFactory(operations, dialect));

        return factory.create();
    }

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
    open fun otelResource(attributes: List<Attributes>): Resource {
        val attributesBuilder = Attributes.builder()
        attributes.stream()
            .peek { log.info("Creating resource by adding attributes {}", it.asMap()) }
            .forEach { attributesBuilder.putAll(it) }

        return Resource.create(attributesBuilder.build())
    }

    @Bean
    open fun spanExporters(): SpanExporters {
        return SpanExporters.of(httpSpanExporter())
    }

    @Bean
    open fun observationRegistry(delegating: DelegatingCdcObservationHandler): ObservationRegistry {
        val create = ObservationRegistry.create()
        create.observationConfig().observationHandler(delegating)
        return create
    }


}