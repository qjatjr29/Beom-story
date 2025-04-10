package com.beomsic.imageservice.adapter.`in`.kafka

import com.beomsic.imageservice.application.port.`in`.usecase.DeleteUseCase
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaMessageListener(
    private val deleteUseCase: DeleteUseCase
){
    @KafkaListener(topics = ["\${kafka.topic.rollback-image}"])
    suspend fun handleImageRollbackEvent(imageUrl: String) {
        deleteUseCase.deleteImage(imageUrl)
    }
}