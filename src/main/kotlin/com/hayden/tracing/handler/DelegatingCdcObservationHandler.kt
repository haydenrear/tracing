package com.hayden.tracing.handler

//import com.hayden.tracingdb.entity.Event
//import com.hayden.tracingdb.repository.EventRepository
import com.hayden.utilitymodule.nullable.mapNullable
import com.hayden.utilitymodule.nullable.orElseGet
import io.micrometer.observation.Observation
import io.micrometer.tracing.handler.DefaultTracingObservationHandler
import io.micrometer.tracing.otel.bridge.OtelTracer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 * Listen on the database/Kafka and then receive on Kafka or HTTP
 */
@Component
open class DelegatingCdcObservationHandler(otelTracer: OtelTracer) : DefaultTracingObservationHandler(otelTracer) {

//    @Autowired
//    @Lazy
//    lateinit var eventRepository: com.hayden.tracingdb.repository.EventRepository

    override fun onStart(context: Observation.Context) {
//        eventRepository.findAll()
//        context.getHighCardinalityKeyValue("data")
//            .mapNullable { d ->
//                context.getHighCardinalityKeyValue("trace")
//                    .mapNullable { Pair(it, d) }
//            }.mapNullable {
//                eventRepository.save(
//                    com.hayden.tracingdb.entity.Event(
//                        it.first.value,
//                        it.second.value
//                    )
//                )
//                context.removeHighCardinalityKeyValue("data")
//                context.remove("data")
//        }.orElseGet {
////                eventRepository.save(Event())
//        }
        super.onStart(context)
    }

}