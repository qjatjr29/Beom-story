package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.storyservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.application.port.`in`.usecase.StoryCreateUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/story-service")
class StoryCreateController(
    private val storyCreateUseCase: StoryCreateUseCase
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createStory(
        @AuthToken authUser: AuthUser,
        @RequestPart(value = "story") request: StoryCreateRequest,
        @RequestPart(value = "file", required = false) images: List<MultipartFile>) {
        val storyCreateCommand = StoryCreateCommand(
            authorId = authUser.id,
            title = request.title,
            description = request.description,
            startDate = request.startDate,
            endDate = request.endDate
        )

        val placeCreateCommands = request.placeRequests?.map { req ->
            PlaceCreateCommand(
                name = req.name,
                description = req.description,
                category = req.category,
                latitude = req.latitude,
                longitude = req.longitude
            )
        }

        storyCreateUseCase.execute(storyCreateCommand, placeCreateCommands)
    }

}