package com.beomsic.storyservice.application.service

import com.beomsic.storyservice.adapter.out.service.UserWebClient
import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.application.port.`in`.usecase.StoryCreateUseCase
import com.beomsic.storyservice.application.port.out.StoryCreatePort
import com.beomsic.storyservice.domain.model.Story
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoryCreateService(
    private val storyCreatePort: StoryCreatePort,
    private val userWebClient: UserWebClient,
): StoryCreateUseCase {

    @Transactional
    override suspend fun execute(storyCreateCommand: StoryCreateCommand): Story {
        userWebClient.findById(storyCreateCommand.authorId)
        return storyCreatePort.create(storyCreateCommand)
    }
}