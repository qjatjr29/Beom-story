package com.beomsic.placeservice.adapter.`in`.web

import java.time.LocalDate

data class PlaceCreateRequest(
    val storyId: Long,
    val name: String,
    val description: String?,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val visitedDate: LocalDate,
)

data class PlaceUpdateContentRequest(
    val name: String,
    val description: String?,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val visitedDate: LocalDate,
)