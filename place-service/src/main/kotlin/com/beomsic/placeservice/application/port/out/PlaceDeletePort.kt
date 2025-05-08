package com.beomsic.placeservice.application.port.out

interface PlaceDeletePort {
    suspend fun deleteById(id: Long)
    suspend fun deleteAllByStoryId(storyId: Long)
}