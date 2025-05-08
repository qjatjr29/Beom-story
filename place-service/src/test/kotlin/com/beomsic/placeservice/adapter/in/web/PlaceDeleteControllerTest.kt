package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.placeservice.application.port.`in`.usecase.PlaceDeleteUseCase
import com.beomsic.placeservice.domain.exception.PlaceNotFoundException
import com.beomsic.placeservice.domain.exception.UnauthorizedPlaceAccessException
import com.beomsic.placeservice.infra.config.AuthTokenResolver
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(PlaceDeleteController::class)
@ActiveProfiles("test")
class PlaceDeleteControllerTest: BehaviorSpec({
    val placeDeleteUseCase = mockk<PlaceDeleteUseCase>()
    val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()

    val client = WebTestClient
        .bindToController(PlaceDeleteController(placeDeleteUseCase))
        .argumentResolvers { it.addCustomResolver(AuthTokenResolver()) }
        .controllerAdvice(globalExceptionHandler)
        .configureClient()
        .build()

    afterEach {
        clearAllMocks()
    }

    val placeId = 1L
    val authorId = 100L
    val otherUserId = 200L
    val email = "user@test.com"

    given("장소 ID로 삭제 요청시") {
        `when`("작성자가 요청한 경우") {
            coEvery { placeDeleteUseCase.deleteById(placeId, authorId) } just Runs

            then("204 NO CONTENT 상태코드와 함께 삭제된다.") {
                client.delete()
                    .uri("/place-service/$placeId")
                    .header("Authorization", "Bearer mock-token")
                    .header("userId", authorId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isNoContent
            }
        }

        `when`("작성자가 아닌 사용자가 요청한 경우") {
            coEvery { placeDeleteUseCase.deleteById(placeId, otherUserId) } throws UnauthorizedPlaceAccessException()

            then("403 FORBIDDEN 응답을 반환한다.") {
                client.delete()
                    .uri("/place-service/$placeId")
                    .header("Authorization", "Bearer mock-token")
                    .header("userId", otherUserId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isForbidden
            }
        }

        `when`("존재하지 않는 장소 ID일 경우") {
            coEvery { placeDeleteUseCase.deleteById(placeId, authorId) } throws PlaceNotFoundException()

            then("404 Not Found 응답을 반환한다.") {
                client.delete()
                    .uri("/place-service/$placeId")
                    .header("Authorization", "Bearer mock-token")
                    .header("userId", authorId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isNotFound
            }
        }
    }
})