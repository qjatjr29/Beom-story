package com.beomsic.placeservice.application.port.`in`.command

data class PlaceCreateCommand (
    val storyId: Long,
    val name: String,
    val description: String?,
    var imageUrl: String? = null,
    val category: String?,
    val latitude: Double?,
    val longitude: Double?,
)