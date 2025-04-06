package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.application.port.out.PlaceFindPort
import com.beomsic.placeservice.domain.Place
import org.springframework.stereotype.Component

@Component
class PlaceFindAdapter(
    private val placeRepository: PlaceRepository
): PlaceFindPort {

    override suspend fun findAllByStoryId(storyId: Long): List<Place> {
        return placeRepository.findAllByStoryId(storyId).map { it.toDomain() }
    }

    override suspend fun findByPlaceId(placeId: Long): Place {
        return placeRepository.findByIdOrNull(placeId).toDomain()
    }
}