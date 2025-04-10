package com.beomsic.storyservice.application.port.`in`.usecase

import com.beomsic.storyservice.domain.model.Story
import org.springframework.data.domain.Page

interface StoryFindUseCase {
    suspend fun getById(id: Long): Story
    suspend fun findAll(page: Int, size: Int): Page<Story>
    suspend fun findAllByUserId(userId: Long, page: Int, size: Int): Page<Story>
    suspend fun findAllMyStoriesByStatus(userId: Long, status: String, page: Int, size: Int): Page<Story>
}