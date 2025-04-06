package com.beomsic.placeservice.domain

import java.time.LocalDateTime

data class Place (
    val id: Long? = null,
    val storyId: Long,
    val authorId: Long,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val category: Category? = Category.OTHER,
    val latitude: Double?,
    val longitude: Double?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)