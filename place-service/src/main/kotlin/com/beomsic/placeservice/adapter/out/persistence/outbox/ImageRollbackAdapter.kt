package com.beomsic.placeservice.adapter.out.persistence.outbox

import com.beomsic.placeservice.application.port.out.ImageRollbackOutboxPort
import org.springframework.stereotype.Component

@Component
class ImageRollbackAdapter(
    private val imageRollbackRepository: ImageRollbackRepository
): ImageRollbackOutboxPort {

    override suspend fun saveImageRollbackMessage(placeId: Long, imageUrl: String) {
        val outbox = ImageRollbackOutbox(
            placeId = placeId,
            imageUrl = imageUrl,
        )
        imageRollbackRepository.save(outbox)
    }

}