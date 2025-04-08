package com.beomsic.placeservice.domain

import java.util.*

enum class Category (val value: String) {
    RESTAURANT("음식점"),
    CAFE("카페"),
    ACCOMMODATION("숙박"),
    ATTRACTION("관광명소"),
    SHOPPING("쇼핑"),
    CULTURE("문화/공연"),
    TRANSPORT("교통"),
    OTHER("기타");

    companion object {
        fun fromValue(value: String): Category {
            return entries.find { it.name.lowercase(Locale.ENGLISH) == value.lowercase() } ?: OTHER
        }
    }
}