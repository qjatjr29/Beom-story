package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import com.beomsic.storyservice.domain.model.Story
import com.beomsic.storyservice.domain.model.StoryStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class StoryFindAdapterTest : BehaviorSpec({

    val storyRepository = mockk<StoryRepository>()
    val storyFindAdapter = StoryFindAdapter(storyRepository)

    val mockStory = mockk<Story>()

    val mockStoryEntity = mockk<StoryEntity> {
        coEvery { toDomain() } returns mockStory
    }

    fun createMockStoryEntity(id: Long): StoryEntity {
        val sampleStory = mockk<Story>(relaxed = true) {
            every { this@mockk.id } returns id
        }
        return mockk {
            every { this@mockk.id } returns id
            coEvery { toDomain() } returns sampleStory
        }
    }

    given("스토리를 ID로 조회할 때") {
        val id = 1L

        `when`("스토리가 존재하면") {
            coEvery { storyRepository.findByIdOrNull(id) } returns mockStoryEntity

            then("스토리를 반환해야 한다") {
                val result = storyFindAdapter.getById(id)
                result shouldBe mockStory
            }
        }

        `when`("스토리가 존재하지 않으면") {
            coEvery { storyRepository.findByIdOrNull(id) } throws StoryNotFoundException()

            then("예외가 발생해야 한다") {
                shouldThrow<StoryNotFoundException> {
                    storyFindAdapter.getById(id)
                }.message shouldBe StoryNotFoundException().message
            }
        }
    }

    given("스토리 전체 조회할 때") {
        val pageable = PageRequest.of(0, 10)
        val entityList = listOf(mockStoryEntity, mockStoryEntity)
        val pageResult: Page<StoryEntity> = PageImpl(entityList, pageable, entityList.size.toLong())

        coEvery { storyRepository.findAllWithPaging(pageable) } returns pageResult

        `when`("조회 요청을 하면") {
            then("조회 결과의 페이지를 반환한다") {
                val result = storyFindAdapter.findAll(pageable)
                result.totalElements shouldBe 2
            }
        }
    }

    given("스토리를 상태로 조회할 때") {
        val pageable = PageRequest.of(0, 10)

        forAll (
            row(StoryStatus.DRAFT, List(2) { createMockStoryEntity(it.toLong()) }),
            row(StoryStatus.ARCHIVED, List(3) { createMockStoryEntity((it + 100).toLong()) }),
            row(StoryStatus.DELETED, List(1) { createMockStoryEntity(999L) })
        ) { status, expectedResult ->
            `when`("상태가 $status 인 경우") {
                coEvery { storyRepository.findAllByStatusWithPaging(status.name, pageable) } returns PageImpl(expectedResult)

                then("상태에 맞는 총 ${expectedResult.size} 개의 스토리를 반환한다") {
                    val result = storyFindAdapter.findAllByStatus(status.name, pageable)
                    result.totalElements shouldBe expectedResult.size
                    result.content.map { it.id } shouldBe expectedResult.map { it.id }
                }
            }
        }
    }

    given("아키이브된 상태의 스토리를 조회할 때") {
        val pageable = PageRequest.of(0, 10)
        val entityList = listOf(createMockStoryEntity(1), createMockStoryEntity(2))
        val pageResult: Page<StoryEntity> = PageImpl(entityList)

        coEvery { storyRepository.findArchivedStoriesWithPaging(pageable) } returns pageResult

        `when`("정상 조회시") {
            then("아카이브 상태의 스토리 페이지를 반환한다") {
                val result = storyFindAdapter.findArchivedStories(pageable)
                result.totalElements shouldBe entityList.size
                result.content.map { it.id } shouldBe entityList.map { it.id }
            }
        }
    }

    given("키워드를 통해 스토리를 조회할 때") {
        val pageable = PageRequest.of(0, 10)
        val entityList = listOf(createMockStoryEntity(1), createMockStoryEntity(2))
        val pageResult: Page<StoryEntity> = PageImpl(entityList)
        val keyword = "keyword"

        coEvery { storyRepository.findAllByKeywordWithPaging(keyword, pageable) } returns pageResult

        `when`("정상 조회시") {
            then("아카이브 상태의 스토리 페이지를 반환한다") {
                val result = storyFindAdapter.findAllByKeyword(keyword, pageable)
                result.totalElements shouldBe entityList.size
                result.content.map { it.id } shouldBe entityList.map { it.id }
            }
        }
    }

    given("작성 유저 ID를 통해 스토리를 조회할 때") {
        val pageable = PageRequest.of(0, 10)
        val entityList = listOf(createMockStoryEntity(1), createMockStoryEntity(2))
        val pageResult: Page<StoryEntity> = PageImpl(entityList)
        val userId = 1L

        coEvery { storyRepository.findAllByUserIdWithPaging(userId, pageable) } returns pageResult

        `when`("정상 조회시") {
            then("아카이브 상태의 스토리 페이지를 반환한다") {
                val result = storyFindAdapter.findAllByUserId(userId, pageable)
                result.totalElements shouldBe entityList.size
                result.content.map { it.id } shouldBe entityList.map { it.id }
            }
        }
    }

    given("특정 유저의 status 별 스토리 조회할 때") {
        val pageable = PageRequest.of(0, 10)
        val userId = 1L

        forAll(
            row(StoryStatus.DRAFT, List(2) { createMockStoryEntity(it.toLong()) }),
            row(StoryStatus.ARCHIVED, List(3) { createMockStoryEntity((it + 100).toLong()) }),
            row(StoryStatus.DELETED, List(1) { createMockStoryEntity(999L) })
        ) { status, expectedResult ->
            `when`("상태가 $status 인 경우") {
                coEvery {
                    storyRepository.findAllByUserIdAndStatusWithPaging(userId, status.name, pageable)
                } returns PageImpl(expectedResult)

                then("상태에 맞는 총 ${expectedResult.size} 개의 스토리를 반환한다") {
                    val result = storyFindAdapter.findAllByUserIdAndStatus(userId, status.name, pageable)
                    result.totalElements shouldBe expectedResult.size
                    result.content.map { it.id } shouldBe expectedResult.map { it.id }
                }
            }
        }

    }
})
