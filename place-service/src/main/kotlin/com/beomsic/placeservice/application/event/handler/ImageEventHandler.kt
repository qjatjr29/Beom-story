package com.beomsic.placeservice.application.event.handler

import com.beomsic.common.application.event.EventHandler
import com.beomsic.common.infra.kafka.event.ImageRollbackEvent
import com.beomsic.common.infra.kafka.event.KafkaEvent
import com.beomsic.placeservice.infra.config.sendMessageWithCallback
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ImageEventHandler(
    @Value("\${kafka.topic.rollback-image}") val TOPIC_ROLLBACK_IMAGE: String,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
): EventHandler<KafkaEvent> {

    override fun eventType(): Class<KafkaEvent> = KafkaEvent::class.java
    override fun handle(event: KafkaEvent) {
        when (event) {
            is ImageRollbackEvent -> kafkaTemplate.sendMessageWithCallback(TOPIC_ROLLBACK_IMAGE, event.eventId, event)
            else -> return
        }
    }
}