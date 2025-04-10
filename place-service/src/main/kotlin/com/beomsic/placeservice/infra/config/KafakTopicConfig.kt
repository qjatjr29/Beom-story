package com.beomsic.placeservice.infra.config

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.config.TopicConfig
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
@EnableConfigurationProperties(KafkaTopicProperties::class)
class KafkaTopicConfig(private val topicProperties: KafkaTopicProperties) {
    @Bean
    fun topics(): List<NewTopic> {
        return listOf(
            createTopic(topicProperties.rollbackImage)
        )
    }

    private fun createTopic(name: String, partitions: Int = 3, replicas: Int = 3): NewTopic {
        return TopicBuilder.name(name)
            .partitions(partitions)
            .replicas(replicas)
            .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2")
            .config(TopicConfig.UNCLEAN_LEADER_ELECTION_ENABLE_CONFIG, "false")
            .build()
    }
}