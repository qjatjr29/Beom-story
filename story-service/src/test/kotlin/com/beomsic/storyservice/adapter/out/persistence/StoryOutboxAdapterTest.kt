package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.common.infra.kafka.story.StoryOutboxPayload
import com.beomsic.common.infra.kafka.story.StoryOutboxType
import com.beomsic.storyservice.adapter.out.persistence.outbox.StoryOutboxAdapter
import com.beomsic.storyservice.adapter.out.persistence.outbox.StoryOutboxRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class StoryOutboxAdapterTest : BehaviorSpec({

    val storyOutboxRepository = mockk<StoryOutboxRepository>()
    val storyOutboxAdapter = StoryOutboxAdapter(storyOutboxRepository)

    val storyId = 1L

    afterEach {
        clearAllMocks()
    }

    given("스토리 삭제 outbox 메시지를 저장할 때") {

        `when`("정상적으로 동작하면") {
            coEvery { storyOutboxRepository.save(any()) } answers { firstArg() }

            then("직렬화된 메시지가 저장된다") {
                storyOutboxAdapter.saveStoryDeleteMessage(storyId)

                val payload = Json.encodeToString(
                    StoryOutboxPayload(type = StoryOutboxType.STORY_DELETED, storyId = storyId)
                )

                coVerify {
                    storyOutboxRepository.save(
                        withArg {
                            it.storyId shouldBe storyId
                            it.payload shouldBe payload
                        }
                    )
                }
            }
        }
    }
})
