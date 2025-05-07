package com.beomsic.placeservice.application.service

import com.beomsic.placeservice.application.port.out.PlaceFindPort
import com.beomsic.placeservice.domain.Place
import com.beomsic.placeservice.domain.exception.PlaceNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class PlaceFindServiceTest: BehaviorSpec({

    val placeFindPort = mockk<PlaceFindPort>()
    val placeFindService = PlaceFindService(placeFindPort)

    afterEach {
        clearAllMocks()
    }

    given("일상 ID가 주어졌을 때") {
        val storyId = 2L
        val mockPlaces = listOf(mockk<Place>(), mockk<Place>())

        `when`("일상에 해당하는 장소들이 존재한다면") {
            coEvery { placeFindPort.findAllByStoryId(storyId) } returns mockPlaces
            then("장소 리스트를 반환한다") {
                val result = placeFindService.findAllByStoryId(storyId)
                coVerify(exactly = 1) { placeFindPort.findAllByStoryId(storyId) }
                result.size shouldBe mockPlaces.size
                result shouldBe mockPlaces
            }
        }
    }

    given("장소 ID가 주어졌을 때") {
        val placeId = 3L
        val mockPlace = mockk<Place>()

        `when`("ID에 해당하는 장소가 존재한다면") {
            coEvery { placeFindPort.findByPlaceId(placeId) } returns mockPlace
            then("장소 정보를 반환한다") {
                val result = placeFindService.findByPlaceId(placeId)
                coVerify(exactly = 1) { placeFindPort.findByPlaceId(placeId) }
                result shouldBe mockPlace
            }
        }

        `when`("ID에 해당하는 장소가 없다면") {
            coEvery { placeFindPort.findByPlaceId(999L) } throws PlaceNotFoundException()
            then("예외를 반환한다") {

                shouldThrow<PlaceNotFoundException> {
                    placeFindService.findByPlaceId(999L)
                }.message shouldBe PlaceNotFoundException().message
                coVerify(exactly = 1) { placeFindPort.findByPlaceId(999L) }
            }
        }
    }

})