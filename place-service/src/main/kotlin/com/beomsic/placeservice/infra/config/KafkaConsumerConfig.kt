package com.beomsic.placeservice.infra.config

import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.util.backoff.FixedBackOff


@Configuration
class KafkaConsumerConfig(
    @Value("\${kafka.bootstrap-servers}") private val bootstrapServers: String,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val kafkaTopicProperties: KafkaTopicProperties,
) {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val configProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to "group-id", // 그룹 ID 설정
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            JsonDeserializer.TRUSTED_PACKAGES to "*"
        )
        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = consumerFactory()

        // 🔁 Retry 설정 (2초 간격으로 최대 3번 시도)
        val backOff = FixedBackOff(2000L, 3)

        // ❗ 재시도 다 실패했을 때 처리할 DLT 핸들러 설정
        val errorHandler = DefaultErrorHandler({ record, exception ->
            logger.error(exception) { "DLT 처리: ${record.value()}" }
            // todo: Slack Alert 또는 모니터링 필수
             kafkaTemplate.send(kafkaTopicProperties.dltTopic, record.value())
        }, backOff)

        factory.setCommonErrorHandler(errorHandler)

        return factory
    }
}