package com.beomsic.storyservice.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface StoryRepository : JpaRepository<StoryEntity, Long> {
}