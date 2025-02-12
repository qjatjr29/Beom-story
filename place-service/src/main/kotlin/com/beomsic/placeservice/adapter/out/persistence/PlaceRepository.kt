package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.domain.exception.PlaceNotFoundException
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PlaceRepository : CoroutineCrudRepository<PlaceEntity, Long> {
    suspend fun findAllByStoryId(storyId: Long): List<PlaceEntity>
}

suspend fun PlaceRepository.findByIdOrNull(id: Long): PlaceEntity {
    return findById(id) ?: throw PlaceNotFoundException()
}