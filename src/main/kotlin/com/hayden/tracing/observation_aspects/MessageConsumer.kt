package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.Trace

/**
 * Save to the database.
 */
interface MessageConsumer {

    fun consume(trace: Trace)

    class DefaultMessageConsumer: MessageConsumer {
        override fun consume(trace: Trace) {
        }

    }

}