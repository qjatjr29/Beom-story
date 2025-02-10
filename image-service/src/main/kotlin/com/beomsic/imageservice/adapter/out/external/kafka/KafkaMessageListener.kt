package com.beomsic.imageservice.adapter.out.external.kafka

import com.beomsic.common.event.ImageDeleteEvent
import com.beomsic.imageservice.application.service.KafkaListenerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaMessageListener(
    @Value("\${kafka.topic.delete-image}") val deleteImageTopic: String,
    private val kafkaListenerService: KafkaListenerService
){
    @KafkaListener(topics = ["\${kafka.topic.delete-image}"], groupId = "group-id")
    suspend fun handlePlaceCreationFailedEvent(event: ImageDeleteEvent) {
        kafkaListenerService.deleteImage(event.imageUrl)
    }
}