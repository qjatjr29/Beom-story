package com.beomsic.placeservice.application.port.`in`.usecase

import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.placeservice.domain.Place
import org.springframework.http.codec.multipart.FilePart

interface PlaceCreateUseCase {
    suspend fun execute(command: PlaceCreateCommand, image: FilePart?): Place
}