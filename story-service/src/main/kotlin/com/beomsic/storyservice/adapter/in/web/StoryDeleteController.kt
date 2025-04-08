package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.storyservice.application.port.`in`.usecase.StoryDeleteUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/story-service")
class StoryDeleteController(
    private val storyDeleteUseCase: StoryDeleteUseCase
) {

    @DeleteMapping("/{storyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteStory(@AuthToken authUser: AuthUser, @PathVariable("storyId") storyId: Long){
        storyDeleteUseCase.execute(authUser.id, storyId)
    }
}