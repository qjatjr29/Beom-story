package com.beomsic.imageservice.application.service

import com.beomsic.imageservice.application.port.`in`.usecase.DeleteUseCase
import com.beomsic.imageservice.application.port.out.DeletePort
import org.springframework.stereotype.Service

@Service
class DeleteService(
    private val deletePort: DeletePort
): DeleteUseCase {

    override suspend fun deleteImage(imageUrl: String) {
        deletePort.deleteImage(imageUrl)
    }
}