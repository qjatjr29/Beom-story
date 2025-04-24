package com.beomsic.imageservice.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kafka.topic")
data class KafkaTopicProperties(
    val uploadImage: String,
    val rollbackImage: String,
)