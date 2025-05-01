package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.application.port.`in`.command.StoryUpdateCommand
import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import com.beomsic.storyservice.domain.exception.UnauthorizedStoryAccessException
import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.StoryStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("test")
class StoryUpdateAdapterTest : BehaviorSpec({

    val storyRepository = mockk<StoryRepository>()
    val storyUpdateAdapter = StoryUpdateAdapter(storyRepository)

    val storyId = 1L
    val authorId = 100L
    val otherUserId = 999L

    val mockStoryEntity = StoryEntity(
        id = storyId,
        authorId = authorId,
        title = "Original Title",
        description = "Original Desc",
        category = Category.TRAVEL.name,
        status = StoryStatus.DRAFT.name,
        startDate = LocalDate.of(2025, 5, 1),
        endDate = LocalDate.of(2025, 5, 2),
        createdAt = LocalDateTime.of(2025, 5, 1, 12, 0, 0),
        updatedAt = LocalDateTime.of(2025, 5, 1, 12, 0, 0)
    )

    val updatedEntitySlot = slot<StoryEntity>()

    afterEach {
        clearAllMocks()
    }

    given("스토리 status를 업데이트 요청 시") {
        val status = StoryStatus.ARCHIVED

        `when`("작성자와 일치하면") {
            then("상태가 정상적으로 업데이트된다") {
                coEvery { storyRepository.findByIdOrNull(storyId) } returns mockStoryEntity
                coEvery { storyRepository.save(capture(updatedEntitySlot)) } answers { updatedEntitySlot.captured }

                storyUpdateAdapter.updateStatus(storyId, authorId, status.name)

                updatedEntitySlot.captured.status shouldBe status.name
            }
        }

        `when`("없는 스토리에 업데이트 요청을 한다면") {
            then("StoryNotFound Exception이 발생한다") {
                coEvery { storyRepository.findById(storyId) } returns null

                shouldThrow<StoryNotFoundException> {
                    storyUpdateAdapter.updateStatus(storyId, otherUserId, status.name)
                }.message shouldBe StoryNotFoundException().message
            }
        }

        `when`("작성자와 일치하지 않으면") {
            then("UnauthorizedStoryAccess Exception이 발생한다") {
                coEvery { storyRepository.findByIdOrNull(storyId) } returns mockStoryEntity

                shouldThrow<UnauthorizedStoryAccessException> {
                    storyUpdateAdapter.updateStatus(storyId, otherUserId, status.name)
                }.message shouldBe UnauthorizedStoryAccessException().message
            }
        }
    }

    given("스토리 정보 업데이트 요청 시") {
        val command = StoryUpdateCommand(
            title = "Updated Title",
            description = "Updated Description",
            category = Category.TRAVEL,
            startDate = LocalDate.of(2025, 5, 2),
            endDate = LocalDate.of(2025, 5, 2),
        )

        `when`("작성자와 일치하면") {
            then("스토리 정보가 업데이트된다") {
                coEvery { storyRepository.findByIdOrNull(storyId) } returns mockStoryEntity
                coEvery { storyRepository.save(capture(updatedEntitySlot)) } answers { updatedEntitySlot.captured }

                storyUpdateAdapter.update(storyId, authorId, command)

                with(updatedEntitySlot.captured) {
                    title shouldBe command.title
                    description shouldBe command.description
                    category shouldBe command.category.name
                    startDate shouldBe command.startDate
                    endDate shouldBe command.endDate
                }
            }
        }

        `when`("없는 스토리라면") {
            then("StoryNotFound Exception이 발생한다") {
                coEvery { storyRepository.findById(storyId) } returns null

                shouldThrow<StoryNotFoundException> {
                    storyUpdateAdapter.update(storyId, authorId, command)
                }.message shouldBe StoryNotFoundException().message
            }
        }

        `when`("작성자와 일치하지 않으면") {
            then("UnauthorizedStoryAccess Exception이 발생한다") {
                coEvery { storyRepository.findByIdOrNull(storyId) } returns mockStoryEntity

                shouldThrow<UnauthorizedStoryAccessException> {
                    storyUpdateAdapter.update(storyId, otherUserId, command)
                }.message shouldBe UnauthorizedStoryAccessException().message
            }
        }
    }

})