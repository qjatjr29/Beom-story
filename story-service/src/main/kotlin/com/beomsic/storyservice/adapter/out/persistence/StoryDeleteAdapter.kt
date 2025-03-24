package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.application.port.out.StoryDeletePort
import com.beomsic.storyservice.infrastructure.persistence.StoryRepository
import org.springframework.stereotype.Component

@Component
class StoryDeleteAdapter (
    private val storyRepository: StoryRepository
) : StoryDeletePort {

    override fun deleteStory(storyId: Long) {
        storyRepository.deleteById(storyId)
    }
}