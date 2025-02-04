package com.beomsic.placeservice.application.service

import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.placeservice.application.port.`in`.usecase.PlaceCreateUseCase
import com.beomsic.placeservice.application.port.out.PlaceCreatePort
import com.beomsic.placeservice.domain.Place
import org.springframework.stereotype.Service

@Service
class PlaceCreateService(
    private val placeCreatePort: PlaceCreatePort
) : PlaceCreateUseCase {
    override suspend fun execute(command: PlaceCreateCommand): Place {
        val placeEntity = placeCreatePort.create(command)
        return placeEntity.toDomain()
    }
}