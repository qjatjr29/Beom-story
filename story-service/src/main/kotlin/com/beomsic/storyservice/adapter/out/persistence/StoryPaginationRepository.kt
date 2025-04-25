package com.beomsic.storyservice.adapter.out.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface StoryPaginationRepository {
    suspend fun findAllWithPaging(pageable: Pageable): Page<StoryEntity>
    suspend fun findArchivedStoriesWithPaging(pageable: Pageable): Page<StoryEntity>
    suspend fun findAllByKeywordWithPaging(keyword: String, pageable: Pageable): Page<StoryEntity>
    suspend fun findAllByUserIdWithPaging(userId: Long, pageable: Pageable): Page<StoryEntity>
    suspend fun findAllByUserIdAndStatusWithPaging(userId: Long, status: String, pageable: Pageable): Page<StoryEntity>
}