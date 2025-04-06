package com.beomsic.placeservice.domain

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
            return entries.find { it.value == value } ?: OTHER
        }
    }
}