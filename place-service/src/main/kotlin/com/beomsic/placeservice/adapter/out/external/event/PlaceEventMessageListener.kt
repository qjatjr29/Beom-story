package com.beomsic.placeservice.adapter.out.external.event

import com.beomsic.common.annotation.EventHandler
import com.beomsic.common.event.ImageDeleteEvent
import com.beomsic.placeservice.adapter.out.external.kafka.ImageDeleteEventSendService
import org.springframework.context.event.EventListener
import org.springframework.transaction.event.TransactionalEventListener

@EventHandler
class PlaceEventMessageListener(
    private val imageDeleteEventSendService: ImageDeleteEventSendService,
) {
    @EventListener
//    @TransactionalEventListener(phase = )
    suspend fun senMessageHandler(deleteEvent: ImageDeleteEvent) =
        imageDeleteEventSendService.send(deleteEvent)

}