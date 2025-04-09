package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.storyservice.application.port.`in`.usecase.StoryFindUseCase
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/story-service")
class StoryFindController(
    private val storyFindUseCase: StoryFindUseCase
) {

    @GetMapping("/{storyId}")
    suspend fun getStoryDetails(@PathVariable("storyId") id: Long): StoryDetailResponse {
        return StoryDetailResponse(storyFindUseCase.getById(id))
    }

    @GetMapping("/me")
    suspend fun findMyStories(@AuthToken authUser: AuthUser,
                             @RequestParam("page", defaultValue = "0") page: Int,
                             @RequestParam("size", defaultValue = "10") size: Int): Page<StorySummaryResponse> {
        return storyFindUseCase.findAllByUserId(authUser.id, page, size).map { StorySummaryResponse(it) }
    }

    @GetMapping("/user")
    suspend fun findStoriesByUserId(@RequestParam("id") userId: Long,
                                   @RequestParam("page", defaultValue = "0") page: Int,
                                   @RequestParam("size", defaultValue = "10") size: Int): Page<StorySummaryResponse> {
        return storyFindUseCase.findAllByUserId(userId, page, size).map { StorySummaryResponse(it) }
    }

    @GetMapping("/status")
    suspend fun findStoriesByStatus(@AuthToken authUser: AuthUser, @RequestParam("status") status: String,
                                    @RequestParam("page", defaultValue = "0") page: Int,
                                    @RequestParam("size", defaultValue = "10") size: Int): Page<StorySummaryResponse> {
        return storyFindUseCase
            .findAllMyStoriesByStatus(authUser.id, status, page, size).map { StorySummaryResponse(it) }
    }

    // todo: 카테고리별 story 검색 / 키워드 기반 story 검색 / 주소 기반 story 검색


}