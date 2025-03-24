package com.beomsic.storyservice.infrastructure

import com.beomsic.storyservice.domain.outbox.StoryOutboxType
import com.beomsic.storyservice.infrastructure.config.KafkaTopicProperties
import org.springframework.stereotype.Component

@Component
class OutboxTopicResolver(private val topicProperties: KafkaTopicProperties) {
    fun getTopicForOutboxType(type: StoryOutboxType): String = when(type) {
        StoryOutboxType.STORY_DELETED -> topicProperties.storyDeleted
    }
}