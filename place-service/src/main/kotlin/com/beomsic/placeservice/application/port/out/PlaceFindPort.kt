package com.beomsic.placeservice.application.port.out

import com.beomsic.placeservice.domain.Place

interface PlaceFindPort {
    suspend fun findAllByStoryId(storyId: Long): List<Place>
    suspend fun findByPlaceId(placeId: Long): Place
}