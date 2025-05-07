package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.common.exception.ErrorResponse
import com.beomsic.placeservice.application.port.`in`.usecase.PlaceFindUseCase
import com.beomsic.placeservice.domain.Category
import com.beomsic.placeservice.domain.Place
import com.beomsic.placeservice.domain.exception.PlaceNotFoundException
import com.beomsic.placeservice.infra.config.AuthTokenResolver
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import java.time.LocalDateTime

@WebFluxTest(PlaceFindController::class)
@ActiveProfiles("test")
class PlaceFindControllerTest: BehaviorSpec ({

    val placeFindUseCase = mockk<PlaceFindUseCase>()
    val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()

    val client = WebTestClient
        .bindToController(PlaceFindController(placeFindUseCase))
        .argumentResolvers { it.addCustomResolver(AuthTokenResolver()) }
        .controllerAdvice(globalExceptionHandler)
        .configureClient()
        .build()

    fun createMockPlace(placeId: Long = 1L, storyId: Long = 2L, authorId: Long = 3L): Place {
        return mockk<Place>(relaxed = true) {
            every { this@mockk.id } returns placeId
            every { this@mockk.storyId } returns storyId
            every { this@mockk.authorId } returns authorId
            every { this@mockk.name } returns "place name"
            every { this@mockk.description } returns "place description"
            every { this@mockk.imageUrl } returns "image/com"
            every { this@mockk.category } returns Category.ATTRACTION
            every { this@mockk.latitude } returns 2.0
            every { this@mockk.longitude } returns 2.0
            every { this@mockk.address } returns "place address"
            every { this@mockk.visitedDate } returns LocalDate.of(2025, 5, 1)
            every { this@mockk.createdAt } returns LocalDateTime.of(2025, 5, 2, 11, 11, 11)
            every { this@mockk.updatedAt } returns LocalDateTime.of(2025, 5, 2, 11, 11, 11)
        }
    }

    fun createMockPlaces(count: Int, storyId: Long, authorId: Long = 3L): List<Place> {
        return (1..count).map { createMockPlace(placeId = it.toLong(), storyId = it.toLong(), authorId = it.toLong()) }
    }

    afterEach {
        clearAllMocks()
    }

    given("일상 ID로 조회 요청시") {
        val storyId = 2L
        val mockPlaces = createMockPlaces(3, storyId = storyId)

        `when`("일상에 포함된 장소들이 있는 경우"){
            coEvery { placeFindUseCase.findAllByStoryId(storyId) } returns mockPlaces
            then("일상에 포함된 장소들의 리스트가 반환된다.") {
                client.get()
                    .uri("/place-service/story/$storyId")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk
                    .expectBodyList(PlaceDetailResponse::class.java)
                    .hasSize(mockPlaces.size)
            }
        }
    }

    given("장소 ID로 조회 요청 시") {
        val placeId = 1L
        val mockPlace = createMockPlace(placeId = placeId)

        `when`("해당 장소가 존재하는 경우") {
            coEvery { placeFindUseCase.findByPlaceId(placeId) } returns mockPlace

            then("200 OK와 함께 장소 정보가 반환된다") {
                client.get()
                    .uri("/place-service/$placeId")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody(PlaceDetailResponse::class.java)
                    .value {
                        assertThat(it.id).isEqualTo(placeId)
                        assertThat(it.name).isEqualTo(mockPlace.name)
                    }
            }
        }

        `when`("해당 장소가 존재하지 않는 경우") {
            coEvery { placeFindUseCase.findByPlaceId(placeId) } throws PlaceNotFoundException()

            then("404 NOT FOUND를 반환한다") {
                client.get()
                    .uri("/place-service/$placeId")
                    .exchange()
                    .expectStatus().isNotFound
                    .expectBody(ErrorResponse::class.java)
                    .value { response ->
                        assertThat(response.message).isEqualTo(PlaceNotFoundException().message)
                        assertThat(response.code).isEqualTo(404)
                    }
            }
        }
    }
})