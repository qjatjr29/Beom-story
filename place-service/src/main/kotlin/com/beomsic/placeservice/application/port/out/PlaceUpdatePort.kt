package com.beomsic.placeservice.application.port.out

import com.beomsic.placeservice.application.port.`in`.command.PlaceUpdateCommand
import com.beomsic.placeservice.domain.Place

interface PlaceUpdatePort {
    suspend fun updateContent(placeId: Long, command: PlaceUpdateCommand): Place
    suspend fun updateImage(placeId: Long, newImageUrl: String): Place
}