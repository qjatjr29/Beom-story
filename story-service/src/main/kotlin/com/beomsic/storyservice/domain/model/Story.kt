package com.beomsic.storyservice.domain.model

import java.time.LocalDateTime

data class Story(
    val id: Long,
    val authorId: Long,
    val title: String,
    val description: String?,
    val category: Category,
    val status: StoryStatus,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)