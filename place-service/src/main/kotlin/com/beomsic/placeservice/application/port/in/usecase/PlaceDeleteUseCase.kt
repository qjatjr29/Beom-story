package com.beomsic.placeservice.application.port.`in`.usecase

interface PlaceDeleteUseCase {
    suspend fun execute(id: Long)
    suspend fun deleteAllByStoryId(storyId: Long)
}