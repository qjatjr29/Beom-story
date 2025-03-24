package com.beomsic.storyservice.application.port.`in`.command

import java.time.LocalDateTime

data class StoryCreateCommand(
    val authorId: Long,
    val title: String,
    val description: String?,
    val category: String,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
)
