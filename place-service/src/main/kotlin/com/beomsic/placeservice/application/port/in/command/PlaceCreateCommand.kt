package com.beomsic.placeservice.application.port.`in`.command

import com.beomsic.placeservice.domain.Category

data class PlaceCreateCommand (
    val storyId: Long,
    val authorId: Long,
    val name: String,
    val description: String?,
    var imageUrl: String? = null,
    val category: Category,
    val latitude: Double?,
    val longitude: Double?,
)