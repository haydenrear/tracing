package com.hayden.tracing

import com.hayden.tracing.config.DatabaseConfiguration
import com.hayden.tracing.config.TracingAutoConfiguration
import com.hayden.tracing_apt.props.TracingConfigurationProperties
import io.opentelemetry.api.trace.SpanBuilder
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.instrumentation.annotations.WithSpan
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import liquibase.Liquibase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
import org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.core.io.ResourceLoader
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.sql.DataSource

@SpringBootTest(classes = [
	TracingAutoConfiguration::class,
	AspectJAwareAdvisorAutoProxyCreator::class,
	AnnotationAwareAspectJAutoProxyCreator::class,
	DatabaseConfiguration::class
])
@ExtendWith(SpringExtension::class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties(TracingConfigurationProperties::class)
open class TracingApplicationTests {
	companion object {
		private val log: org.slf4j.Logger? = LoggerFactory.getLogger(this::class.java)
	}

	@Autowired
	lateinit var span: OtlpHttpSpanExporter;

	@Autowired
	lateinit var otelSpanProcessor: BatchSpanProcessor
	@Autowired
	lateinit var sdkTracerProvider: SdkTracerProvider
	@Autowired
	lateinit var datasource: DataSource
	@Autowired
	lateinit var resourceLoader: ResourceLoader

	var liquibase: Liquibase? = null


//	@Test
	fun contextLoads() {
		log?.info("hello!")
		for (i in 1..10) {
			otelSpanProcessor.forceFlush()
			sdkTracerProvider.forceFlush()
		}
		otelSpanProcessor.forceFlush()
		sdkTracerProvider.forceFlush()
		Thread.sleep(5000)
	}

	@WithSpan(value = "call-this")
	public open fun callThis() {
		val s: SpanBuilder
	}

}
