package com.beomsic.storyservice.application.port.out

interface StoryOutboxPort {
    suspend fun saveStoryDeleteMessage(storyId: Long)
}