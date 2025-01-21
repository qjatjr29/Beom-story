package com.beomsic.storyservice.application.port.`in`.usecase

import com.beomsic.storyservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand

interface StoryCreateUseCase {
    suspend fun execute(storyCreateCommand: StoryCreateCommand, placeCreateCommands: List<PlaceCreateCommand>?)
}