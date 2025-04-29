package com.beomsic.storyservice.application.service

import com.beomsic.storyservice.adapter.out.service.UserResponse
import com.beomsic.storyservice.adapter.out.service.UserWebClient
import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.application.port.out.StoryCreatePort
import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.Story
import com.beomsic.storyservice.domain.model.StoryStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("test")
class StoryCreateServiceTest : BehaviorSpec({

    val storyCreatePort = mockk<StoryCreatePort>()
    val userWebClient = mockk<UserWebClient>()
    val storyCreateService = StoryCreateService(storyCreatePort, userWebClient)

    afterTest {
        clearAllMocks()
    }

    given("일상 기록 생성을 요청할 때") {

        val authorId = 1L
        val command = StoryCreateCommand(
            authorId = authorId,
            title = "new Story",
            description = "daily record",
            category = Category.DAILY,
            startDate = LocalDate.of(2025, 4, 1),
            endDate = LocalDate.of(2025, 4, 1)
        )

        `when`("정상적으로 생성되는 경우") {
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

            coEvery { userWebClient.findById(authorId) } returns UserResponse(userId = authorId)
            coEvery { storyCreatePort.create(command) } returns story

            then("스토리가 정상적으로 생성되어야 한다") {
                val result = storyCreateService.execute(command)

                result shouldBe story
                coVerify(exactly = 1) { userWebClient.findById(authorId) }
                coVerify(exactly = 1) { storyCreatePort.create(command) }
            }
        }

        `when`("사용자 정보 조회에 실패하는 경우") {

            coEvery { userWebClient.findById(authorId) } throws RuntimeException("유저 정보 조회 실패")

            then("예외가 발생해야 한다") {
                shouldThrow<RuntimeException> {
                    storyCreateService.execute(command)
                }.message shouldBe "유저 정보 조회 실패"

                coVerify(exactly = 1) { userWebClient.findById(authorId) }
                coVerify(exactly = 0) { storyCreatePort.create(any()) }
            }
        }
    }

})
