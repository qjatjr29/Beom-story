package com.beomsic.placeservice.application.service

import com.beomsic.placeservice.application.port.`in`.usecase.PlaceFindUseCase
import com.beomsic.placeservice.application.port.out.PlaceFindPort
import com.beomsic.placeservice.domain.Place
import org.springframework.stereotype.Service

@Service
class PlaceFindService(
    private val placeFindPort: PlaceFindPort
): PlaceFindUseCase {

    override suspend fun findAllByStoryId(storyId: Long): List<Place> {
        return placeFindPort.findAllByStoryId(storyId)
    }

    override suspend fun findByPlaceId(placeId: Long): Place {
        return placeFindPort.findByPlaceId(placeId)
    }
}