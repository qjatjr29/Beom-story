package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.placeservice.domain.Category
import com.beomsic.placeservice.domain.Place
import java.time.LocalDateTime

data class PlaceDetailResponse (
    val id: Long,
    val storyId: Long,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val category: Category?,
    val latitude: Double?,
    val longitude: Double?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        operator fun invoke(place: Place) = with(place) {
            PlaceDetailResponse(
                id = id!!,
                storyId = storyId,
                name = name,
                description = description,
                imageUrl = imageUrl,
                category = category,
                latitude = latitude,
                longitude = longitude,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

data class PlaceSummaryResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime
) {
    companion object {
        operator fun invoke(place: Place) = with(place) {
            PlaceSummaryResponse(
                id = id!!,
                name = name,
                description = description,
                createdAt = createdAt,
            )
        }
    }
}