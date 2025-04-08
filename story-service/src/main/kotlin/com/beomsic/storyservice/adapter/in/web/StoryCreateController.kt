package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.application.port.`in`.usecase.StoryCreateUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/story-service")
class StoryCreateController(
    private val storyCreateUseCase: StoryCreateUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createStory(@AuthToken authUser: AuthUser,
                            @RequestBody request: StoryCreateRequest): ResponseEntity<StoryDetailResponse> {
        val storyCreateCommand = with(request) {
            StoryCreateCommand(
                authorId = authUser.id,
                title = title,
                description = description,
                category = category,
                startDate = startDate,
                endDate = endDate
            )
        }
        val response = StoryDetailResponse(storyCreateUseCase.execute(storyCreateCommand))
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

}