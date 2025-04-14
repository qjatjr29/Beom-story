package com.beomsic.storyservice.adapter.out.kafka

import com.beomsic.storyservice.adapter.out.persistence.outbox.StoryOutbox
import com.beomsic.storyservice.adapter.out.persistence.outbox.StoryOutboxRepository
import com.beomsic.storyservice.infrastructure.config.KafkaTopicProperties
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OutboxPublisher(
    private val outboxRepository: StoryOutboxRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val kafkaTopicProperties: KafkaTopicProperties,
    @Value("\${outbox.batch-size:50}") private val batchSize: Int = 50,
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    suspend fun publishPendingMessages() {

        val pendingMessages = outboxRepository.findAllByOrderByCreatedAtAsc(batchSize)

        if (pendingMessages.isEmpty()) {
            return
        }

        val successfulIds = runBlocking {
            publishPendingMessagesAsync(pendingMessages)
        }

        if (successfulIds.isNotEmpty()) {
            outboxRepository.deleteAllById(successfulIds)
        }
    }

    private suspend fun publishPendingMessagesAsync(pendingMessages: List<StoryOutbox>): List<Long> = supervisorScope {
        pendingMessages.map { message ->
            async {
                try {
                    val topic = kafkaTopicProperties.storyOutbox;
                    val result = withContext(Dispatchers.IO) {
                        kafkaTemplate.send(topic, message.id.toString(), message.payload).await()
                    }
                    logger.info("Story Outbox Message delivered to Kafka with offset: ${result.recordMetadata.offset()}")
                    message.id  // ✅ 성공한 경우 ID 반환
                } catch (e: Exception) {
                    logger.error("Story Outbox Kafka message delivery failed: ${e.message}", e)
                    null  // ✅ 실패 시 null 반환
                }
            }
        }.awaitAll().filterNotNull()
    }
}