package com.hayden.tracing.config

import com.hayden.tracing.handler.DelegatingCdcObservationHandler
import com.hayden.tracing_apt.observation_aspects.AnnotationRegistrarObservabilityUtility
import com.hayden.tracing_apt.observation_aspects.DiObservationUtility
import com.hayden.tracing_apt.props.TracingConfigurationProperties
import com.hayden.utilitymodule.nullable.mapNullable
import com.hayden.utilitymodule.nullable.orElseGet
import io.micrometer.tracing.otel.bridge.OtelTracer
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.OpenTelemetrySdkBuilder
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.samplers.Sampler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.context.annotation.*
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.data.jdbc.core.convert.*
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext
import org.springframework.data.relational.core.dialect.Dialect
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.yaml.snakeyaml.Yaml


@AutoConfiguration
@Import(value=[
    org.springframework.boot.actuate.autoconfigure.tracing.OpenTelemetryAutoConfiguration::class,
    io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration::class,
    DatabaseConfiguration::class,
    LiquibaseAutoConfiguration::class,
    TracingResourceConfiguration::class
])
@ComponentScan(
    basePackageClasses = [
        DiObservationUtility::class,
        AnnotationRegistrarObservabilityUtility::class,
        DelegatingCdcObservationHandler::class,
        JdbcAnnotationConverter::class
    ],
    basePackages = ["com.hayden.tracing"]
)
@Profile("telemetry-logging")
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
            jdbcConverter, dialect)
        val factory  = DataAccessStrategyFactory(
            sqlGeneratorSource,
            jdbcConverter,
            operations,
            SqlParametersFactoryImpl(context, jdbcConverter),
            InsertStrategyFactory(operations, dialect))

        return factory.create()
    }

    @Bean
    @Primary
    open fun httpOtelSamplerAlwaysOn(): Sampler {
        return Sampler.alwaysOn()
    }

    @Bean
    open fun loggingSpanExporter(): LoggingSpanExporter {
        return LoggingSpanExporter.create()
    }

    @Bean
    open fun tracingConfigPropertiesAttributes(tracingConfigurationProperties: TracingConfigurationProperties?): Attributes
        = tracingConfigurationProperties
            .mapNullable { attrs(it) }
            .orElseGet {
                attrs(
                    PathMatchingResourcePatternResolver().getResource("classpath:otel.yml")
                        .inputStream
                        .use { Yaml().loadAs(it.readAllBytes().toString(Charsets.UTF_8), TracingConfigurationProperties::class.java); }
                    )
                    .orElseGet {
                        Attributes.builder()
                            .put("service.name", "default-service-id")
                            .put("service.instance-id", "default-service-instance-id")
                            .build()
                    }
            }

    private fun attrs(p: TracingConfigurationProperties)
        = p.serviceId
            .mapNullable {
                Pair(it, p.serviceInstanceId)
            }
            .mapNullable {
                Attributes.builder()
                    .put("service.name", it.first)
                    .put("service.instance-id", it.second)
                    .build()
            }




}