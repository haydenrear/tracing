package com.hayden.tracing.config

import com.hayden.tracing.model.ServiceIds
import com.hayden.tracing.observation_aspects.Cdc
import org.springframework.stereotype.Component

interface ITracingInterceptor {
    fun test()
}

@Component
open class TracingInterceptor: ITracingInterceptor {


    @Cdc(messageId = "hello")
    override fun test(){
    }
}