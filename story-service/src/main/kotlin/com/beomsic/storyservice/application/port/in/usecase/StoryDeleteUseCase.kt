package com.beomsic.storyservice.application.port.`in`.usecase

interface StoryDeleteUseCase {
    fun execute(storyId: Long)
}