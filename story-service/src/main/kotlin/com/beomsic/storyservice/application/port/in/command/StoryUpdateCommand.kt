package com.beomsic.storyservice.application.port.`in`.command

import com.beomsic.storyservice.domain.model.Category
import java.time.LocalDateTime

data class StoryUpdateCommand(
    val title: String,
    val description: String?,
    val category: Category,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
)
