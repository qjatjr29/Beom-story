package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.storyservice.application.port.`in`.usecase.StoryCreateUseCase
import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.Story
import com.beomsic.storyservice.domain.model.StoryStatus
import com.beomsic.storyservice.infrastructure.config.AuthTokenResolver
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coEvery
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import java.time.LocalDateTime

@WebFluxTest(StoryCreateController::class)
@ActiveProfiles("test")
class StoryCreateControllerTest : BehaviorSpec({

    val storyCreateUseCase = mockk<StoryCreateUseCase>()

    val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()

    val client = WebTestClient
        .bindToController(StoryCreateController(storyCreateUseCase))
        .argumentResolvers { it.addCustomResolver(AuthTokenResolver()) }
        .controllerAdvice(globalExceptionHandler)
        .configureClient()
        .build()

    given("유저 인증 정보와 StoryCreateRequest가 주어졌을 때") {
        val userId = 1L
        val email = "test@test.com"
        val accessToken = "accessToken"
        val title = "새로운 일상"
        val description = "일상 설명"
        val request = StoryCreateRequest(
            title = title,
            description = description,
            category = "DAILY",
            startDate = LocalDate.of(2025, 4, 1),
            endDate = LocalDate.of(2025, 4, 2)
        )

        val createdStory = Story(
            id = 1L,
            authorId = userId,
            title = request.title,
            description = request.description,
            category = Category.DAILY,
            startDate = request.startDate,
            endDate = request.endDate,
            status = StoryStatus.DRAFT,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        `when`("스토리를 정상적으로 생성하면") {
            coEvery { storyCreateUseCase.execute(any()) } returns createdStory

            then("201 코드와 함께 StoryDetailResponse를 반환한다") {
                client.post()
                    .uri("/story-service")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated
                    .expectBody(StoryDetailResponse::class.java)
                    .value { response ->
                        assertThat(response.id).isEqualTo(userId)
                        assertThat(response.title).isEqualTo(title)
                        assertThat(response.description).isEqualTo(description)
                    }
            }
        }

        `when`("스토리 생성 중 예외가 발생하면") {
            coEvery { storyCreateUseCase.execute(any()) } throws RuntimeException("스토리 생성 실패")

            then("에러가 발생한다") {
                client.post()
                    .uri("/story-service")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().is5xxServerError
            }
        }
    }
})