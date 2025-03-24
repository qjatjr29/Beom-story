package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.storyservice.application.port.`in`.usecase.StoryDeleteUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/story-service")
class StoryDeleteController(
    private val storyDeleteUseCase: StoryDeleteUseCase
) {

    @DeleteMapping("/{storyId}")
    suspend fun deleteStory(@PathVariable("storyId") storyId: Long) : ResponseEntity<Void> {
        storyDeleteUseCase.execute(storyId)
        return ResponseEntity.noContent().build()
    }
}