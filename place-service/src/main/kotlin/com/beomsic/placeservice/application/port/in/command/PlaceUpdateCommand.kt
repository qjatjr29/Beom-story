package com.beomsic.placeservice.application.port.`in`.command

data class PlaceUpdateCommand (
    val placeId: Long,
    val authorId: Long,
    val name: String?,
    val description: String?,
    val category: String?,
    val latitude: Double?,
    val longitude: Double?,
)