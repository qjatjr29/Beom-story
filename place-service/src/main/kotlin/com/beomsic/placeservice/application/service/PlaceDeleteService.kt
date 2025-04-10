package com.beomsic.placeservice.application.service

import com.beomsic.placeservice.application.port.`in`.usecase.PlaceDeleteUseCase
import com.beomsic.placeservice.application.port.out.ImageRollbackOutboxPort
import com.beomsic.placeservice.application.port.out.PlaceDeletePort
import com.beomsic.placeservice.application.port.out.PlaceFindPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlaceDeleteService(
    private val placeFindPort: PlaceFindPort,
    private val placeDeletePort: PlaceDeletePort,
    private val imageRollbackOutboxPort: ImageRollbackOutboxPort
): PlaceDeleteUseCase {

    @Transactional
    override suspend fun execute(id: Long) {
        val place = placeFindPort.findByPlaceId(id)
        placeDeletePort.deletePlace(id)
        place.imageUrl?.let { imageUrl ->
            imageRollbackOutboxPort.saveImageRollbackMessage(placeId = id, imageUrl = imageUrl) }
    }

    @Transactional
    override suspend fun deleteAllByStoryId(storyId: Long) {
        val places = placeFindPort.findAllByStoryId(storyId)
        placeDeletePort.deleteAllByStoryId(storyId)
        places.mapNotNull { place ->
            place.imageUrl?.let { imageUrl ->
                place.id?.let { id ->
                    imageRollbackOutboxPort.saveImageRollbackMessage(placeId = id, imageUrl = imageUrl)
                }
            }
        }
    }
}