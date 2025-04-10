package com.beomsic.storyservice.application.port.out

import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.domain.model.Story

interface StoryCreatePort {
    suspend fun create(command: StoryCreateCommand): Story
}