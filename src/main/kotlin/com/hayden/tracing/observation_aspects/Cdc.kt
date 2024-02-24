package com.hayden.tracing.observation_aspects

import kotlin.reflect.KClass


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.PROPERTY)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class Cdc(
    val messageId: String,
    val argumentExtractor: KClass<out ArgumentExtractor> = ArgumentExtractor.DefaultArgumentExtractor::class,
    val messageConsumer: KClass<out MessageConsumer> = MessageConsumer.DefaultMessageConsumer::class
)