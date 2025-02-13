package com.beomsic.placeservice.application.port.out

import com.beomsic.placeservice.adapter.out.persistence.PlaceEntity
import com.beomsic.placeservice.application.port.`in`.command.PlaceUpdateCommand

interface PlaceUpdatePort {
    suspend fun updateContent(placeId: Long, command: PlaceUpdateCommand): PlaceEntity
    suspend fun updateImage(placeId: Long, newImageUrl: String): PlaceEntity
}