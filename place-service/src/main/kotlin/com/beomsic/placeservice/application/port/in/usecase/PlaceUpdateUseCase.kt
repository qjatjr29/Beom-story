package com.beomsic.placeservice.application.port.`in`.usecase

import com.beomsic.placeservice.application.port.`in`.command.PlaceUpdateCommand
import com.beomsic.placeservice.domain.Place
import org.springframework.http.codec.multipart.FilePart

interface PlaceUpdateUseCase {
    suspend fun updateContent(command: PlaceUpdateCommand): Place
    suspend fun updateImage(placeId: Long, authorId: Long, image: FilePart)
}