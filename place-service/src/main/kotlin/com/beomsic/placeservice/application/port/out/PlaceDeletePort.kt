package com.beomsic.placeservice.application.port.out

interface PlaceDeletePort {
    suspend fun deletePlace(id: Long)
    suspend fun deleteAllByStoryId(storyId: Long)
}