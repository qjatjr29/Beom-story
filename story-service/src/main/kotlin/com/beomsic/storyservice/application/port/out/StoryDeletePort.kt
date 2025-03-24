package com.beomsic.storyservice.application.port.out

interface StoryDeletePort {
    fun deleteStory(storyId: Long)
}