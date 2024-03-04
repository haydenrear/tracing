package com.hayden.tracing.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.hayden.tracing.entity.Event
import com.hayden.tracing.repository.EventRepository
import io.micrometer.common.KeyValue
import io.micrometer.observation.Observation
import io.micrometer.tracing.Span
import io.micrometer.tracing.Tracer
import io.micrometer.tracing.handler.DefaultTracingObservationHandler
import io.micrometer.tracing.otel.bridge.OtelTracer
import io.opentelemetry.instrumentation.jdbc.datasource.JdbcTelemetry
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class DelegatingCdcObservationHandler(tracer: OtelTracer,
                                      val eventRepository: EventRepository) : DefaultTracingObservationHandler(tracer) {


    override fun onStart(context: Observation.Context) {
        // can do anything here
        eventRepository.save(Event(context.getHighCardinalityKeyValue("trace").value))
        context.removeHighCardinalityKeyValue("trace")
        context.remove("trace")
        super.onStart(context)
    }

    override fun onStop(context: Observation.Context) {
        super.onStop(context)
    }
}