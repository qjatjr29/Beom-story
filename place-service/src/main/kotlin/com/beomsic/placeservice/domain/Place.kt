package com.beomsic.placeservice.domain

data class Place (
    val id: Long? = null,
    val storyId: Long,
    val authorId: Long,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val category: Category? = Category.기타,
    val latitude: Double?,
    val longitude: Double?,
)