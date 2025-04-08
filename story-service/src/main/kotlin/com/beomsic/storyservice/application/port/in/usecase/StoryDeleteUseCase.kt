package com.beomsic.storyservice.application.port.`in`.usecase

interface StoryDeleteUseCase {
    suspend fun execute(userId: Long, storyId: Long)
}