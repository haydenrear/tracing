package com.hayden.tracing.config

import com.hayden.utilitymodule.telemetry.log.AttributeProvider
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.sdk.resources.Resource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.*
import java.util.*


@AutoConfiguration
@Profile("telemetry-logging")
open class TracingResourceConfiguration {

    companion object {
        val log: Logger = LoggerFactory.getLogger(TracingResourceConfiguration::class.java.name)
    }

    @Bean
    open fun otelResource(@Autowired(required = false) attributes: List<Attributes>?,
                          @Autowired(required = false) attributesList: List<List<Attributes>>?,
                          @Autowired(required = false) attributesProvider: List<AttributeProvider>?): Resource {
        val attributesBuilder = Attributes.builder()

        fun logMessage(attributes: Attributes) = log.info("Creating resource by adding attributes {}", attributes.asMap())

        Optional.ofNullable(attributes).stream()
            .flatMap { it.stream() }
            .peek { logMessage(it) }
            .forEach { attributesBuilder.putAll(it) }

        Optional.ofNullable(attributesList)
            .stream()
            .flatMap { it.stream() }
            .flatMap { it.stream() }
            .peek { logMessage(it) }
            .forEach { attributesBuilder.putAll(it) }

        Optional.ofNullable(attributesProvider)
            .stream()
            .flatMap { it.stream() }
            .flatMap { it.attributes.stream() }
            .peek { logMessage(it) }
            .forEach { attributesBuilder.putAll(it) }

        return Resource.create(attributesBuilder.build())
    }



}