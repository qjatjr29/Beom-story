package com.beomsic.storyservice.adapter.`in`.web

import com.beomsic.common.model.AuthUser
import com.beomsic.storyservice.application.port.`in`.usecase.StoryFindUseCase
import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.Story
import com.beomsic.storyservice.domain.model.StoryStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("test")
class StoryFindControllerTest : BehaviorSpec({

    val storyFindUseCase = mockk<StoryFindUseCase>()
    val controller = StoryFindController(storyFindUseCase)

    fun createStory(id: Long = 1L): Story {
        return Story(
            id = id,
            authorId = 1L,
            title = "Mock Title",
            description = "Mock Description",
            category = Category.DAILY,
            status = StoryStatus.DRAFT,
            startDate = LocalDate.of(2025, 4, 30),
            endDate = LocalDate.of(2025, 5, 1),
            createdAt = LocalDateTime.of(2025, 5, 1, 0, 0),
            updatedAt = LocalDateTime.of(2025, 5, 1, 0, 0),
        )
    }

    val mockStory = createStory()
    val mockStories = listOf(mockStory, mockStory)
    val mockPage = PageImpl(mockStories)

    afterTest {
        clearAllMocks()
    }

    given("스토리 ID로 상세 조회") {
        val id = 1L

        `when`("정상적으로 조회되는 경우") {
            coEvery { storyFindUseCase.getById(id) } returns mockStory

            then("StoryDetailResponse를 반환한다") {
                val response = controller.getStoryDetails(id)
                response.id shouldBe mockStory.id
            }
        }

        `when`("스토리를 찾을 수 없는 경우") {
            coEvery { storyFindUseCase.getById(id) } throws StoryNotFoundException()

            then("예외를 던진다") {
                shouldThrow<StoryNotFoundException> {
                    controller.getStoryDetails(id)
                }.message shouldBe StoryNotFoundException().message
            }
        }
    }

    given("내가 작성한 스토리 목록 조회") {
        val userId = 1L
        val authUser = AuthUser(userId, "test@test.com", "access-token", "refresh-token")

        `when`("정상 조회") {
            coEvery { storyFindUseCase.findAllByUserId(userId, 0, 10) } returns mockPage

            then("작성한 스토리 페이지를 반환한다") {
                val result = controller.findMyStories(authUser, 0, 10)
                result.totalElements shouldBe mockStories.size
                result.content.map { it.id } shouldBe mockStories.map { it.id }
            }
        }
    }

    given("유저 ID로 스토리 조회") {
        val userId = 42L
        `when`("정상 조회") {
            coEvery { storyFindUseCase.findAllByUserId(userId, 0, 10) } returns mockPage

            then("스토리 페이지를 반환한다") {
                val result = controller.findStoriesByUserId(userId, 0, 10)
                result.content.size shouldBe mockStories.size
                result.content.map { it.id } shouldBe mockStories.map { it.id }
            }
        }
    }

    given("내 스토리를 상태값 기준으로 조회") {
        val userId = 1L
        val authUser = AuthUser(userId, "test@test.com", "access-token", "refresh-token")

        forAll(
            row(StoryStatus.DRAFT, List(2) { createStory(it.toLong()) }),
            row(StoryStatus.ARCHIVED, List(3) { createStory((it + 100).toLong()) }),
            row(StoryStatus.DELETED, List(1) { createStory(999L) }),
        ) { status, expectedResult ->
            `when`("상태가 $status 인 경우") {
                coEvery {
                    storyFindUseCase.findAllMyStoriesByStatus(authUser.id, status.name, 0, 10)
                } returns PageImpl(expectedResult)

                then("상태에 맞는 총 ${expectedResult.size} 개의 스토리를 반환한다") {
                    val result = controller.findStoriesByStatus(authUser, status.name, 0, 10)
                    result.totalElements shouldBe expectedResult.size
                    result.content.map { it.id } shouldBe expectedResult.map { it.id }
                }
            }
        }
    }

    given("전체 스토리 조회") {
        `when`("정상 조회") {
            coEvery { storyFindUseCase.findAll(0, 10) } returns mockPage

            then("모든 스토리를 반환한다") {
                val result = controller.findAllStories(0, 10)
                result.totalElements shouldBe mockStories.size
                result.content.map { it.id } shouldBe mockStories.map { it.id }
            }
        }
    }

    given("아카이브된 스토리 조회") {
        `when`("정상 조회시") {
            coEvery { storyFindUseCase.findArchivedStories(0, 10) } returns mockPage

            then("스토리를 반환한다") {
                val result = controller.findAllArchivedStories(0, 10)
                result.totalElements shouldBe mockStories.size
                result.content.map { it.id } shouldBe mockStories.map { it.id }
            }
        }
    }

    given("상태별 스토리 조회") {
        forAll(
            row(StoryStatus.DRAFT, List(2) { createStory(it.toLong()) }),
            row(StoryStatus.ARCHIVED, List(3) { createStory((it + 100).toLong()) }),
            row(StoryStatus.DELETED, List(1) { createStory(999L) }),
        ) { status, expectedResult ->
            `when`("상태가 $status 인 경우") {
                coEvery {
                    storyFindUseCase.findAllByStatus(status.name, 0, 10)
                } returns PageImpl(expectedResult)

                then("상태에 맞는 총 ${expectedResult.size} 개의 스토리를 반환한다") {
                    val result = controller.findAllByStatus(status.name, 0, 10)
                    result.totalElements shouldBe expectedResult.size
                    result.content.map { it.id } shouldBe expectedResult.map { it.id }
                }
            }
        }
    }

    given("키워드 기반 검색") {
        val keyword = "travel"

        `when`("정상 검색") {
            coEvery { storyFindUseCase.findAllByKeyword(keyword, 0, 10) } returns mockPage

            then("검색 결과를 반환한다") {
                val result = controller.findAllStoriesByKeyword(keyword, 0, 10)
                result.totalElements shouldBe mockStories.size
                result.content.map { it.id } shouldBe mockStories.map { it.id }
            }
        }
    }
})
