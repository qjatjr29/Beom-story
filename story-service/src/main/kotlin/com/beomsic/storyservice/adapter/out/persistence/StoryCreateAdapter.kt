package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.application.port.out.StoryCreatePort
import org.springframework.stereotype.Component

@Component
class StoryCreateAdapter(
    private val storyRepository: StoryRepository
): StoryCreatePort {

    override suspend fun create(command: StoryCreateCommand): StoryEntity {
        val entity = StoryEntity(
            authorId = command.authorId,
            title = command.title,
            description = command.description,
            startDate = command.startDate,
            endDate = command.endDate
        )
        return storyRepository.save(entity)
    }
}