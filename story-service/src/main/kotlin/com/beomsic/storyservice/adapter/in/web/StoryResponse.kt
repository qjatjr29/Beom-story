package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.storyservice.domain.model.Story
import java.time.LocalDateTime

data class StoryDetailResponse(
    val id: Long,
    val authorId: Long,
    val title: String,
    val description: String?,
    val category: String,
    val status: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        operator fun invoke(story: Story) = with(story) {
            StoryDetailResponse(
                id = id,
                authorId = authorId,
                title = title,
                description = description,
                category = category.name,
                status = status.name,
                startDate = startDate,
                endDate = endDate,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

data class StorySummaryResponse(
    val id: Long,
    val authorId: Long,
    val title: String,
    val category: String,
    val status: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val createdAt: LocalDateTime,
) {
    companion object {
        operator fun invoke(story: Story) = with(story) {
            StorySummaryResponse(
                id = id,
                authorId = authorId,
                title = title,
                status = status.name,
                category = category.name,
                startDate = startDate,
                endDate = endDate,
                createdAt = createdAt,
            )
        }}
}
