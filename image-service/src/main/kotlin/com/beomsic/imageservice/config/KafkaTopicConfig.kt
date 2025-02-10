package com.beomsic.imageservice.config

import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaTopicConfig(
    @Value("\${kafka.topic.upload-image}") val uploadImageTopic: String,
    val kafkaAdmin: KafkaAdmin
) {

    @PostConstruct
    fun initTopic() {
        kafkaAdmin.createOrModifyTopics(uploadImageTopic())
    }

    private fun uploadImageTopic(): NewTopic =
        TopicBuilder.name(uploadImageTopic)
            .partitions(1)
            .replicas(1)
            .build()
}