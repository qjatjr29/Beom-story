package com.beomsic.storyservice.application.service

import com.beomsic.storyservice.application.port.out.StoryDeletePort
import com.beomsic.storyservice.application.port.out.StoryOutboxPort
import com.beomsic.storyservice.domain.exception.StoryNotFoundException
import com.beomsic.storyservice.domain.exception.UnauthorizedStoryAccessException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class StoryDeleteServiceTest: BehaviorSpec ({

    val storyDeletePort = mockk<StoryDeletePort>()
    val storyOutboxPort = mockk<StoryOutboxPort>()
    val storyDeleteService = StoryDeleteService(storyDeletePort, storyOutboxPort)

    val storyId = 1L
    val userId = 123L

    afterEach {
        clearAllMocks()
    }

    given("스토리 삭제 요청이 들어왔을 때") {

        `when`("정상적인 요청이라면") {
            coEvery { storyDeletePort.deleteStory(userId, storyId) } just Runs
            coEvery { storyOutboxPort.saveStoryDeleteMessage(storyId) } just Runs

            then("스토리가 삭제되고 삭제 outbox 메시지가 저장된다") {
                storyDeleteService.execute(userId, storyId)

                coVerifyOrder {
                    storyDeletePort.deleteStory(userId, storyId)
                    storyOutboxPort.saveStoryDeleteMessage(storyId)
                }
            }
        }

        `when`("작성자가 요청하지 않은 경우") {
            coEvery { storyDeletePort.deleteStory(userId, storyId) } throws UnauthorizedStoryAccessException()
            coEvery { storyOutboxPort.saveStoryDeleteMessage(any()) } just Runs

            then("예외가 발생하고 삭제 메시지는 저장되지 않는다") {
                shouldThrow<UnauthorizedStoryAccessException> {
                    storyDeleteService.execute(userId, storyId)
                }.message shouldBe UnauthorizedStoryAccessException().message

                coVerify(exactly = 1) { storyDeletePort.deleteStory(userId, storyId) }
                coVerify(exactly = 0) { storyOutboxPort.saveStoryDeleteMessage(any()) }
            }
        }

        `when`("스토리를 찾을 수 없다면") {
            coEvery { storyDeletePort.deleteStory(userId, storyId) } throws StoryNotFoundException()

            then("예외가 발생하고 삭제 메시지는 저장되지 않는다") {
                shouldThrow<StoryNotFoundException> {
                    storyDeleteService.execute(userId, storyId)
                }.message shouldBe StoryNotFoundException().message

                coVerify(exactly = 0) { storyOutboxPort.saveStoryDeleteMessage(any()) }
            }
        }
    }
})