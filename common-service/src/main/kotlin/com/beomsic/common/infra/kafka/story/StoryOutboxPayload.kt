package com.beomsic.common.infra.kafka.story

import kotlinx.serialization.Serializable

@Serializable
data class StoryOutboxPayload (
    val type: StoryOutboxType,
    val storyId: Long
)

@Serializable
enum class StoryOutboxType {
    STORY_DELETED
}