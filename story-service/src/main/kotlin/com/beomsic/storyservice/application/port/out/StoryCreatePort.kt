package com.beomsic.storyservice.application.port.out

import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand

interface StoryCreatePort {
    suspend fun create(command: StoryCreateCommand)
}