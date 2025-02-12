package com.beomsic.placeservice.adapter.out.external.kafka

import com.beomsic.common.event.ImageDeleteEvent
import com.beomsic.placeservice.config.sendMessageWithCallback
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ImageDeleteEventSendService(
    @Value("\${kafka.topic.delete-image}") val deleteImageTopic: String,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) {
    private val logger = KotlinLogging.logger {}

    suspend fun send(placeCreateFailedEvent: ImageDeleteEvent) {
        try {
            kafkaTemplate.sendMessageWithCallback(deleteImageTopic, "", placeCreateFailedEvent)
        } catch (e: Exception) {
            logger.error(e.message, e)
        }
    }
}