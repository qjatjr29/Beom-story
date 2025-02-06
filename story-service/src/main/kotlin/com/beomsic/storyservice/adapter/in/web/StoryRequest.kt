package com.beomsic.storyservice.adapter.`in`.web

import java.time.LocalDateTime

data class StoryCreateRequest(
    val title: String,
    val description: String?,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
)