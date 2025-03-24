package com.beomsic.storyservice.application.service

import com.beomsic.storyservice.application.port.`in`.usecase.StoryDeleteUseCase
import com.beomsic.storyservice.application.port.out.StoryDeletePort
import com.beomsic.storyservice.application.port.out.StoryOutboxPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoryDeleteService(
    val storyDeletePort: StoryDeletePort,
    val storyOutboxPort: StoryOutboxPort
) : StoryDeleteUseCase {

    @Transactional
    override fun execute(storyId: Long) {
        storyDeletePort.deleteStory(storyId)
        storyOutboxPort.saveOutboxMessage(storyId);
    }
}