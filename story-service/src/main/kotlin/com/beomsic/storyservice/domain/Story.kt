package com.beomsic.storyservice.domain

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