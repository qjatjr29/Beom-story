package com.beomsic.storyservice.infrastructure.persistence

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StoryOutboxRepository: JpaRepository<StoryOutbox, Long> {

    @Modifying
    @Query("DELETE FROM StoryOutbox s WHERE s.id IN :ids")
    fun deleteAllByIds(@Param("ids") ids: List<Long>)

    fun findAllByOrderByCreatedAtAsc(pageable: Pageable): List<StoryOutbox>
}