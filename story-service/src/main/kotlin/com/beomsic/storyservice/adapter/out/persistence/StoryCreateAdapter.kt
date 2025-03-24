package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.application.port.out.StoryCreatePort
import com.beomsic.storyservice.infrastructure.persistence.StoryEntity
import com.beomsic.storyservice.infrastructure.persistence.StoryRepository
import org.springframework.stereotype.Component

@Component
class StoryCreateAdapter(
    private val storyRepository: StoryRepository
): StoryCreatePort {

    override fun create(command: StoryCreateCommand): StoryEntity {
        val entity = StoryEntity(
            authorId = command.authorId,
            title = command.title,
            description = command.description,
            category = command.category,
            startDate = command.startDate,
            endDate = command.endDate
        )
        return storyRepository.save(entity)
    }
}