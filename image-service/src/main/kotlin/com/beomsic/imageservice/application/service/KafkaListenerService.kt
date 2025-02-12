package com.beomsic.imageservice.application.service

import org.springframework.stereotype.Component

@Component
class KafkaListenerService(
    private val deleteService: DeleteService,
) {

    suspend fun deleteImage(imageUrl: String) {
        deleteService.deleteImage(imageUrl)
    }
}