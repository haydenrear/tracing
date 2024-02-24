package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.*
import com.hayden.tracing.props.TracingConfigurationProperties
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.function.Supplier
import kotlin.reflect.KClass

@Component
class ObservationBehavior(
    private val observationRegistry: ObservationRegistry,
    private val observabilityUtility: ObservabilityUtility,
    private val tracingProps: TracingConfigurationProperties
) {

    data class ObservationArgs(val joinPoint: ProceedingJoinPoint,
                               val argumentExtractor: KClass<out ArgumentExtractor>,
                               val messageId: String,
                               val messageConsumer: KClass<out MessageConsumer>);

    fun doObservation(
        observationArgs: ObservationArgs
    ): Any? {
        val trace = Trace(
            Instant.now(),
            MessageMetadata(TraceMetadata(tracingProps.toServiceIds()), LogType.MESSAGE),
            Message(observabilityUtility.extract(observationArgs.argumentExtractor, observationArgs.joinPoint), observationArgs.messageId)
        )

        observabilityUtility.consumer(observationArgs.messageConsumer, trace)

        return Observation.createNotStarted(observationArgs.messageId, observationRegistry)
            .lowCardinalityKeyValue("trace", trace.toString())
            .observe(Supplier{ observationArgs.joinPoint.proceed() });
    }

}