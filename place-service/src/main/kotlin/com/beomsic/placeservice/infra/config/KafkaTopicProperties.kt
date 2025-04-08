package com.beomsic.placeservice.infra.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kafka.topic")
data class KafkaTopicProperties(
    val rollbackImage: String,
    val storyOutbox: String
)