package com.beomsic.storyservice.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "kafka.topic")
data class KafkaTopicProperties(
    var storyDeleted: String
)