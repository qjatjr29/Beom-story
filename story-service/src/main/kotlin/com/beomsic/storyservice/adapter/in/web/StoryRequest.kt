package com.beomsic.storyservice.adapter.`in`.web

import java.time.LocalDate

data class StoryCreateRequest(
    val title: String,
    val description: String?,
    val category: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)

data class StoryUpdateRequest(
    val title: String,
    val description: String?,
    val category: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)