package com.beomsic.storyservice.adapter.out.persistence.outbox

//import com.beomsic.common.event.StoryDeletedOutboxPayload
import com.beomsic.common.infra.kafka.story.StoryOutboxPayload
import com.beomsic.common.infra.kafka.story.StoryOutboxType
import com.beomsic.storyservice.application.port.out.StoryOutboxPort
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

@Component
class StoryOutboxAdapter(
    private val storyOutboxRepository: StoryOutboxRepository,
): StoryOutboxPort {

    override suspend fun saveStoryDeleteMessage(storyId: Long) {
        val outboxPayload = StoryOutboxPayload(type = StoryOutboxType.STORY_DELETED, storyId = storyId)
        val serializedPayload = Json.encodeToString(outboxPayload)
        val outbox = StoryOutbox(
            storyId = storyId,
            payload = serializedPayload,
        )
        storyOutboxRepository.save(outbox)
    }
}