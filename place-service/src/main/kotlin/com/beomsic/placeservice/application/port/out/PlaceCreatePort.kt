package com.beomsic.placeservice.application.port.out

import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.placeservice.domain.Place

interface PlaceCreatePort {
    suspend fun create(command: PlaceCreateCommand): Place
}