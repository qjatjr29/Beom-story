package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.storyservice.application.port.`in`.usecase.StoryDeleteUseCase
import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import com.beomsic.storyservice.domain.exception.UnauthorizedStoryAccessException
import com.beomsic.storyservice.infrastructure.config.AuthTokenResolver
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(StoryCreateController::class)
@ActiveProfiles("test")
class StoryDeleteControllerTest: BehaviorSpec ({

    val storyDeleteUseCase = mockk<StoryDeleteUseCase>()
    val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()

    val client = WebTestClient
        .bindToController(StoryDeleteController(storyDeleteUseCase))
        .argumentResolvers { it.addCustomResolver(AuthTokenResolver()) }
        .controllerAdvice(globalExceptionHandler)
        .configureClient()
        .build()

    val userId = 1L
    val storyId = 2L
    val email = "test@example.com"

    afterEach {
        clearAllMocks()
    }

    given("스토리 ID를 통한 삭제 요청 시") {
        `when`("정상적인 요청이라면") {
            coEvery { storyDeleteUseCase.execute(userId, storyId) } just Runs

            then("204 NO_CONTENT를 반환") {
                client.delete()
                    .uri("/story-service/${storyId}")
                    .header("Authorization", "Bearer mockToken")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isNoContent
            }
        }

        `when`("작성자가 아니라면") {
            val otherUserId = 999L
            coEvery { storyDeleteUseCase.execute(otherUserId, storyId) } throws UnauthorizedStoryAccessException()

            then("403 FORBIDDEN 을 반환") {
                client.delete()
                    .uri("/story-service/${storyId}")
                    .header("Authorization", "Bearer mockToken")
                    .header("userId", otherUserId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isForbidden
            }
        }

        `when`("없는 스토리라면") {
            coEvery { storyDeleteUseCase.execute(userId, storyId) } throws StoryNotFoundException()

            then("404 NOT_FOUND 반환") {
                client.delete()
                    .uri("/story-service/${storyId}")
                    .header("Authorization", "Bearer mockToken")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isNotFound
            }
        }
    }

})