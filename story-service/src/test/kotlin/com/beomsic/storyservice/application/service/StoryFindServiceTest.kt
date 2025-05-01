package com.beomsic.storyservice.application.service

import com.beomsic.storyservice.application.port.out.StoryFindPort
import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.Story
import com.beomsic.storyservice.domain.model.StoryStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("test")
class StoryFindServiceTest : BehaviorSpec({

    val storyFindPort = mockk<StoryFindPort>()
    val storyFindService = StoryFindService(storyFindPort)

    afterTest {
        clearAllMocks()
    }

    given("스토리 ID로 조회할 때") {
        val story = Story(
            id = 1L,
            authorId = 100L,
            title = "test story",
            description = "desc",
            category = Category.DAILY,
            status = StoryStatus.ARCHIVED,
            startDate = LocalDate.of(2025, 4, 1),
            endDate = LocalDate.of(2025, 4, 1),
            createdAt = LocalDateTime.of(2025, 4, 1, 0, 0, 0),
            updatedAt = LocalDateTime.of(2025, 4, 1, 0, 0, 0),
        )

        `when`("존재하는 ID를 조회하면") {
            coEvery { storyFindPort.getById(1L) } returns story

            then("해당 스토리를 반환해야 한다") {
                val result = storyFindService.getById(1L)
                result shouldBe story
                coVerify(exactly = 1) { storyFindPort.getById(1L) }
            }
        }

        `when`("존재하지 않는 ID로 조회하면") {
            coEvery { storyFindPort.getById(999L) } throws StoryNotFoundException()

            then("예외가 발생해야 한다") {
                shouldThrow<StoryNotFoundException> {
                    storyFindService.getById(999L)
                }.message shouldBe StoryNotFoundException().message
            }
        }
    }

    given("스토리 전체 조회를 요청할 때") {
        val page = 0
        val size = 10
        val mockStories = listOf(mockk<Story>(), mockk())
        val mockPage = PageImpl(mockStories)

        coEvery { storyFindPort.findAll(PageRequest.of(page, size)) } returns mockPage

        `when`("기본 페이지 요청을 하면") {
            then("스토리 리스트가 반환되어야 한다") {
                val result = storyFindService.findAll(page, size)
                result.content shouldHaveSize mockStories.size
                result.content shouldBe mockStories
                coVerify(exactly = 1) { storyFindPort.findAll(PageRequest.of(page, size)) }
            }
        }
    }

    given("스토리 상태로 검색 요청을 받을 때") {
        val page = 0
        val size = 10
        val pageable = PageRequest.of(page, size)
        val mockStories = listOf(mockk<Story>(), mockk<Story>())
        val mockPage = PageImpl(mockStories)

        forAll(
            row(StoryStatus.DRAFT),
            row(StoryStatus.ARCHIVED),
            row(StoryStatus.DELETED)
        ) { status ->

            When("상태가 $status 인 경우") {
                coEvery {
                    storyFindPort.findAllByKeyword(status.name, pageable)
                } returns mockPage

                Then("정상적으로 해당 상태의 스토리 페이지를 반환해야 한다") {
                    val result = storyFindService.findAllByKeyword(status.name, page, size)

                    result.totalElements shouldBe mockStories.size
                    result.content shouldContainExactly mockStories

                    coVerify(exactly = 1) {
                        storyFindPort.findAllByKeyword(status.name, pageable)
                    }
                }
            }
        }
    }

    given("키워드로 검색 요청을 받을 때") {
        val keyword = "travel"
        val page = 0
        val size = 10
        val mockStories = listOf(mockk<Story>())
        val mockPage = PageImpl(mockStories)

        coEvery {
            storyFindPort.findAllByKeyword(keyword, PageRequest.of(page, size))
        } returns mockPage

        `when`("해당 키워드로 검색하면") {
            then("해당하는 스토리 페이지를 반환해야 한다") {
                val result = storyFindService.findAllByKeyword(keyword, page, size)
                result.totalElements shouldBe mockStories.size
                result.content shouldBe mockStories
                coVerify(exactly = 1) {
                    storyFindPort.findAllByKeyword(keyword, PageRequest.of(page, size))
                }
            }
        }
    }

    given("사용자 ID로 사용자의 스토리 조회를 요청할 때") {
        val userId = 42L
        val page = 0
        val size = 10
        val mockStories = listOf(mockk<Story>(), mockk())
        val mockPage = PageImpl(mockStories)

        coEvery {
            storyFindPort.findAllByUserId(userId, PageRequest.of(page, size))
        } returns mockPage

        `when`("해당 유저 ID로 요청하면") {
            then("해당 유저의 스토리 리스트가 반환되어야 한다") {
                val result = storyFindService.findAllByUserId(userId, page, size)
                result.totalElements shouldBe mockStories.size
                result.content shouldBe mockStories
                coVerify(exactly = 1) {
                    storyFindPort.findAllByUserId(userId, PageRequest.of(page, size))
                }
            }
        }
    }

    given("유저 ID와 상태값으로 스토리 목록을 조회할 때") {
        val userId = 42L
        val status = "ARCHIVED"
        val page = 0
        val size = 10
        val mockStories = listOf(mockk<Story>(), mockk<Story>(), mockk<Story>())
        val mockPage = PageImpl(mockStories)

        coEvery {
            storyFindPort.findAllByUserIdAndStatus(userId, status, PageRequest.of(page, size))
        } returns mockPage

        `when`("유저 ID와 상태로 요청하면") {
            then("해당 조건의 스토리를 반환해야 한다") {
                val result = storyFindService.findAllMyStoriesByStatus(userId, status, page, size)
                result.totalElements shouldBe mockStories.size
                result.content shouldBe mockStories
                coVerify(exactly = 1) {
                    storyFindPort.findAllByUserIdAndStatus(userId, status, PageRequest.of(page, size))
                }
            }
        }
    }
})
