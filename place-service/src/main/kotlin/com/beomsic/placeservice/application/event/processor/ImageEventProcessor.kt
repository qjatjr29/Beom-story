package com.beomsic.placeservice.application.event.processor

import com.beomsic.common.infra.kafka.event.ImageRollbackEvent
import com.beomsic.placeservice.application.event.handler.ImageEventHandler
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ImageEventProcessor(
    private val imageEventHandler: ImageEventHandler,
) {
    @Async("asyncTaskExecutor")
    @EventListener
    suspend fun execute(imageEvent: ImageRollbackEvent) = imageEventHandler.handle(imageEvent)
}