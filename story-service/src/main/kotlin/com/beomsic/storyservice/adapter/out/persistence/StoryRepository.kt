package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoryRepository : CoroutineCrudRepository<StoryEntity, Long>, StoryPaginationRepository {
}

suspend fun StoryRepository.findByIdOrNull(id: Long): StoryEntity {
    return findById(id) ?: throw StoryNotFoundException()
}