package com.beomsic.storyservice.application.port.`in`.command

import com.beomsic.storyservice.domain.model.Category
import java.time.LocalDate

data class StoryCreateCommand(
    val authorId: Long,
    val title: String,
    val description: String?,
    val category: Category,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
