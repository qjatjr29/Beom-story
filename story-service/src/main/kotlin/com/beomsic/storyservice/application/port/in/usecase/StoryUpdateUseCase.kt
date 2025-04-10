package com.beomsic.storyservice.application.port.`in`.usecase

import com.beomsic.storyservice.application.port.`in`.command.StoryUpdateCommand

interface StoryUpdateUseCase {
    suspend fun updateStatus(storyId: Long, userId: Long, status: String)
    suspend fun update(storyId: Long, userId: Long, command: StoryUpdateCommand)
}