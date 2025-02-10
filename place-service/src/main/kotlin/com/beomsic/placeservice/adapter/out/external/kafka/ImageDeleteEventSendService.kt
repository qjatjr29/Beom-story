package com.beomsic.placeservice.adapter.out.external.kafka

import com.beomsic.common.event.ImageDeleteEvent
//import com.beomsic.placeservice.adapter.out.external.event.ImageDeleteEvent
import com.beomsic.placeservice.config.sendMessageWithCallback
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ImageDeleteEventSendService(
    @Value("\${kafka.topic.delete-image}") val deleteImageTopic: String,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) {
    suspend fun send(placeCreateFailedEvent: ImageDeleteEvent) {
        try {
            kafkaTemplate.sendMessageWithCallback(deleteImageTopic, "", placeCreateFailedEvent)
            println("kafka publish success")
        } catch (e: Exception) {
            println(e.message)
            println("kafka publish fail")
        }
    }
}