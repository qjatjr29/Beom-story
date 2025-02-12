package com.beomsic.placeservice.application.port.`in`.usecase

import com.beomsic.placeservice.domain.Place

interface PlaceFindUseCase {
    suspend fun findAllByStoryId(storyId: Long): List<Place>
    suspend fun findByPlaceId(placeId: Long): Place
}