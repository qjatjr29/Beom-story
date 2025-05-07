package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.common.exception.ErrorResponse
import com.beomsic.storyservice.application.port.`in`.usecase.StoryFindUseCase
import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.Story
import com.beomsic.storyservice.domain.model.StoryStatus
import com.beomsic.storyservice.infrastructure.config.AuthTokenResolver
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import java.time.LocalDateTime


@WebFluxTest(StoryCreateController::class)
@ActiveProfiles("test")
class StoryFindControllerIntegrationTest: BehaviorSpec({

    val storyFindUseCase = mockk<StoryFindUseCase>()
    val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()

    val client = WebTestClient
        .bindToController(StoryFindController(storyFindUseCase))
        .argumentResolvers { it.addCustomResolver(AuthTokenResolver()) }
        .controllerAdvice(globalExceptionHandler)
        .configureClient()
        .build()

    val title = "Test Title"
    val description = "Test Description"
    val objectMapper = ObjectMapper()

    afterEach {
        clearAllMocks()
    }

    fun createMockStory(storyId: Long = 1L, authorId: Long = 2L): Story {
        return mockk<Story>(relaxed = true) {
            every { this@mockk.id } returns storyId
            every { this@mockk.authorId } returns authorId
            every { this@mockk.title } returns title
            every { this@mockk.description } returns description
            every { this@mockk.category } returns Category.DAILY
            every { this@mockk.status } returns StoryStatus.ARCHIVED
            every { this@mockk.startDate } returns LocalDate.of(2025, 4, 1)
            every { this@mockk.endDate  } returns LocalDate.of(2025, 4, 2)
            every { this@mockk.createdAt } returns LocalDateTime.of(2025, 4, 2, 11, 11, 11)
            every { this@mockk.updatedAt } returns LocalDateTime.of(2025, 4, 2, 11, 11, 11)
        }
    }

    fun createMockStories(count: Int, authorId: Long = 1L): List<Story> {
        return (1..count).map { createMockStory(storyId = it.toLong(), authorId = authorId) }
    }


    given("스토리 ID를 통해서 특정 스토리 조회 요청 시") {
        val storyId = 1L
        val mockStory = createMockStory(storyId)

        `when`("스토리를 정상적으로 조회할 경우") {
            coEvery { storyFindUseCase.getById(storyId) } returns mockStory

            then("StoryDetailResponse 를 반환한다") {
                client.get()
                    .uri("/story-service/$storyId")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody(StoryDetailResponse::class.java)
                    .value { response ->
                        assertThat(response.id).isEqualTo(storyId)
                        assertThat(response.title).isEqualTo(title)
                        assertThat(response.description).isEqualTo(description)
                    }
            }
        }

        `when`("없는 스토리 ID로 조회할 경우") {
            val id = 2L
            coEvery { storyFindUseCase.getById(id) } throws StoryNotFoundException()

            then("에러를 반환한다") {
                client.get()
                    .uri("/story-service/$id")
                    .exchange()
                    .expectStatus().isNotFound
                    .expectBody(ErrorResponse::class.java)
                    .value { response ->
                        assertThat(response.message).isEqualTo(StoryNotFoundException().message)
                        assertThat(response.code).isEqualTo(404)
                    }
            }
        }
    }

    given("자신의 스토리 조회 요청 시") {
        val userId = 1L
        val mockStories = createMockStories(3, userId)
        val mockPage = PageImpl(mockStories, PageRequest.of(0, 10), mockStories.size.toLong())
        val accessToken = "accessToken"
        val email = "test@example.com"

        `when`("스토리를 정상적으로 조회할 경우") {
            coEvery { storyFindUseCase.findAllByUserId(userId, 0, 10) } returns mockPage
            then("StorySummaryResponse 페이지를 반환한다") {
                client.get()
                    .uri("/story-service/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                    .header("userId", userId.toString())
                    .header("email", email)
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { response ->
                        val responseBody = response.responseBody
                        val responseJson = objectMapper.readTree(responseBody)
                        val contentArray = responseJson.get("content")

                        mockStories.forEachIndexed { index, story ->
                            val jsonStory = contentArray[index]
                            assertThat(jsonStory["id"].asLong()).isEqualTo(story.id)
                            assertThat(jsonStory["title"].asText()).isEqualTo(story.title)
                        }
                        assertThat(responseJson.get("totalElements").asInt()).isEqualTo(mockStories.size)
                    }
            }
        }
    }

    given("스토리 상태를 통해 자신의 스토리 조회 요청 시") {
        val userId = 1L
        val accessToken = "accessToken"
        val email = "test@example.com"

        forAll(
            row(StoryStatus.DRAFT, createMockStories(2, userId)),
            row(StoryStatus.ARCHIVED, createMockStories(1, userId)),
            row(StoryStatus.DELETED, createMockStories(3, userId))
        ) { status, mockStories ->

            val mockPage = PageImpl(mockStories, PageRequest.of(0, 10), mockStories.size.toLong())

            `when`("user ID에 해당하는 스토리를 정상적으로 조회할 경우") {

                coEvery {
                    storyFindUseCase.findAllMyStoriesByStatus(userId, status.name, 0, 10)
                } returns mockPage

                then("StorySummaryResponse 페이지를 반환한다") {
                    client.get()
                        .uri("/story-service/me/status?status=${status}")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .header("userId", userId.toString())
                        .header("email", email)
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .consumeWith { response ->
                            val responseBody = response.responseBody
                            val responseJson = objectMapper.readTree(responseBody)
                            val contentArray = responseJson.get("content")

                            mockStories.forEachIndexed { index, story ->
                                val jsonStory = contentArray[index]
                                assertThat(jsonStory["id"].asLong()).isEqualTo(story.id)
                                assertThat(jsonStory["title"].asText()).isEqualTo(story.title)
                            }
                            assertThat(responseJson.get("totalElements").asInt()).isEqualTo(mockStories.size)
                        }
                }
            }
        }
    }

    given("유저 ID를 통한 스토리 조회 요청 시") {
        val userId = 1L
        val mockStories = createMockStories(2, userId)
        val mockPage = PageImpl(mockStories, PageRequest.of(0, 10), mockStories.size.toLong())

        `when`("user ID에 해당하는 스토리를 정상적으로 조회할 경우") {
            coEvery { storyFindUseCase.findAllByUserId(userId, 0, 10) } returns mockPage
            then("StorySummaryResponse 페이지를 반환한다") {
                client.get()
                    .uri("/story-service/user?id=${userId}")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { response ->
                        val responseBody = response.responseBody
                        val responseJson = objectMapper.readTree(responseBody)
                        val contentArray = responseJson.get("content")

                        mockStories.forEachIndexed { index, story ->
                            val jsonStory = contentArray[index]
                            assertThat(jsonStory["id"].asLong()).isEqualTo(story.id)
                            assertThat(jsonStory["title"].asText()).isEqualTo(story.title)
                        }
                        assertThat(responseJson.get("totalElements").asInt()).isEqualTo(mockStories.size)
                    }
            }
        }
    }

    given("모든 스토리 조회 요청 시") {
        val mockStories = createMockStories(2)
        val mockPage = PageImpl(mockStories, PageRequest.of(0, 10), mockStories.size.toLong())

        `when`("전체 스토리를 정상적으로 조회할 경우") {
            coEvery { storyFindUseCase.findAll(0, 10) } returns mockPage
            then("StorySummaryResponse 페이지를 반환한다") {
                client.get()
                    .uri("/story-service")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { response ->
                        val responseBody = response.responseBody
                        val responseJson = objectMapper.readTree(responseBody)
                        val contentArray = responseJson.get("content")

                        mockStories.forEachIndexed { index, story ->
                            val jsonStory = contentArray[index]
                            assertThat(jsonStory["id"].asLong()).isEqualTo(story.id)
                            assertThat(jsonStory["title"].asText()).isEqualTo(story.title)
                        }
                        assertThat(responseJson.get("totalElements").asInt()).isEqualTo(mockStories.size)
                    }
            }
        }
    }

    given("스토리 상태별 조회 요청 시") {

        forAll(
            row(StoryStatus.DRAFT, createMockStories(2)),
            row(StoryStatus.ARCHIVED, createMockStories(1)),
            row(StoryStatus.DELETED, createMockStories(3))
        ) { status, mockStories ->

            val mockPage = PageImpl(mockStories, PageRequest.of(0, 10), mockStories.size.toLong())

            `when`("${status} 상태의 스토리를 정상적으로 조회할 경우") {
                coEvery { storyFindUseCase.findAllByStatus(status.name, 0, 10) } returns mockPage
                then("StorySummaryResponse 페이지를 반환한다") {
                    client.get()
                        .uri("/story-service/status?status=${status.name}")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .consumeWith { response ->
                            val responseBody = response.responseBody
                            val responseJson = objectMapper.readTree(responseBody)
                            val contentArray = responseJson.get("content")

                            mockStories.forEachIndexed { index, story ->
                                val jsonStory = contentArray[index]
                                assertThat(jsonStory["id"].asLong()).isEqualTo(story.id)
                                assertThat(jsonStory["title"].asText()).isEqualTo(story.title)
                            }
                            assertThat(responseJson.get("totalElements").asInt()).isEqualTo(mockStories.size)
                        }
                }
            }
        }
    }

    given("키워드를 통해 스토리 검색 요청 시") {
        val keyword = "keyword"
        val mockStories = createMockStories(2)
        val mockPage = PageImpl(mockStories, PageRequest.of(0, 10), mockStories.size.toLong())

        `when`("키워드에 맞는 스토리를 정상적으로 조회할 경우") {
            coEvery { storyFindUseCase.findAllByKeyword(keyword, 0, 10) } returns mockPage
            then("StorySummaryResponse 페이지를 반환한다") {
                client.get()
                    .uri("/story-service/search?keyword=$keyword")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody()
                    .consumeWith { response ->
                        val responseBody = response.responseBody
                        val responseJson = objectMapper.readTree(responseBody)
                        val contentArray = responseJson.get("content")

                        mockStories.forEachIndexed { index, story ->
                            val jsonStory = contentArray[index]
                            assertThat(jsonStory["id"].asLong()).isEqualTo(story.id)
                            assertThat(jsonStory["title"].asText()).isEqualTo(story.title)
                        }
                        assertThat(responseJson.get("totalElements").asInt()).isEqualTo(mockStories.size)
                    }
            }
        }
    }
})