package com.beomsic.placeservice.infra.config

import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaTopicConfig(
    @Value("\${kafka.topic.rollback-image}") val TOPIC_ROLLBACK_IMAGE: String,
    val kafkaAdmin: KafkaAdmin
) {

    @PostConstruct
    fun initTopic() {
        kafkaAdmin.createOrModifyTopics(rollbackImageTopic())
    }

    private fun rollbackImageTopic(): NewTopic =
        TopicBuilder.name(TOPIC_ROLLBACK_IMAGE)
            .partitions(1)
            .replicas(1)
            .build()
}