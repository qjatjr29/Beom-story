package com.beomsic.storyservice.adapter.out.persistence

import java.util.*

enum class StoryStatus {
    DRAFT,          // 임시 저장
    ARCHIVED,       // 보관됨
    DELETED         // 삭제됨
    ;

    companion object {
        fun fromValue(value: String): StoryStatus {
            return entries.find { it.name.lowercase(Locale.ENGLISH) == value.lowercase() } ?: throw IllegalArgumentException()
        }
    }
}