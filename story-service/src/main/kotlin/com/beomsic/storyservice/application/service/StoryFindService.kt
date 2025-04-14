package com.beomsic.storyservice.application.service

import com.beomsic.storyservice.application.port.`in`.usecase.StoryFindUseCase
import com.beomsic.storyservice.application.port.out.StoryFindPort
import com.beomsic.storyservice.domain.model.Story
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class StoryFindService(private val storyFindPort: StoryFindPort): StoryFindUseCase {

    override suspend fun getById(id: Long): Story {
        return storyFindPort.getById(id)
    }

    override suspend fun findAll(page: Int, size: Int): Page<Story> {
        val pageable = PageRequest.of(page, size)
        return storyFindPort.findAll(pageable)
    }

    override suspend fun findAllByKeyword(keyword: String, page: Int, size: Int): Page<Story> {
        val pageable = PageRequest.of(page, size)
        return storyFindPort.findAllByKeyword(keyword, pageable)
    }

    override suspend fun findAllByUserId(userId: Long, page: Int, size: Int): Page<Story> {
        val pageable = PageRequest.of(page, size)
        return storyFindPort.findAllByUserId(userId, pageable)
    }

    override suspend fun findAllMyStoriesByStatus(userId: Long, status: String, page: Int, size: Int): Page<Story> {
        val pageable = PageRequest.of(page, size)
        return storyFindPort.findAllByUserIdAndStatus(userId, status, pageable)
    }
}