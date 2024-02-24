package com.hayden.tracing.observation_aspects

import lombok.extern.java.Log
import org.aspectj.lang.ProceedingJoinPoint
import kotlin.math.max
import kotlin.reflect.full.memberProperties

interface ArgumentExtractor {

    class DefaultArgumentExtractor: ArgumentExtractor {
        override fun extract(proceeding: ProceedingJoinPoint): Map<String, *> {
            return mutableMapOf<String, String>()
        }
    }

    fun extract(proceeding: ProceedingJoinPoint): Map<String, *>

}