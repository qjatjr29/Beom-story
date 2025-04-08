package com.beomsic.storyservice.application.port.out

interface StoryDeletePort {
    suspend fun deleteStory(userId: Long, storyId: Long)
}