package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.application.port.out.StoryOutboxPort
import com.beomsic.storyservice.domain.outbox.StoryDeletedOutboxPayload
import com.beomsic.storyservice.domain.outbox.StoryOutboxType
import com.beomsic.storyservice.infrastructure.persistence.StoryOutbox
import com.beomsic.storyservice.infrastructure.persistence.StoryOutboxRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

@Component
class StoryOutboxAdapter(
    private val storyOutboxRepository: StoryOutboxRepository,
): StoryOutboxPort {

    override fun saveOutboxMessage(storyId: Long) {
        val payload = StoryDeletedOutboxPayload(storyId)
        val serializedPayload = Json.encodeToString(payload)
        val outboxType = StoryOutboxType.STORY_DELETED

        val outbox = StoryOutbox(
            storyId = storyId,
//            payload = serializedPayload,
            payload = payload,
            outboxType = outboxType,
        )

        storyOutboxRepository.save(outbox)
    }
}