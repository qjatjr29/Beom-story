package com.beomsic.storyservice.application.port.`in`.usecase

import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.domain.model.Story

interface StoryCreateUseCase {
    fun execute(storyCreateCommand: StoryCreateCommand): Story
}