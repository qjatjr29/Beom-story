package com.beomsic.placeservice.config

import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaTopicConfig(
    @Value("\${kafka.topic.delete-image}") val deleteImageTopic: String,
    val kafkaAdmin: KafkaAdmin
) {

    @PostConstruct
    fun initTopic() {
        kafkaAdmin.createOrModifyTopics(deleteImageTopic())
    }

    private fun deleteImageTopic(): NewTopic =
        TopicBuilder.name(deleteImageTopic)
            .partitions(1)
            .replicas(1)
            .build()
}