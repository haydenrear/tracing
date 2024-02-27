package com.hayden.tracing.config

import com.hayden.tracing.model.ServiceIds
import com.hayden.tracing_apt.Logged
import org.springframework.stereotype.Component

interface ITracingInterceptor {
    fun test()
}

@Component
open class TracingInterceptor: ITracingInterceptor {


    @Logged(logId = "log")
    override fun test(){
    }
}