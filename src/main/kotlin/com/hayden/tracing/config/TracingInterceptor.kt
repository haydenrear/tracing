package com.hayden.tracing.config

import com.hayden.tracing.model.ServiceIds
import org.springframework.stereotype.Component

interface ITracingInterceptor {
    fun test()
}

@Component
open class TracingInterceptor: ITracingInterceptor {


    override fun test(){
    }
}