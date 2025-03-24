package com.beomsic.storyservice.infrastructure

import com.beomsic.storyservice.infrastructure.persistence.StoryOutbox
import com.beomsic.storyservice.infrastructure.persistence.StoryOutboxRepository
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class OutboxPublisher(
    private val outboxRepository: StoryOutboxRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val outboxTopicResolver: OutboxTopicResolver,
    @Value("\${outbox.batch-size:50}") private val batchSize: Int = 50,
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(fixedDelay = 15000)
//    @Scheduled(cron = "0 */10 * * * *") // 10분 마다 실행
    @Transactional
    fun publishPendingMessages() {

        val pageable = Pageable.ofSize(batchSize)
        val pendingMessages = outboxRepository.findAllByOrderByCreatedAtAsc(pageable)

        if (pendingMessages.isEmpty) {
            return
        }

        val successfulIds = runBlocking {
            publishPendingMessagesAsync(pendingMessages)
        }

        if (successfulIds.isNotEmpty()) {
            outboxRepository.deleteAllById(successfulIds)
        }
    }

    private suspend fun publishPendingMessagesAsync(pendingMessages: Page<StoryOutbox>): List<Long> {
        val successfulIds = Collections.synchronizedList(mutableListOf<Long>())
        supervisorScope {
            pendingMessages.forEach { message ->
                launch {
                    try {
                        val topic = outboxTopicResolver.getTopicForOutboxType(message.outboxType)
                        val result = withContext(Dispatchers.IO) {
                            kafkaTemplate.send(topic, message.storyId.toString(), message).get()
                        }
                        successfulIds.add(message.id!!)
                        logger.info("Story Outbox Message delivered to Kafka with offset: ${result.recordMetadata.offset()}")
                    } catch (e: Exception) {
                        logger.error("Story Outbox Kafka message delivery failed: ${e.message}", e)
                    }
                }
            }
        }

        return successfulIds
    }
}