package com.beomsic.placeservice.adapter.out.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PlaceRepository : CoroutineCrudRepository<PlaceEntity, Long> {
}