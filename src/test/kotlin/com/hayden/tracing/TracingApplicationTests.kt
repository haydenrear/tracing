package com.hayden.tracing

import com.hayden.tracing.config.ITracingInterceptor
import com.hayden.tracing.config.TracingAutoConfiguration
import com.hayden.tracing.model.*
import com.hayden.tracing.observation_aspects.AnnotationRegistrarObservabilityUtility
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
import org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant

@SpringBootTest(classes = [
	TracingAutoConfiguration::class,
	AspectJAwareAdvisorAutoProxyCreator::class,
	AnnotationAwareAspectJAutoProxyCreator::class
])
@ExtendWith(SpringExtension::class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
open class TracingApplicationTests {
	companion object {
		private val log: org.slf4j.Logger? = LoggerFactory.getLogger(this::class.java)
	}

	@Autowired
	lateinit var tracingConfigurationProperties: ITracingInterceptor


	@Test
	fun contextLoads() {
		val ex: JavaReflectionArgumentExtractor = JavaReflectionArgumentExtractor();
		var extracted = ex.extractRecursive(
			Trace(
				Instant.now(),
				MessageMetadata(TraceMetadata(ServiceIds("one", "two")), LogType.MESSAGE),
				Message(mutableMapOf(Pair("one", "two")) as Map<String, *>, "one-two"),

			), 3, 0, AnnotationRegistrarObservabilityUtility());
		println(extracted.toString())

		log?.info("hello!")
		for (i in 1..10) {
			callThis()
			tracingConfigurationProperties.test()
		}
	}

//	@WithSpan(value = "call-this")
	public open fun callThis() {
	}

}
