package com.beomsic.storyservice.domain.outbox

import jakarta.persistence.Embeddable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Embeddable
sealed interface StoryOutboxPayload {
    val storyId: Long
}

@Serializable
data class StoryDeletedOutboxPayload(
    @SerialName("storyId") override val storyId: Long
): StoryOutboxPayload
