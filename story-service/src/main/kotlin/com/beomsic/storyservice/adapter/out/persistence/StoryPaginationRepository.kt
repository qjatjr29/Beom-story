package com.beomsic.storyservice.adapter.out.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface StoryPaginationRepository {
    suspend fun findAllByAuthorIdWithPaging(authorId: Long, pageable: Pageable): Page<StoryEntity>
}