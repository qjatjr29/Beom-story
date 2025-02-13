package com.beomsic.common.event

import java.util.*

sealed interface ImageEvent {
    val eventId: String
}

data class ImageRollbackEvent(
    val imageUrl: String,
    override val eventId: String = UUID.randomUUID().toString()
) : ImageEvent

