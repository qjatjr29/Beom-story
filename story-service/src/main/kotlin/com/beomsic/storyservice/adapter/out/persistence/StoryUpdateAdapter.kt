package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.application.port.`in`.command.StoryUpdateCommand
import com.beomsic.storyservice.application.port.out.StoryUpdatePort
import com.beomsic.storyservice.domain.exception.UnauthorizedStoryAccessException
import com.beomsic.storyservice.domain.model.Story
import com.beomsic.storyservice.domain.model.StoryStatus
import org.springframework.stereotype.Component

@Component
class StoryUpdateAdapter(
    private val storyRepository: StoryRepository
): StoryUpdatePort {

    override suspend fun updateStatus(storyId: Long, userId: Long, status: String) {
        updateStory(storyId) { storyEntity ->
            if (storyEntity.authorId != userId) throw UnauthorizedStoryAccessException()
            storyRepository.save(storyEntity.copy(status = StoryStatus.fromValue(status).name)).toDomain()
        }
    }

    override suspend fun update(storyId: Long, userId: Long, updateCommand: StoryUpdateCommand) {
        updateStory(storyId) { storyEntity ->
            if (storyEntity.authorId != userId) throw UnauthorizedStoryAccessException()
            storyRepository.save(
                storyEntity.copy(
                    title = updateCommand.title,
                    description = updateCommand.description,
                    category = updateCommand.category.name,
                    startDate = updateCommand.startDate,
                    endDate = updateCommand.endDate
                )
            ).toDomain()
        }
    }

    private suspend fun updateStory(id: Long, updateAction: suspend (StoryEntity) -> Story): Story {
        val storyEntity = storyRepository.findByIdOrNull(id)
        return updateAction(storyEntity)
    }
}