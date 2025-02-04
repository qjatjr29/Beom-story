package com.beomsic.placeservice.application.port.`in`.usecase

import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.placeservice.domain.Place

interface PlaceCreateUseCase {
    suspend fun execute(command: PlaceCreateCommand): Place
}