package com.beomsic.storyservice.application.service

import com.beomsic.storyservice.adapter.out.service.UserResponse
import com.beomsic.storyservice.adapter.out.service.UserWebClient
import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.application.port.out.StoryCreatePort
import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.Story
import com.beomsic.storyservice.domain.model.StoryStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("test")
class StoryCreateServiceTest : FunSpec({

    val storyCreatePort = mockk<StoryCreatePort>()
    val userWebClient = mockk<UserWebClient>()
    val storyCreateService = StoryCreateService(storyCreatePort, userWebClient)

    beforeTest {
        clearAllMocks()
    }

    context("일상 기록 생성") {

        test("정상적으로 생성되는 경우") {
            // given
            val authorId = 1L
            val command = StoryCreateCommand(
                authorId = authorId,
                title = "new Story",
                description = "daily record",
                category = Category.DAILY,
                startDate = LocalDate.of(2025, 4, 1),
                endDate = LocalDate.of(2025, 4, 1)
            )
            val story = Story(
                id = 2L,
                authorId = authorId,
                title = "new Story",
                description = "daily record",
                category = Category.DAILY,
                status = StoryStatus.ARCHIVED,
                startDate = command.startDate,
                endDate = command.endDate,
                createdAt = LocalDateTime.of(2025, 4, 1, 22, 0),
                updatedAt = LocalDateTime.of(2025, 4, 1, 22, 0)
            )
            val userResponse = UserResponse(userId = authorId)

            coEvery { userWebClient.findById(authorId) } returns userResponse
            coEvery { storyCreatePort.create(command) } returns story

            // when
            val result = storyCreateService.execute(command)

            // then
            result shouldBe story
            coVerify(exactly = 1) { userWebClient.findById(authorId) }
            coVerify(exactly = 1) { storyCreatePort.create(command) }
        }

        test("사용자 정보 조회에 실패할 경우 예외가 발생한다") {
            // given
            val authorId = 1L
            val command = StoryCreateCommand(
                authorId = authorId,
                title = "new Story",
                description = "daily record",
                category = Category.DAILY,
                startDate = LocalDate.of(2025, 4, 1),
                endDate = LocalDate.of(2025, 4, 1)
            )

            coEvery { userWebClient.findById(authorId) } throws RuntimeException("유저 정보 조회 실패")

            // when & then
            shouldThrow<RuntimeException> {
                storyCreateService.execute(command)
            }.message shouldBe "유저 정보 조회 실패"

            coVerify(exactly = 1) { userWebClient.findById(authorId) }
            coVerify(exactly = 0) { storyCreatePort.create(any()) }
        }
    }
})
