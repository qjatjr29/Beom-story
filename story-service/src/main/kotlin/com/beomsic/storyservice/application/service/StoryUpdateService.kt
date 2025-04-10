package com.beomsic.storyservice.application.service

import com.beomsic.storyservice.application.port.`in`.command.StoryUpdateCommand
import com.beomsic.storyservice.application.port.`in`.usecase.StoryUpdateUseCase
import com.beomsic.storyservice.application.port.out.StoryUpdatePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoryUpdateService(
    private val storyUpdatePort : StoryUpdatePort
): StoryUpdateUseCase {

    @Transactional
    override suspend fun updateStatus(storyId: Long, userId: Long, status: String) {
        storyUpdatePort.updateStatus(storyId, userId, status)
    }

    @Transactional
    override suspend fun update(storyId: Long, userId: Long, command: StoryUpdateCommand) {
        storyUpdatePort.update(storyId, userId, command)
    }
}