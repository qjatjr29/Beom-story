package com.beomsic.storyservice.adapter.`in`.web

import java.time.LocalDateTime

data class StoryDetailResponse(
    val id: Long,
    val authorId: Long,
    val title: String,
    val description: String?,
    val category: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
