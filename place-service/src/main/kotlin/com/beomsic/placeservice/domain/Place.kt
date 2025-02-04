package com.beomsic.placeservice.domain

data class Place (
    val id: Long? = null,
    val storyId: Long,
    val name: String,
    val description: String?,
    val category: Category? = Category.기타,
    val latitude: Double?,
    val longitude: Double?,
)