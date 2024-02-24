package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.*
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
open class CdcObservabilityAspect(
    private val observation: ObservationBehavior
) {



    @Around("@annotation(cdc)")
    @Throws(Throwable::class)
    public open fun doCdc(joinPoint: ProceedingJoinPoint, cdc: Cdc): Any? {
        return observation.doObservation(ObservationBehavior.ObservationArgs(joinPoint, cdc.argumentExtractor, cdc.messageId, cdc.messageConsumer))
    }
}
