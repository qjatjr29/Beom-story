package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.placeservice.application.port.out.PlaceCreatePort
import com.beomsic.placeservice.domain.Place
import org.springframework.stereotype.Component

@Component
class PlaceCreateAdapter(
    private val placeRepository: PlaceRepository
): PlaceCreatePort {

    override suspend fun create(command: PlaceCreateCommand): Place {
        val entity = PlaceEntity(
            storyId = command.storyId,
            authorId = command.authorId,
            name = command.name,
            description = command.description,
            imageUrl = command.imageUrl,
            category = command.category.name,
            latitude = command.latitude,
            longitude = command.longitude,
            address = command.address,
        )

        val placeEntity = placeRepository.save(entity)
        return placeEntity.toDomain()
    }
}