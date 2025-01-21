package com.beomsic.storyservice.adapter.`in`.web

import java.time.LocalDateTime

data class StoryCreateRequest(
    val title: String,
    val description: String?,
    val placeRequests: List<PlaceRequest>?,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
)

data class PlaceRequest(
    val name: String,
    val description: String?,
    val category: String?,
    val latitude: Double?,
    val longitude: Double?,
)