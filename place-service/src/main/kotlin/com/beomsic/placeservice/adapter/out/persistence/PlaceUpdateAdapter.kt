package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.application.port.`in`.command.PlaceUpdateCommand
import com.beomsic.placeservice.application.port.out.PlaceUpdatePort
import com.beomsic.placeservice.domain.Category
import org.springframework.stereotype.Component

@Component
class PlaceUpdateAdapter(
    private val placeRepository: PlaceRepository
): PlaceUpdatePort {

    override suspend fun updateContent(placeId: Long, command: PlaceUpdateCommand): PlaceEntity {

        val existingPlace = placeRepository.findByIdOrNull(placeId)

        existingPlace.apply {
            command.name.let { this.name = it }
            command.description?.let { this.description = it }
            command.category.let { this.category = it }
            command.latitude.let { this.latitude = it }
            command.longitude.let { this.longitude = it }
        }

        return placeRepository.save(existingPlace)
    }

    override suspend fun updateImage(placeId: Long, newImageUrl: String): PlaceEntity {
        val existingPlace = placeRepository.findByIdOrNull(placeId)
        existingPlace.imageUrl = newImageUrl
        return placeRepository.save(existingPlace)
    }

}