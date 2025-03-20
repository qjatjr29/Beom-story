package com.beomsic.storyservice.domain.model

import com.beomsic.storyservice.adapter.`in`.web.StoryDetailResponse
import java.time.LocalDateTime

data class Story(
    val id: Long,
    val authorId: Long,
    val title: String,
    val description: String?,
    val category: Category,
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
    category = category.value,
    startDate = startDate,
    endDate = endDate,
    createdAt = createdAt,
    updatedAt = updatedAt
)