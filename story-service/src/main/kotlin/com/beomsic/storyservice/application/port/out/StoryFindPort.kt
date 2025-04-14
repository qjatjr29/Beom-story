package com.beomsic.storyservice.application.port.out

import com.beomsic.storyservice.domain.model.Story
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface StoryFindPort {
    suspend fun getById(id: Long): Story
    suspend fun findAll(pageable: Pageable): Page<Story>
    suspend fun findAllByKeyword(keyword: String, pageable: Pageable): Page<Story>
    suspend fun findAllByUserId(userId: Long, pageable: Pageable): Page<Story>
    suspend fun findAllByUserIdAndStatus(userId: Long, status: String, pageable: Pageable): Page<Story>
}