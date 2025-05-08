package com.beomsic.placeservice.adapter.out.persistence

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class PlaceDeleteAdapterTest: BehaviorSpec({

    val placeRepository = mockk<PlaceRepository>()
    val placeDeleteAdapter = PlaceDeleteAdapter(placeRepository)

    afterEach { clearAllMocks() }

    given("placeId를 통한 장소 삭제 메소드가 호출될 때") {
        val placeId = 1L

        `when`("정상적으로 삭제가 완료되면") {
            coEvery { placeRepository.deleteById(placeId) } just Runs

            then("예외가 발생하지 않고 삭제가 실행된다.") {
                shouldNotThrow<Exception> {
                    placeDeleteAdapter.deleteById(placeId)
                }
                coVerify(exactly = 1) { placeRepository.deleteById(placeId) }
            }
        }

        `when`("삭제 중 예외가 발생하면") {
            coEvery { placeRepository.deleteById(placeId) } throws  RuntimeException()

            then("예외가 발생 한다") {
                shouldThrow<RuntimeException> {
                    placeDeleteAdapter.deleteById(placeId)
                }
                coVerify(exactly = 1) { placeRepository.deleteById(placeId) }
            }
        }
    }

    given("storyId를 통해 story에 있는 모든 장소를 삭제하는 메소드가 호출되면") {
        val storyId = 1L

        `when`("정상적으로 삭제가 완료되면") {
            coEvery { placeRepository.deleteAllByStoryId(storyId) } just Runs

            then("예외가 발생하지 않고 삭제되어야 한다.") {
                shouldNotThrow<Exception> {
                    placeDeleteAdapter.deleteAllByStoryId(storyId)
                }

                coVerify(exactly = 1) { placeRepository.deleteAllByStoryId(storyId) }
            }
        }

        `when`("삭제 중 예외가 발생하면") {
            coEvery { placeRepository.deleteAllByStoryId(storyId) } throws RuntimeException()

            then("예외가 전파되어야 한다") {
               shouldThrow<RuntimeException> {
                    placeDeleteAdapter.deleteAllByStoryId(storyId)
                }
                coVerify(exactly = 1) { placeRepository.deleteAllByStoryId(storyId) }
            }
        }
    }

})