package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.placeservice.application.port.`in`.usecase.PlaceCreateUseCase
import com.beomsic.placeservice.domain.Category
import com.beomsic.placeservice.domain.Place
import com.beomsic.placeservice.infra.config.AuthTokenResolver
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.time.LocalDate
import java.time.LocalDateTime

@WebFluxTest(PlaceCreateController::class)
@ActiveProfiles("test")
class PlaceCreateControllerTest: BehaviorSpec ({

    val placeCreateUseCase = mockk<PlaceCreateUseCase>()
//    val placeCreateController = PlaceCreateController(placeCreateUseCase)

    val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()

    val client = WebTestClient
        .bindToController(PlaceCreateController(placeCreateUseCase))
        .argumentResolvers { it.addCustomResolver(AuthTokenResolver()) }
        .controllerAdvice(globalExceptionHandler)
        .configureClient()
        .build()

    afterEach {
        clearAllMocks()
    }

    val storyId = 2L
    val userId = 2L
    val email = "test@test.com"

    val mockPlace = Place(
        id = 1L,
        storyId = storyId,
        authorId = userId,
        name = "Place name",
        description = "place description",
        category = Category.CAFE,
        latitude = 1.0,
        longitude = 2.0,
        address = "123 Road",
        imageUrl = "https://img.com",
        visitedDate = LocalDate.of(2025, 5, 4),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    val request = PlaceCreateRequest(
        storyId = storyId,
        name = "place name",
        description = "place description",
        category = "CAFE",
        latitude = 1.0,
        longitude = 2.0,
        address = "123 Road",
        visitedDate = LocalDate.of(2025, 5, 4)
    )

    given("장소 생성 api 호출시") {

        `when`("이미지를 포함하지 않은 정상적인 요청인 경우") {
            coEvery { placeCreateUseCase.execute(any(), null) } returns mockPlace

            then("CREATED (201) 코드와 함께 Place 정보를 반환한다.") {
                client.post()
                    .uri("/place-service")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(
                        BodyInserters.fromMultipartData(
                            MultipartBodyBuilder().apply {
                                part("request", request, MediaType.APPLICATION_JSON)
                            }.build()
                        )
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer mock-access-token")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isCreated
                    .expectBody(Place::class.java)
                    .value { response ->
                        assertThat(response.id).isEqualTo(mockPlace.id)
                        assertThat(response.storyId).isEqualTo(storyId)
                        assertThat(response.authorId).isEqualTo(userId)
                        assertThat(response.name).isEqualTo(mockPlace.name)
                    }
            }
        }

        `when`("이미지를 포함한 정상적인 요청인 경우") {
            coEvery { placeCreateUseCase.execute(any(), any()) } returns mockPlace
            then("CREATED (201) 코드와 함께 Place 정보를 반환한다.") {
                client.post()
                    .uri("/place-service")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(
                        BodyInserters.fromMultipartData(
                            MultipartBodyBuilder().apply {
                                part("request", request, MediaType.APPLICATION_JSON)
                                part("image", "fake-content".toByteArray())
                                    .filename("image.jpg")
                                    .header("Content-Type", "image/jpeg")
                            }.build()
                        )
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer mock-access-token")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isCreated
                    .expectBody(Place::class.java)
                    .value { response ->
                        assertThat(response.id).isEqualTo(mockPlace.id)
                        assertThat(response.storyId).isEqualTo(storyId)
                        assertThat(response.authorId).isEqualTo(userId)
                        assertThat(response.name).isEqualTo(mockPlace.name)
                        assertThat(response.imageUrl).isEqualTo(mockPlace.imageUrl)
                    }
            }
        }

        `when`("유효하지 않은 request 데이터가 들어오면") {
            then("400 Bad Request 를 응답한다.") {
                val invalidRequest = mapOf("invalid" to "value")

                client.post()
                    .uri("/place-service")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(
                        BodyInserters.fromMultipartData(
                            MultipartBodyBuilder().apply {
                                part("request", invalidRequest, MediaType.APPLICATION_JSON)
                            }.build()
                        )
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer mock-access-token")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isBadRequest
            }
        }

        `when`("UseCase 에서 예외가 발생한 경우") {
            then("500 Server Error 응답을 반환한다.") {
                coEvery { placeCreateUseCase.execute(any(), any()) } throws RuntimeException()

                client.post()
                    .uri("/place-service")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(
                        BodyInserters.fromMultipartData(
                            MultipartBodyBuilder().apply {
                                part("request", request, MediaType.APPLICATION_JSON)
                            }.build()
                        )
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer mock-access-token")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().is5xxServerError
            }
        }
    }


})