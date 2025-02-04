package com.beomsic.placeservice.application.port.out

import com.beomsic.placeservice.adapter.out.persistence.PlaceEntity
import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand

interface PlaceCreatePort {
    suspend fun create(command: PlaceCreateCommand): PlaceEntity
}