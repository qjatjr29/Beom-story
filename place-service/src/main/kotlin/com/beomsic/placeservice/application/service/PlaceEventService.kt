package com.beomsic.placeservice.application.service

//import com.beomsic.placeservice.adapter.out.external.event.PlaceCreatedEvent
//import com.beomsic.placeservice.adapter.out.external.event.PlaceCreationFailedEvent
import com.beomsic.common.event.ImageDeleteEvent
//import com.beomsic.placeservice.adapter.out.external.event.ImageDeleteEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalEventPublisher
//import org.springframework.transaction.reactive.TransactionalEventPublisher.publishEvent

@Service
class PlaceEventService(
    private val eventPublisher: ApplicationEventPublisher,
//    private val test: TransactionalEventPublisher
) {
//    fun publishEvent(event: PlaceCreatedEvent) {
//        println("PlaceEventService - publishEvent")
////        test.publishEvent(event)
//        eventPublisher.publishEvent(event)
//    }

    fun publishDeleteImageEvent(event: ImageDeleteEvent) {
        eventPublisher.publishEvent(event)
    }

}
