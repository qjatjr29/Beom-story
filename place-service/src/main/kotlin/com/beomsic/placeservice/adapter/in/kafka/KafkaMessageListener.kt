package com.beomsic.placeservice.adapter.`in`.kafka

import com.beomsic.common.infra.kafka.story.StoryOutboxPayload
import com.beomsic.common.infra.kafka.story.StoryOutboxType
import com.beomsic.placeservice.application.port.`in`.usecase.PlaceDeleteUseCase
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaMessageListener(
    private val placeDeleteUseCase: PlaceDeleteUseCase
){
    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = ["\${kafka.topic.story-outbox}"])
    fun handleStoryOutboxEvent(payload: String) {
        runBlocking {
            try {
                val storyOutboxPayload = Json.decodeFromString<StoryOutboxPayload>(payload)
                when (storyOutboxPayload.type) {
                    StoryOutboxType.STORY_DELETED -> handleStoryDeletedEvent(storyOutboxPayload.storyId)
                }
            } catch (e: Exception) {
                logger.error("❗ Kafka 메시지 처리 중 오류 발생: ${e.message}")
                throw e
            }
        }
    }

    private suspend fun handleStoryDeletedEvent(storyId: Long) {
        placeDeleteUseCase.deleteAllByStoryId(storyId)
    }
}