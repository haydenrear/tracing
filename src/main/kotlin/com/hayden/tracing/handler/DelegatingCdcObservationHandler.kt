package com.hayden.tracing.handler

import com.hayden.tracing.entity.Event
import com.hayden.tracing.repository.EventRepository
import io.micrometer.observation.Observation
import io.micrometer.tracing.Tracer
import io.micrometer.tracing.handler.DefaultTracingObservationHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
open class DelegatingCdcObservationHandler(otelTracer: Tracer) : DefaultTracingObservationHandler(otelTracer) {

    @Autowired
    @Lazy
    lateinit var eventRepository: EventRepository

    override fun onStart(context: Observation.Context) {
        // can do anything here
//        eventRepository.save(Event(
//            context.getHighCardinalityKeyValue("data").value,
//            context.getHighCardinalityKeyValue("trace").value
//        ))
//        context.removeHighCardinalityKeyValue("data")
//        context.remove("data")
        super.onStart(context)
    }

}