package com.beomsic.placeservice.application.port.`in`.command

import com.beomsic.placeservice.domain.Category

data class PlaceUpdateCommand (
    val placeId: Long,
    val authorId: Long,
    val name: String,
    val description: String?,
    val category: Category,
    val latitude: Double,
    val longitude: Double,
)