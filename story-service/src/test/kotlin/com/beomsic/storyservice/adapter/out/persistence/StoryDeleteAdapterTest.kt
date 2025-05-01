package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import com.beomsic.storyservice.domain.exception.UnauthorizedStoryAccessException
import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.StoryStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("test")
class StoryDeleteAdapterTest: BehaviorSpec({

    val storyRepository = mockk<StoryRepository>()
    val storyDeleteAdapter = StoryDeleteAdapter(storyRepository)

    val storyId = 1L
    val userId = 2L

    afterEach {
        clearAllMocks()
    }

    given("스토리 삭제 요청이 들어왔을 때") {
        `when`("요청한 유저가 작성자라면") {
            val storyEntity = StoryEntity(
                id = storyId,
                authorId = userId,
                title = "제목",
                description = "내용",
                category = Category.TRAVEL.name,
                startDate = LocalDate.of(2025, 5, 1),
                endDate = LocalDate.of(2025, 5, 1),
                status = StoryStatus.ARCHIVED.name,
                createdAt = LocalDateTime.of(2025, 5, 1, 12, 0, 0),
                updatedAt = LocalDateTime.of(2025, 5, 1, 12, 0, 0),
            )
            coEvery { storyRepository.findByIdOrNull(storyId) } returns storyEntity
            coEvery { storyRepository.deleteById(storyId) } just Runs

            then("스토리를 정상적으로 삭제한다") {
                storyDeleteAdapter.deleteStory(userId, storyId)

                coVerifyOrder {
                    storyRepository.findByIdOrNull(storyId)
                    storyRepository.deleteById(storyId)
                }
            }
        }

        `when`("작성자가 아닌 경우") {
            val storyEntity = StoryEntity(
                id = storyId,
                authorId = 999L,
                title = "제목",
                description = "내용",
                category = Category.TRAVEL.name,
                startDate = LocalDate.of(2025, 5, 1),
                endDate = LocalDate.of(2025, 5, 1),
                status = StoryStatus.ARCHIVED.name,
                createdAt = LocalDateTime.of(2025, 5, 1, 12, 0, 0),
                updatedAt = LocalDateTime.of(2025, 5, 1, 12, 0, 0),
            )

            coEvery { storyRepository.findByIdOrNull(storyId) } returns storyEntity

            then("UnauthorizedStoryAccess Exception이 발생한다") {
                shouldThrow<UnauthorizedStoryAccessException> {
                    storyDeleteAdapter.deleteStory(userId, storyId)
                }.message shouldBe UnauthorizedStoryAccessException().message

                coVerify(exactly = 0) { storyRepository.deleteById(any()) }
            }
        }

        `when`("스토리가 존재하지 않는 경우") {
            coEvery { storyRepository.findById(storyId) } returns null

            then("StoryNotFound Exception이 발생한다.") {
                shouldThrow<StoryNotFoundException> {
                    storyDeleteAdapter.deleteStory(userId, storyId)
                }.message shouldBe StoryNotFoundException().message

                coVerify(exactly = 0) { storyRepository.deleteById(any()) }
            }
        }
    }

})