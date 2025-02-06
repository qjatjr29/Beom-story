package com.beomsic.storyservice.domain

import com.beomsic.storyservice.adapter.`in`.web.StoryDetailResponse
import java.time.LocalDateTime

data class Story(
    val id: Long,
    val authorId: Long,
    val title: String,
    val description: String?,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
fun Story.toDetailResponse() = StoryDetailResponse(
    id = id,
    authorId = authorId,
    title = title,
    description = description,
    startDate = startDate,
    endDate = endDate,
    createdAt = createdAt,
    updatedAt = updatedAt
)