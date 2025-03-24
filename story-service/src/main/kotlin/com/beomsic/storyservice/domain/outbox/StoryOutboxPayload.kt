package com.beomsic.storyservice.domain.outbox

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface StoryOutboxPayload {
    val type: StoryOutboxType
    val storyId: Long
}

@Serializable
data class StoryDeletedOutboxPayload(
    @SerialName("type") override val type: StoryOutboxType,
    @SerialName("storyId") override val storyId: Long
): StoryOutboxPayload
