package com.beomsic.storyservice.application.service

import com.beomsic.storyservice.application.port.`in`.command.StoryUpdateCommand
import com.beomsic.storyservice.application.port.out.StoryUpdatePort
import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import com.beomsic.storyservice.domain.exception.UnauthorizedStoryAccessException
import com.beomsic.storyservice.domain.model.Category
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@ActiveProfiles("test")
class StoryUpdateServiceTest : BehaviorSpec({

    val storyUpdatePort = mockk<StoryUpdatePort>()
    val storyUpdateService = StoryUpdateService(storyUpdatePort)

    val storyId = 1L
    val userId = 100L

    afterEach {
        clearAllMocks()
    }

    given("상태 업데이트 시") {
        val status = "ARCHIVED"
        `when`("정상적으로 동작하면") {
            then("원하는 상태로 업데이트 된다.") {
                coEvery { storyUpdatePort.updateStatus(any(), any(), any()) } just Runs

                storyUpdateService.updateStatus(storyId, userId, status)

                coVerify(exactly = 1) { storyUpdatePort.updateStatus(storyId, userId, status) }
            }
        }

        `when`("not found 예외가 발생하면") {
            then("예외가 그대로 전파된다") {
                coEvery { storyUpdatePort.updateStatus(any(), any(), any()) } throws StoryNotFoundException()

                shouldThrow<StoryNotFoundException> {
                    storyUpdateService.updateStatus(storyId, userId, status)
                }.message shouldBe StoryNotFoundException().message

                coVerify(exactly = 1) { storyUpdatePort.updateStatus(storyId, userId, status) }
            }
        }

        `when`("권한 관련 예외가 발생하면") {
            then("예외가 그대로 전파된다") {
                coEvery { storyUpdatePort.updateStatus(any(), any(), any()) } throws UnauthorizedStoryAccessException()

                shouldThrow<UnauthorizedStoryAccessException> {
                    storyUpdateService.updateStatus(storyId, userId, status)
                }.message shouldBe UnauthorizedStoryAccessException().message

                coVerify(exactly = 1) { storyUpdatePort.updateStatus(storyId, userId, status) }
            }
        }
    }

    given("스토리 update 호출 시") {
        val command = StoryUpdateCommand(
            title = "New Title",
            description = "Updated Content",
            category = Category.ACTIVITY,
            startDate = LocalDate.of(2025, 5, 1),
            endDate = LocalDate.of(2025, 5, 1)
        )

        `when`("정상적으로 동작하면") {
            then("업데이트가 정상적으로 처리된다") {
                coEvery { storyUpdatePort.update(any(), any(), any()) } just Runs

                storyUpdateService.update(storyId, userId, command)

                coVerify(exactly = 1) { storyUpdatePort.update(storyId, userId, command) }
            }
        }

        `when`("not found 예외가 발생하면") {
            then("예외가 그대로 전파된다") {
                coEvery { storyUpdatePort.update(any(), any(), any()) } throws StoryNotFoundException()

                shouldThrow<StoryNotFoundException> {
                    storyUpdateService.update(storyId, userId, command)
                }.message shouldBe StoryNotFoundException().message

                coVerify(exactly = 1) { storyUpdatePort.update(storyId, userId, command) }
            }
        }

        `when`("권한 관련 예외가 발생하면") {
            then("예외가 그대로 전파된다") {
                coEvery { storyUpdatePort.update(any(), any(), any()) } throws UnauthorizedStoryAccessException()

                shouldThrow<UnauthorizedStoryAccessException> {
                    storyUpdateService.update(storyId, userId, command)
                }.message shouldBe UnauthorizedStoryAccessException().message

                coVerify(exactly = 1) { storyUpdatePort.update(storyId, userId, command) }
            }
        }
    }

})