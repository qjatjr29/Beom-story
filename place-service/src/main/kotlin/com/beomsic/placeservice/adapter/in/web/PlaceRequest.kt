package com.beomsic.placeservice.adapter.`in`.web

data class PlaceCreateRequest(
    val storyId: Long,
    val name: String,
    val description: String?,
    val category: String?,
    val latitude: Double?,
    val longitude: Double?,
)

data class PlaceUpdateContentRequest(
    val name: String?,
    val description: String?,
    val category: String?,
    val latitude: Double?,
    val longitude: Double?,
)