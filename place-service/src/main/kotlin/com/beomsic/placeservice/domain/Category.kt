package com.beomsic.placeservice.domain

enum class Category {
    음식점,
    카페,
    관광지,
    기타;

    companion object {
        fun fromString(value: String): Category =
            enumValues<Category>().find { it.name == value } ?: 기타
    }
}