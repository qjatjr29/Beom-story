package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.application.port.out.PlaceDeletePort
import org.springframework.stereotype.Component

@Component
class PlaceDeleteAdapter(
    private val placeRepository: PlaceRepository,
): PlaceDeletePort {

    override suspend fun deletePlace(id: Long) {
        placeRepository.deleteById(id)
    }

    override suspend fun deleteAllByStoryId(storyId: Long) {
        placeRepository.deleteAllByStoryId(storyId)
    }
}