package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.application.port.out.StoryFindPort
import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import com.beomsic.storyservice.domain.model.Story
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class StoryFindAdapter(
    private val storyRepository: StoryRepository
): StoryFindPort {
    override suspend fun getById(id: Long): Story {
        val storyEntity = storyRepository.findById(id) ?: throw StoryNotFoundException()
        return storyEntity.toDomain()
    }

    override suspend fun findAll(pageable: Pageable): Page<Story> {
        return storyRepository.findAllWithPaging(pageable).map { it.toDomain() }
    }

    override suspend fun findAllByKeyword(keyword: String, pageable: Pageable): Page<Story> {
        return storyRepository.findAllByKeywordWithPaging(keyword, pageable).map { it.toDomain() }
    }

    override suspend fun findAllByUserId(userId: Long, pageable: Pageable): Page<Story> {
        return storyRepository.findAllByUserIdWithPaging(userId, pageable).map { it.toDomain() }
    }

    override suspend fun findAllByUserIdAndStatus(userId: Long, status: String, pageable: Pageable): Page<Story> {
        return storyRepository.findAllByUserIdAndStatusWithPaging(userId, status, pageable).map { it.toDomain() }
    }
}