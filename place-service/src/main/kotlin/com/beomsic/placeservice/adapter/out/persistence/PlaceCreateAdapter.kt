package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.placeservice.application.port.out.PlaceCreatePort
import com.beomsic.placeservice.domain.Category
import org.springframework.stereotype.Component

@Component
class PlaceCreateAdapter(
    private val placeRepository: PlaceRepository
): PlaceCreatePort {

    override suspend fun create(command: PlaceCreateCommand): PlaceEntity {
        val entity = PlaceEntity(
            storyId = command.storyId,
            authorId = command.authorId,
            name = command.name,
            description = command.description,
            imageUrl = command.imageUrl,
            category = command.category?.let { Category.valueOf(it) },
            latitude = command.latitude,
            longitude = command.longitude
        )

        return placeRepository.save(entity)
    }
}