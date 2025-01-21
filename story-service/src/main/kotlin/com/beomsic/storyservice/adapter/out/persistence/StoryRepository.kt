package com.beomsic.storyservice.adapter.out.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoryRepository : CoroutineCrudRepository<StoryEntity, Long> {
}