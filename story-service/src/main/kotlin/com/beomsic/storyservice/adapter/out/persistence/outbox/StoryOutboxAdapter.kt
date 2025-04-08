package com.beomsic.storyservice.adapter.out.persistence.outbox

import com.beomsic.storyservice.application.port.out.StoryOutboxPort
import com.beomsic.storyservice.domain.outbox.StoryDeletedOutboxPayload
import com.beomsic.storyservice.domain.outbox.StoryOutboxType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

@Component
class StoryOutboxAdapter(
    private val storyOutboxRepository: StoryOutboxRepository,
): StoryOutboxPort {

    override suspend fun saveStoryDeleteMessage(storyId: Long) {

        val outboxType = StoryOutboxType.STORY_DELETED
        val payload = StoryDeletedOutboxPayload(outboxType, storyId)
        val serializedPayload = Json.encodeToString(payload)

        val outbox = StoryOutbox(
            storyId = storyId,
            payload = serializedPayload,
            outboxType = outboxType.name,
        )

        storyOutboxRepository.save(outbox)
    }
}