package com.beomsic.storyservice.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Story(
    val id: Long,
    val authorId: Long,
    val title: String,
    val description: String?,
    val category: Category,
    val status: StoryStatus,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)