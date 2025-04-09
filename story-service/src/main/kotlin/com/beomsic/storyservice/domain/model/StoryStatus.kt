package com.beomsic.storyservice.domain.model

import java.util.*

enum class StoryStatus {
    DRAFT,
    ARCHIVED,
    DELETED
    ;

    companion object {
        fun fromValue(value: String): StoryStatus {
            return entries.find { it.name.lowercase(Locale.ENGLISH) == value.lowercase() } ?: throw IllegalArgumentException()
        }
    }
}