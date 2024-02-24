package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.*
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
open class ScanningObservabilityAspect(
    private val observation: ObservationBehavior
) {



    @Around("execution ${scanning.observability.aspect}")
    @Throws(Throwable::class)
    public open fun doCdc(joinPoint: ProceedingJoinPoint, cdc: Cdc): Any? {
        return observation.doObservation(ObservationBehavior.ObservationArgs(joinPoint, cdc.argumentExtractor, cdc.messageId, cdc.messageConsumer))
    }
}
