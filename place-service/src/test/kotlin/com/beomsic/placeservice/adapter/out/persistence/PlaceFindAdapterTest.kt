package com.beomsic.placeservice.adapter.out.persistence

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
class PlaceFindAdapterTest: BehaviorSpec({

    val placeRepository = mockk<PlaceRepository>()
    val placeFindAdapter = PlaceFindAdapter(placeRepository)

    afterEach {
        clearAllMocks()
    }

    fun createMockPlaceEntity(mockPlace: Place): PlaceEntity {
        return mockk {
            coEvery { toDomain() } returns mockPlace
        }
    }

    given("일상 ID가 주어졌을 때") {
        val storyId = 2L

        val mockPlace1 = mockk<Place>()
        val mockPlace2 = mockk<Place>()

        val mockPlaceEntities = listOf(
            createMockPlaceEntity(mockPlace1),
            createMockPlaceEntity(mockPlace2)
        )

        val expectedPlaces = listOf(mockPlace1, mockPlace2)

        `when`("일상에 해당하는 장소 엔티티들이 있다면") {
            coEvery { placeRepository.findAllByStoryId(storyId) } returns mockPlaceEntities
            then("장소 도메인 리스트를 반환") {
                val result = placeFindAdapter.findAllByStoryId(storyId)
                coVerify(exactly = 1) { placeRepository.findAllByStoryId(storyId) }
                result shouldBe expectedPlaces
                result.size shouldBe mockPlaceEntities.size
            }
        }
    }

    given("장소 ID가 주어졌을 때") {
        val placeId = 2L
        val mockPlace = mockk<Place>()

        val mockPlaceEntity =  createMockPlaceEntity(mockPlace)

        `when`("ID에 해당하는 장소 엔티티가 존재한다면") {
            coEvery { placeRepository.findByIdOrNull(placeId) } returns mockPlaceEntity
            then("장소 도메인을 반환") {
                val result = placeFindAdapter.findByPlaceId(placeId)
                coVerify(exactly = 1) { placeRepository.findById(placeId) }
                coVerify(exactly = 1) { placeRepository.findByIdOrNull(placeId) }
                result shouldBe mockPlace
            }
        }

        `when`("ID에 해당하는 장소 엔티티가 존재하지 않는다면") {
            coEvery { placeRepository.findByIdOrNull(placeId) } throws PlaceNotFoundException()
            then("장소 도메인을 반환") {

                shouldThrow<PlaceNotFoundException> {
                    placeFindAdapter.findByPlaceId(placeId)
                }.message shouldBe PlaceNotFoundException().message

                coVerify(exactly = 1) { placeRepository.findById(placeId) }
                coVerify(exactly = 1) { placeRepository.findByIdOrNull(placeId) }

            }
        }
    }
})