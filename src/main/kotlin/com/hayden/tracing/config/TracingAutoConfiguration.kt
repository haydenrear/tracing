package com.hayden.tracing.config

import com.hayden.tracing_apt.observation_aspects.AnnotationRegistrarObservabilityUtility
import com.hayden.tracing_apt.observation_aspects.DiObservationUtility
import com.hayden.tracing_apt.props.TracingConfigurationProperties
import com.hayden.utilitymodule.nullable.mapNullable
import com.hayden.utilitymodule.nullable.orElseGet
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.sdk.trace.samplers.Sampler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.*
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.yaml.snakeyaml.Yaml


@AutoConfiguration
@Import(value=[
    org.springframework.boot.actuate.autoconfigure.tracing.OpenTelemetryAutoConfiguration::class,
    io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration::class,
    TracingResourceConfiguration::class
])
@ComponentScan(
    basePackageClasses = [
        DiObservationUtility::class,
        AnnotationRegistrarObservabilityUtility::class
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
    open fun httpOtelSamplerAlwaysOn(): Sampler {
        return Sampler.alwaysOn()
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