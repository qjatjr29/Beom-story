package com.beomsic.placeservice.application.kafka

import com.beomsic.common.infra.kafka.story.StoryOutboxPayload
import com.beomsic.common.infra.kafka.story.StoryOutboxType
import com.beomsic.placeservice.application.port.out.PlaceDeletePort
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class KafkaMessageListener(
    private val placeDeletePort: PlaceDeletePort
){
    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = ["\${kafka.topic.story-outbox}"])
    @Transactional
    suspend fun handleStoryOutboxEvent(payload: String) {
        try {
            val storyOutboxPayload = Json.decodeFromString<StoryOutboxPayload>(payload)
            when (storyOutboxPayload.type) {
                StoryOutboxType.STORY_DELETED -> handleStoryDeletedEvent(storyOutboxPayload.storyId)
            }
        } catch (e: Exception) {
            logger.error("❗ Kafka 메시지 처리 중 오류 발생: ${e.message}")
        }
    }

    // todo: 장소에 사용된 이미지가 있다면 이미지 삭제도 처리.
    private suspend fun handleStoryDeletedEvent(storyId: Long) {
        placeDeletePort.deleteAllByStoryId(storyId)
    }
}