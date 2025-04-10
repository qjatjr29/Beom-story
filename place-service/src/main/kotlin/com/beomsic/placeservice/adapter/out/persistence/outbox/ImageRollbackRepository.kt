package com.beomsic.placeservice.adapter.out.persistence.outbox

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface ImageRollbackRepository: CoroutineCrudRepository<ImageRollbackOutbox, Long> {

    @Query("SELECT * FROM image_rollback_outbox ORDER BY created_at ASC LIMIT :limit")
    suspend fun findAllByOrderByCreatedAtAsc(@Param("limit") limit: Int): List<ImageRollbackOutbox>
}