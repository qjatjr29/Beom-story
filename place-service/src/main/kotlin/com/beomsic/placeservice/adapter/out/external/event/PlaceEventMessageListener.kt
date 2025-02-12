package com.beomsic.placeservice.adapter.out.external.event

import com.beomsic.common.annotation.EventHandler
import com.beomsic.common.event.ImageDeleteEvent
import com.beomsic.placeservice.adapter.out.external.kafka.ImageDeleteEventSendService
import org.springframework.context.event.EventListener

@EventHandler
class PlaceEventMessageListener(
    private val imageDeleteEventSendService: ImageDeleteEventSendService,
) {
    @EventListener
    suspend fun senMessageHandler(deleteEvent: ImageDeleteEvent) =
        imageDeleteEventSendService.send(deleteEvent)
}