package com.beomsic.common.infra.kafka.event

import java.util.*

sealed interface KafkaEvent {
    val eventId: String
}

data class ImageRollbackEvent(
    val imageUrl: String,
    override val eventId: String = UUID.randomUUID().toString()
): KafkaEvent
