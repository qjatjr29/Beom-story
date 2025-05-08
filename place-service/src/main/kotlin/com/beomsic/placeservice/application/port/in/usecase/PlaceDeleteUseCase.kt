package com.beomsic.placeservice.application.port.`in`.usecase

interface PlaceDeleteUseCase {
    suspend fun deleteById(placeId: Long, userId: Long)
    suspend fun deleteAllByStoryId(storyId: Long)
}