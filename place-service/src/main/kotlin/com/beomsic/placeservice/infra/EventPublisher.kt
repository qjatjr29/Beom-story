package com.beomsic.placeservice.infra

import com.beomsic.common.infra.kafka.event.ImageRollbackEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class EventPublisher(
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun publishImageRollbackEvent(event: ImageRollbackEvent) {
        eventPublisher.publishEvent(event)
    }
}