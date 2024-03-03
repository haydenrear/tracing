package com.hayden.tracing.config

import com.hayden.tracing_apt.Logged
import org.springframework.stereotype.Component

interface ITracingInterceptor {
    fun doTestValue()
}

@Component
open class TracingInterceptor: ITracingInterceptor {
    @Logged(logId = "log")
    override fun doTestValue(){
    }
}