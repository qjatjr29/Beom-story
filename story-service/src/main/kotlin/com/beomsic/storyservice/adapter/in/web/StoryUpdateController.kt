package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.storyservice.application.port.`in`.command.StoryUpdateCommand
import com.beomsic.storyservice.application.port.`in`.usecase.StoryUpdateUseCase
import com.beomsic.storyservice.domain.model.Category
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/story-service")
class StoryUpdateController(
    private val storyUpdateUseCase: StoryUpdateUseCase
) {
    @PutMapping("/{storyId}")
    suspend fun update(@AuthToken authUser: AuthUser,
                       @PathVariable storyId: Long, @RequestBody request: StoryUpdateRequest) {
        val updateCommand = with(request) {
            StoryUpdateCommand(
                title = title,
                description = description,
                category = Category.valueOf(category.uppercase()),
                startDate = startDate,
                endDate = endDate
            )
        }
        storyUpdateUseCase.update(storyId, authUser.id, updateCommand)
    }

    @PatchMapping("/{storyId}")
    suspend fun updateStatus(@AuthToken authUser: AuthUser,
                             @PathVariable storyId: Long, @RequestParam("status") status: String) {
        storyUpdateUseCase.updateStatus(storyId = storyId, userId = authUser.id, status = status)
    }

}