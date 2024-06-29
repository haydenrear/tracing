package com.hayden.tracing.config

import org.springframework.stereotype.Component
import com.hayden.tracing.Logged;

interface ITracingInterceptor {
    fun doTestValue()
}

@Component
open class TracingInterceptor: ITracingInterceptor {
    @Logged(logId = "log")
    override fun doTestValue(){
    }
}