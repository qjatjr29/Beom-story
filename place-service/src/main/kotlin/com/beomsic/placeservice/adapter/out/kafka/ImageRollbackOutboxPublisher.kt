package com.beomsic.placeservice.adapter.out.kafka

import com.beomsic.placeservice.adapter.out.persistence.outbox.ImageRollbackOutbox
import com.beomsic.placeservice.adapter.out.persistence.outbox.ImageRollbackRepository
import com.beomsic.placeservice.infra.config.KafkaTopicProperties
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ImageRollbackOutboxPublisher(
    private val outboxRepository: ImageRollbackRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val kafkaTopicProperties: KafkaTopicProperties,
    @Value("\${outbox.batch-size:50}") private val batchSize: Int = 50,
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(fixedDelay = 15000)
//    @Scheduled(cron = "0 */10 * * * *") // 10분 마다 실행
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

    private suspend fun publishPendingMessagesAsync(pendingMessages: List<ImageRollbackOutbox>): List<Long> = supervisorScope {
        pendingMessages.map { message ->
            async {
                try {
                    val topic = kafkaTopicProperties.rollbackImage;
                    val result = withContext(Dispatchers.IO) {
                        kafkaTemplate.send(topic, message.id.toString(), message.imageUrl).await()
                    }
                    logger.info("ImageRollback Message delivered to Kafka with offset: ${result.recordMetadata.offset()}")
                    message.id
                } catch (e: Exception) {
                    logger.error("ImageRollback Kafka message delivery failed: ${e.message}", e)
                    null  // ✅ 실패 시 null 반환
                }
            }
        }.awaitAll().filterNotNull()
    }
}