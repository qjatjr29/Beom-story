package com.beomsic.storyservice.adapter.out.persistence.outbox

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface StoryOutboxRepository: CoroutineCrudRepository<StoryOutbox, Long> {

    @Modifying
    @Query("DELETE FROM StoryOutbox s WHERE s.id IN :ids")
    fun deleteAllByIds(@Param("ids") ids: List<Long>)

    @Query("SELECT * FROM story_outbox ORDER BY created_at ASC LIMIT :limit")
    suspend fun findAllByOrderByCreatedAtAsc(@Param("limit") limit: Int): List<StoryOutbox>
}