package com.hayden.tracing.observation_aspects

import java.lang.reflect.Method

/**
 *
 */
interface Scanner {

    fun doAspect(method: Method, clazz: Class<*>): Boolean


}