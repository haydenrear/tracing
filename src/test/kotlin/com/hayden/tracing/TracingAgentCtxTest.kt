package com.hayden.tracing

import com.hayden.tracing_agent.TracingAgent
import com.hayden.tracing_agent.advice.ContextHolder
import org.junit.jupiter.api.Test


open class TracingAgentCtxTest {



    @Test
    open fun doTextContext() {
        ContextHolder.getTracingService()
        TracingAgent.instrumentClass(TracingAgentCtxTest::class.java.name)
        for (i in 0..10) {
            doTest()
        }
        TracingAgent.revertInstrumentation(TracingAgentCtxTest::class.java.name);
        for (i in 0..10) {
            doTest()
        }
    }

    open fun doTest() {
        println("hello");
        Thread.sleep(1000)
    }

}