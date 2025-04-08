package com.beomsic.imageservice.adapter.out.external.kafka

import com.beomsic.common.infra.kafka.event.ImageRollbackEvent
import com.beomsic.common.infra.kafka.event.KafkaEvent
import com.beomsic.imageservice.application.service.KafkaListenerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaMessageListener(
    @Value("\${kafka.topic.rollback-image}") val TOPIC_ROLLBACK_IMAGE: String,
    private val kafkaListenerService: KafkaListenerService
){
    @KafkaListener(topics = ["\${kafka.topic.rollback-image}"], groupId = "group-id")
    suspend fun handlePlaceCreationFailedEvent(event: KafkaEvent) {
        when (event) {
            is ImageRollbackEvent -> kafkaListenerService.rollbackImage(event.imageUrl)
        }
    }
}