package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.application.port.out.StoryDeletePort
import com.beomsic.storyservice.domain.exception.UnauthorizedStoryAccessException
import org.springframework.stereotype.Component

@Component
class StoryDeleteAdapter (
    private val storyRepository: StoryRepository
) : StoryDeletePort {

    override suspend fun deleteStory(userId: Long, storyId: Long) {
        val entity = storyRepository.findByIdOrNull(storyId)
        if (entity.authorId != userId) throw UnauthorizedStoryAccessException()
        storyRepository.deleteById(storyId)
    }
}