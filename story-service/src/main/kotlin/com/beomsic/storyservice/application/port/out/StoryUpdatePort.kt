package com.beomsic.storyservice.application.port.out

import com.beomsic.storyservice.application.port.`in`.command.StoryUpdateCommand

interface StoryUpdatePort {
    suspend fun updateStatus(storyId: Long, userId: Long, status: String)
    suspend fun update(storyId: Long, userId: Long, updateCommand: StoryUpdateCommand)
}