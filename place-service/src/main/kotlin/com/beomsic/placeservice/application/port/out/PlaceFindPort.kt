package com.beomsic.placeservice.application.port.out

import com.beomsic.placeservice.adapter.out.persistence.PlaceEntity

interface PlaceFindPort {
    suspend fun findAllByStoryId(storyId: Long): List<PlaceEntity>
    suspend fun findByPlaceId(placeId: Long): PlaceEntity
}