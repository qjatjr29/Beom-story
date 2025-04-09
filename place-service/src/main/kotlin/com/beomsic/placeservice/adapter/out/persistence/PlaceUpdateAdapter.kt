package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.application.port.`in`.command.PlaceUpdateCommand
import com.beomsic.placeservice.application.port.out.PlaceUpdatePort
import com.beomsic.placeservice.domain.Place
import org.springframework.stereotype.Component

@Component
class PlaceUpdateAdapter(
    private val placeRepository: PlaceRepository
): PlaceUpdatePort {
    override suspend fun updateContent(placeId: Long, command: PlaceUpdateCommand): Place {
        return updatePlace(placeId) { placeEntity ->
            placeRepository.save(
                placeEntity.copy(
                    name = command.name,
                    description = command.description,
                    category = command.category.name,
                    latitude = placeEntity.latitude,
                    longitude = placeEntity.longitude,
                    address = placeEntity.address,
                )).toDomain()
        }
    }

    override suspend fun updateImage(placeId: Long, newImageUrl: String): Place {
        return updatePlace(placeId) { placeEntity ->
            placeRepository.save(placeEntity.copy(imageUrl = newImageUrl)).toDomain()
        }
    }

    private suspend fun updatePlace(id: Long, updateAction: suspend (PlaceEntity) -> Place): Place {
        val placeEntity = placeRepository.findByIdOrNull(id)
        return updateAction(placeEntity)
    }

}