package com.beomsic.storyservice.application.port.out

interface StoryOutboxPort {
    fun saveOutboxMessage(storyId: Long)
}