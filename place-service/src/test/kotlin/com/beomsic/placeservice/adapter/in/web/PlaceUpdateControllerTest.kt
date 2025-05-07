package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.placeservice.application.port.`in`.usecase.PlaceUpdateUseCase
import com.beomsic.placeservice.domain.Place
import com.beomsic.placeservice.domain.exception.ForbiddenException
import com.beomsic.placeservice.domain.exception.PlaceNotFoundException
import com.beomsic.placeservice.infra.config.AuthTokenResolver
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.time.LocalDate

@WebFluxTest(PlaceUpdateController::class)
@ActiveProfiles("test")
class PlaceUpdateControllerTest: BehaviorSpec({

    val placeUpdateUseCase = mockk<PlaceUpdateUseCase>()
    val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()

    val client = WebTestClient
        .bindToController(PlaceUpdateController(placeUpdateUseCase))
        .argumentResolvers { it.addCustomResolver(AuthTokenResolver()) }
        .controllerAdvice(globalExceptionHandler)
        .configureClient()
        .build()

    afterEach {
        clearAllMocks()
    }

    given("장소 내용 업데이트 API 호출 시") {
        val placeId = 1L
        val userId = 2L
        val email = "test@test.com"
        val request = PlaceUpdateContentRequest(
            name = "Updated Name",
            description = "Updated Description",
            category = "RESTAURANT",
            longitude = 127.0,
            latitude = 37.0,
            address = "seoul",
            visitedDate = LocalDate.of(2025, 5, 7)
        )

        `when`("정상 요청일 경우") {
            val updatedPlace = mockk<Place>(relaxed = true)

            coEvery { placeUpdateUseCase.updateContent(any()) } returns updatedPlace

            then("200 OK와 업데이트된 장소 정보를 응답한다") {
                client.put()
                    .uri("/place-service/${placeId}/content")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer test-token")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk
            }
        }

        `when`("해당 장소의 작성자가 아닌 경우") {
            coEvery { placeUpdateUseCase.updateContent(any()) } throws ForbiddenException()

            then("403 Forbidden을 응답한다") {
                client.put()
                    .uri("/place-service/$placeId/content")
                    .bodyValue(request)
                    .header("Authorization", "Bearer test-token")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isForbidden
            }
        }

        `when`("존재하지 않는 장소인 경우") {
            coEvery { placeUpdateUseCase.updateContent(any()) } throws PlaceNotFoundException()

            then("404 Not Found를 응답한다") {
                client.put()
                    .uri("/place-service/$placeId/content")
                    .bodyValue(request)
                    .header("Authorization", "Bearer test-token")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isNotFound
            }
        }
    }

    given("장소 이미지 업데이트 요청시") {
        val placeId = 2L
        val userId = 1L
        val email = "test@test.com"

        `when`("정상 요청일 경우") {
            coEvery { placeUpdateUseCase.updateImage(placeId, userId, any<FilePart>()) } just Runs

            then("204 No Content를 응답한다") {
                client.patch()
                    .uri("/place-service/$placeId/image")
                    .header("Authorization", "Bearer test-token")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(
                        BodyInserters.fromMultipartData(
                            MultipartBodyBuilder().apply {
                                part("image", "fake-content".toByteArray())
                                    .filename("image.jpg")
                                    .header("Content-Type", "image/jpeg")
                            }.build()
                        )
                    )
                    .exchange()
                    .expectStatus().isNoContent
            }
        }

        `when`("장소 작성자가 아닌 사람의 요청인 경우") {
            coEvery { placeUpdateUseCase.updateImage(placeId, userId, any<FilePart>()) } throws ForbiddenException()

            then("403 Forbidden을 응답한다") {
                client.patch()
                    .uri("/place-service/$placeId/image")
                    .header("Authorization", "Bearer test-token")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .body(
                        BodyInserters.fromMultipartData(
                            MultipartBodyBuilder().apply {
                                part("image", "fake-content".toByteArray())
                                    .filename("image.jpg")
                                    .header("Content-Type", "image/jpeg")
                            }.build()
                        )
                    )
                    .exchange()
                    .expectStatus().isForbidden
            }
        }

        When("장소 Id에 해당하는 엔티티가 없는 경우") {
            coEvery { placeUpdateUseCase.updateImage(placeId, userId, any<FilePart>()) } throws PlaceNotFoundException()

            Then("404 Not Found를 응답한다") {
                client.patch()
                    .uri("/place-service/$placeId/image")
                    .header("Authorization", "Bearer test-token")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .body(
                        BodyInserters.fromMultipartData(
                            MultipartBodyBuilder().apply {
                                part("image", "fake-content".toByteArray())
                                    .filename("image.jpg")
                                    .header("Content-Type", "image/jpeg")
                            }.build()
                        )
                    )
                    .exchange()
                    .expectStatus().isNotFound
            }
        }
    }

})