package com.beomsic.imageservice.application.service

import org.springframework.stereotype.Component

@Component
class KafkaListenerService(
    private val deleteService: DeleteService,
) {

    suspend fun rollbackImage(imageUrl: String) {
        deleteService.deleteImage(imageUrl)
    }
}