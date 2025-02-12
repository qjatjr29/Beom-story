package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.application.port.out.PlaceFindPort
import org.springframework.stereotype.Component

@Component
class PlaceFindAdapter(
    private val placeRepository: PlaceRepository
): PlaceFindPort {

    override suspend fun findAllByStoryId(storyId: Long): List<PlaceEntity> {
        return placeRepository.findAllByStoryId(storyId)
    }

    override suspend fun findByPlaceId(placeId: Long): PlaceEntity {
        return placeRepository.findByIdOrNull(placeId)
    }
}