package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.application.port.out.StoryCreatePort
import com.beomsic.storyservice.domain.model.Story
import com.beomsic.storyservice.domain.model.StoryStatus
import org.springframework.stereotype.Component

@Component
class StoryCreateAdapter(
    private val storyRepository: StoryRepository
): StoryCreatePort {

    override suspend fun create(command: StoryCreateCommand): Story {
        val entity = StoryEntity(
            authorId = command.authorId,
            title = command.title,
            description = command.description,
            category = command.category.name,
            startDate = command.startDate,
            endDate = command.endDate,
            status = StoryStatus.DRAFT.name,
        )
        return storyRepository.save(entity).toDomain()
    }
}