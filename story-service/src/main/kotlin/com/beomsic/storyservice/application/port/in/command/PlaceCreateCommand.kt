package com.beomsic.storyservice.application.port.`in`.command

data class PlaceCreateCommand (
    val name: String,
    val description: String?,
    val category: String?,
    val latitude: Double?,
    val longitude: Double?
)