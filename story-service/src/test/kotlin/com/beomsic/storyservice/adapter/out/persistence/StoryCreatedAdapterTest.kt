package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.StoryStatus
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("test")
class StoryCreateAdapterTest : BehaviorSpec({

    val storyRepository = mockk<StoryRepository>()
    val storyCreateAdapter = StoryCreateAdapter(storyRepository)

    given("스토리를 생성할 때") {
        val command = StoryCreateCommand(
            authorId = 1L,
            title = "여행기록",
            description = "제주도 여행",
            category = Category.TRAVEL,
            startDate = LocalDate.of(2024, 4, 1),
            endDate = LocalDate.of(2024, 4, 5),
        )

        val savedEntity = StoryEntity(
            id = 1L,
            authorId = command.authorId,
            title = command.title,
            description = command.description,
            category = command.category.name,
            status = StoryStatus.DRAFT.name,
            startDate = command.startDate,
            endDate = command.endDate,
            createdAt = LocalDateTime.of(2024, 4, 5, 22, 0, 0, 0),
            updatedAt = LocalDateTime.of(2024, 4, 5, 22, 0, 0, 0)
        )

        `when`("정상적으로 저장된다면") {
            coEvery { storyRepository.save(any()) } returns savedEntity

            val result = storyCreateAdapter.create(command)

            then("스토리가 저장되고 반환된다") {
                result.id shouldBe 1L
                result.title shouldBe command.title
                result.status shouldBe StoryStatus.DRAFT
                coVerify(exactly = 1) { storyRepository.save(any()) }
            }
        }
    }
})
