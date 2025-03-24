package com.beomsic.storyservice.application.port.out

import com.beomsic.storyservice.infrastructure.persistence.StoryEntity
import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand

interface StoryCreatePort {
    fun create(command: StoryCreateCommand): StoryEntity
}