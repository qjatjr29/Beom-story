package com.beomsic.placeservice.infra

import com.beomsic.common.event.ImageEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class EventPublisher(
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun publishImageEvent(event: ImageEvent) {
        eventPublisher.publishEvent(event)
    }
}