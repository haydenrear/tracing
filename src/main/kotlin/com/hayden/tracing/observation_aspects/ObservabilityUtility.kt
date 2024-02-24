package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.Trace
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

@Component
class ObservabilityUtility{

    val arguments: MutableMap<KClass<out ArgumentExtractor>, ArgumentExtractor> = mutableMapOf();
    val consumer: MutableMap<KClass<out MessageConsumer>, MessageConsumer> = mutableMapOf();

    fun <T: Any> add(
        argumentExtractor: KClass<out T>,
        mutableMap: MutableMap<KClass<out T>, T>
    ) {
        if (!mutableMap.containsKey(argumentExtractor))
            mutableMap[argumentExtractor] = argumentExtractor.createInstance()
    }

    fun extract(argumentExtractor: KClass<out ArgumentExtractor>, proceeding: ProceedingJoinPoint): Map<String, *>? {
        add(argumentExtractor, arguments)
        return arguments[argumentExtractor]?.extract(proceeding)
    }

    fun consumer(argumentExtractor: KClass<out MessageConsumer>, trace: Trace) {
        add(argumentExtractor, consumer)
        consumer[argumentExtractor]?.consume(trace)
    }


}