package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.storyservice.application.port.`in`.usecase.StoryUpdateUseCase
import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import com.beomsic.storyservice.domain.exception.UnauthorizedStoryAccessException
import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.StoryStatus
import com.beomsic.storyservice.infrastructure.config.AuthTokenResolver
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate

@WebFluxTest(StoryCreateController::class)
@ActiveProfiles("test")
class StoryUpdateControllerIntegrationTest: BehaviorSpec({

    val storyUpdateUseCase = mockk<StoryUpdateUseCase>()
    val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()

    val client = WebTestClient
        .bindToController(StoryUpdateController(storyUpdateUseCase))
        .argumentResolvers { it.addCustomResolver(AuthTokenResolver()) }
        .controllerAdvice(globalExceptionHandler)
        .configureClient()
        .build()

    afterEach {
        clearAllMocks()
    }


    given("스토리 내용을 수정할 때") {

        val storyId = 1L
        val userId = 1L
        val email = "test@example.com"
        val otherUserId = 2L

        val request = StoryUpdateRequest(
            title = "제목 수정",
            description = "설명 수정",
            category = Category.TRAVEL.name,
            startDate = LocalDate.of(2025, 5, 1),
            endDate = LocalDate.of(2025, 5, 5)
        )

        `when`("정상적인 요청이라면") {
            coEvery { storyUpdateUseCase.update(storyId, userId, any()) } just Runs

            then("200 OK를 반환") {
                client.put()
                    .uri("/story-service/${storyId}")
                    .bodyValue(request)
                    .header("Authorization", "Bearer mockToken")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isOk
            }
        }

        `when`("작성자가 아닌 유저의 요청이라면") {

            coEvery { storyUpdateUseCase.update(storyId, otherUserId, any()) } throws UnauthorizedStoryAccessException()

            then("403 Forbidden을 반환") {
                client.put()
                    .uri("/story-service/${storyId}")
                    .header("Authorization", "Bearer mockToken")
                    .header("userId", otherUserId.toString())
                    .header("email", email)
                    .bodyValue(request)
                    .header("Authorization", "Bearer mockToken")
                    .exchange()
                    .expectStatus().isForbidden
            }
        }

        `when`("스토리를 찾을 수 없다면") {

            coEvery { storyUpdateUseCase.update(storyId, userId, any()) } throws StoryNotFoundException()

            then("404 Not Found를 반환") {
                client.put()
                    .uri("/story-service/${storyId}")
                    .header("Authorization", "Bearer mockToken")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .bodyValue(request)
                    .header("Authorization", "Bearer mockToken")
                    .exchange()
                    .expectStatus().isNotFound
            }
        }
    }

    given("스토리 상태을 수정할 때") {

        val storyId = 1L
        val userId = 1L
        val email = "test@example.com"
        val otherUserId = 2L
        val status = StoryStatus.ARCHIVED

        `when`("정상적인 요청이라면") {
            coEvery { storyUpdateUseCase.updateStatus(storyId, userId, status.name) } just Runs

            then("200 OK를 반환") {
                client.patch()
                    .uri("/story-service/${storyId}?status=$status")
                    .header("Authorization", "Bearer mockToken")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isOk
            }
        }

        `when`("작성자가 아닌 유저의 요청이라면") {

            coEvery {
                storyUpdateUseCase.updateStatus(storyId, otherUserId, status.name)
            } throws UnauthorizedStoryAccessException()

            then("403 Forbidden을 반환") {
                client.patch()
                    .uri("/story-service/${storyId}?status=$status")
                    .header("Authorization", "Bearer mockToken")
                    .header("userId", otherUserId.toString())
                    .header("email", email)
                    .header("Authorization", "Bearer mockToken")
                    .exchange()
                    .expectStatus().isForbidden
            }
        }

        `when`("스토리를 찾을 수 없다면") {

            coEvery { storyUpdateUseCase.updateStatus(storyId, userId, status.name) } throws StoryNotFoundException()

            then("404 Not Found를 반환") {
                client.patch()
                    .uri("/story-service/${storyId}?status=$status")
                    .header("Authorization", "Bearer mockToken")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .header("Authorization", "Bearer mockToken")
                    .exchange()
                    .expectStatus().isNotFound
            }
        }
    }
})