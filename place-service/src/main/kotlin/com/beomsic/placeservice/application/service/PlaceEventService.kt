package com.beomsic.placeservice.application.service

import com.beomsic.common.event.ImageDeleteEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class PlaceEventService(
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun publishDeleteImageEvent(event: ImageDeleteEvent) {
        eventPublisher.publishEvent(event)
    }
}
