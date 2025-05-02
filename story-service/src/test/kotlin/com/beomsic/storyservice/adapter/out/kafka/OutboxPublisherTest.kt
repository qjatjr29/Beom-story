
import com.beomsic.storyservice.adapter.out.kafka.OutboxPublisher
import com.beomsic.storyservice.adapter.out.persistence.outbox.StoryOutbox
import com.beomsic.storyservice.adapter.out.persistence.outbox.StoryOutboxRepository
import com.beomsic.storyservice.infrastructure.config.KafkaTopicProperties
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.apache.kafka.clients.producer.RecordMetadata
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.CompletableFuture

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class OutboxPublisherTest: BehaviorSpec ({

    val outboxRepository = mockk<StoryOutboxRepository>()
    val kafkaTemplate = mockk<KafkaTemplate<String, Any>>()
    val kafkaTopicProperties = KafkaTopicProperties(storyOutbox = "story-outbox-topic")
    val batchSize = 50
    val outboxPublisher = OutboxPublisher(outboxRepository, kafkaTemplate, kafkaTopicProperties, batchSize)

    afterEach {
        clearAllMocks()
    }

    given("스토리 아웃박스 메시지가 존재할 때") {
        val messages = listOf(
            StoryOutbox(1, 1, "payload1"),
            StoryOutbox(2, 2, "payload2")
        )

        coEvery { outboxRepository.findAllByOrderByCreatedAtAsc(batchSize) } returns messages

        messages.forEach { message ->
            val sendResult = mockk<SendResult<String, Any>>()
            val metadata = mockk<RecordMetadata>()
            every { metadata.offset() } returns 123
            every { sendResult.recordMetadata } returns metadata
            val future = CompletableFuture<SendResult<String, Any>>().apply {
                complete(sendResult)
            }
            every {
                kafkaTemplate.send(kafkaTopicProperties.storyOutbox, message.id.toString(), message.payload)
            } returns future
        }

        coEvery { outboxRepository.deleteAllById(listOf(1, 2)) } just Runs

        `when`("publishPendingMessages를 성공적으로 호출하면") {
            runTest { outboxPublisher.publishPendingMessages() }

            then("스토리 아웃박스 메시지가 Kafka로 전송되고 DB에서 삭제된다") {
                coVerify(exactly = 1) { outboxRepository.findAllByOrderByCreatedAtAsc(batchSize) }
                verify(exactly = 2) { kafkaTemplate.send(any(), any(), any()) }
                coVerify(exactly = 1) { outboxRepository.deleteAllById(listOf(1, 2)) }
            }
        }
    }

    given("스토리 아웃박스 메시지가 비어있을 때") {
        coEvery { outboxRepository.findAllByOrderByCreatedAtAsc(batchSize) } returns emptyList()

        `when`("publishPendingMessages를 호출하면") {
            runTest { outboxPublisher.publishPendingMessages() }

            then("아무 작업도 수행되지 않는다") {
                coVerify(exactly = 1) { outboxRepository.findAllByOrderByCreatedAtAsc(batchSize) }
                verify(exactly = 0) { kafkaTemplate.send(any(), any(), any()) }
                coVerify(exactly = 0) { outboxRepository.deleteAllById(any()) }
            }
        }
    }

    given("Kafka 전송이 실패한 메시지가 존재할 때") {
        val messages = listOf(StoryOutbox(1, 1, "payload1"))

        coEvery { outboxRepository.findAllByOrderByCreatedAtAsc(batchSize) } returns messages

        coEvery {
            kafkaTemplate.send(kafkaTopicProperties.storyOutbox, "1", "payload1")
        } throws RuntimeException()

        coEvery { outboxRepository.deleteAllById(any()) } just Runs

        `when`("publishPendingMessages를 호출하면") {
            runTest { outboxPublisher.publishPendingMessages() }

            then("실패한 메시지는 삭제되지 않는다") {
                coVerify(exactly = 1) { outboxRepository.findAllByOrderByCreatedAtAsc(batchSize) }
                coVerify(exactly = 1) { kafkaTemplate.send(any(), any(), any()) }
                coVerify(exactly = 0) { outboxRepository.deleteAllById(any()) }
            }
        }
    }
})