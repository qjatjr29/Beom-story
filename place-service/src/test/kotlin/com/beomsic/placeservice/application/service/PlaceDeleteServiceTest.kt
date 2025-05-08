package com.beomsic.placeservice.application.service


import com.beomsic.placeservice.application.port.out.ImageRollbackOutboxPort
import com.beomsic.placeservice.application.port.out.PlaceDeletePort
import com.beomsic.placeservice.application.port.out.PlaceFindPort
import com.beomsic.placeservice.domain.Category
import com.beomsic.placeservice.domain.Place
import com.beomsic.placeservice.domain.exception.PlaceNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("test")
class PlaceDeleteServiceTest: BehaviorSpec({
    val placeDeletePort = mockk<PlaceDeletePort>()
    val placeFindPort = mockk<PlaceFindPort>()
    val imageRollbackOutboxPort = mockk<ImageRollbackOutboxPort>()

    val placeDeleteService = PlaceDeleteService(
        placeDeletePort = placeDeletePort,
        placeFindPort = placeFindPort,
        imageRollbackOutboxPort = imageRollbackOutboxPort
    )

    val placeId = 1L
    val storyId = 2L
    val authorId = 3L
    val currentImageUrl = "image/image.jpg"

    fun makeMockPlace(placeId: Long = 1L, storyId: Long = 2L, authorId: Long = 3L, imageUrl: String? = null): Place {
        return mockk<Place>(relaxed = true) {
            every { this@mockk.id } returns placeId
            every { this@mockk.storyId } returns storyId
            every { this@mockk.authorId } returns authorId
            every { this@mockk.name } returns "place name"
            every { this@mockk.description } returns "place description"
            every { this@mockk.imageUrl } returns imageUrl
            every { this@mockk.category } returns Category.ATTRACTION
            every { this@mockk.latitude } returns 2.0
            every { this@mockk.longitude } returns 2.0
            every { this@mockk.address } returns "place address"
            every { this@mockk.visitedDate } returns LocalDate.of(2025, 5, 1)
            every { this@mockk.createdAt } returns LocalDateTime.of(2025, 5, 2, 11, 11, 11)
            every { this@mockk.updatedAt } returns LocalDateTime.of(2025, 5, 2, 11, 11, 11)
        }
    }

    afterEach {
        clearAllMocks()
    }

    given("장소 ID를 통한 장소 삭제 시") {

        val mockPlace = makeMockPlace(imageUrl = currentImageUrl)

        `when`("이미지가 존재하는 장소인 경우") {

            coEvery { placeFindPort.findByPlaceId(placeId) } returns mockPlace
            coEvery { placeDeletePort.deleteById(placeId) } just Runs
            coEvery { imageRollbackOutboxPort.saveImageRollbackMessage(any(), any()) } just Runs

            then("deletePlace가 호출되고 이미지 rollback 메시지가 저장된다") {

                placeDeleteService.deleteById(placeId, authorId)

                coVerify { placeDeletePort.deleteById(placeId) }
                coVerify {
                    imageRollbackOutboxPort.saveImageRollbackMessage(
                        placeId = placeId,
                        imageUrl = currentImageUrl
                    )
                }
            }
        }

        `when`("이미지가 없는 장소 삭제") {
            val place : Place = makeMockPlace()
            coEvery { placeFindPort.findByPlaceId(placeId) } returns place
            coEvery { placeDeletePort.deleteById(placeId) } just Runs

            then("deletePlace는 호출되지만 rollback 메시지는 저장되지 않는다") {
                placeDeleteService.deleteById(placeId, authorId)
                coVerify { placeDeletePort.deleteById(placeId) }
                coVerify(exactly = 0) { imageRollbackOutboxPort.saveImageRollbackMessage(any(), any()) }
            }
        }

        `when`("없는 장소를 삭제하려는 경우") {

            coEvery { placeFindPort.findByPlaceId(placeId) } throws PlaceNotFoundException()

            then("404 예외가 반환된다.") {
                shouldThrow<PlaceNotFoundException> {
                    placeDeleteService.deleteById(placeId, authorId)
                }.message shouldBe PlaceNotFoundException().message

                coVerify { placeFindPort.findByPlaceId(placeId) }
                coVerify(exactly = 0) { placeDeletePort.deleteById(placeId) }
                coVerify(exactly = 0) { imageRollbackOutboxPort.saveImageRollbackMessage(any(), any()) }
            }
        }

        `when`("장소 삭제과정에서 예외(에러)가 발생하는 경우") {
            val place : Place = makeMockPlace()
            coEvery { placeFindPort.findByPlaceId(placeId) } returns place
            coEvery { placeDeletePort.deleteById(placeId) } throws RuntimeException()

            then("해당 예외가 반환된다.") {
                shouldThrow<RuntimeException> {
                    placeDeleteService.deleteById(placeId, authorId)
                }
                coVerify(exactly = 1) { placeFindPort.findByPlaceId(placeId) }
                coVerify(exactly = 1) { placeDeletePort.deleteById(placeId) }
                coVerify(exactly = 0) { imageRollbackOutboxPort.saveImageRollbackMessage(any(), any()) }
            }
        }
    }

    given("스토리 ID를 통한 스토리에 있는 모든 장소를 삭제하는 경우") {

        val imageUrl1 = "http://image.com/1.jpg"
        val imageUrl3 = "http://image.com/3.jpg"
        val mockPlaces = listOf(
            makeMockPlace(placeId = 1L, imageUrl = imageUrl1),
            makeMockPlace(placeId = 2L),
            makeMockPlace(placeId = 3L, imageUrl = imageUrl3)
        )

        `when`("여러 장소 중 일부에만 이미지가 존재하면") {

            coEvery { placeFindPort.findAllByStoryId(storyId) } returns mockPlaces
            coEvery { placeDeletePort.deleteAllByStoryId(storyId) } just Runs
            coEvery { imageRollbackOutboxPort.saveImageRollbackMessage(any(), any()) } just Runs

            then("삭제 메소드 호출 이후 이미지가 있는 장소에 대해서만 rollback 메시지 저장") {

                placeDeleteService.deleteAllByStoryId(storyId)

                coVerify { placeDeletePort.deleteAllByStoryId(storyId) }

                coVerify {
                    imageRollbackOutboxPort.saveImageRollbackMessage(1L, imageUrl1)
                    imageRollbackOutboxPort.saveImageRollbackMessage(3L, imageUrl3)
                }

                coVerify(exactly = 0) {
                    imageRollbackOutboxPort.saveImageRollbackMessage(2L, any())
                }
            }
        }

        `when`("모든 장소가 imageUrl이 없는 경우") {

            val mockNonImagePlaces = listOf(
                makeMockPlace(placeId = 1L),
                makeMockPlace(placeId = 2L),
                makeMockPlace(placeId = 3L)
            )

            coEvery { placeFindPort.findAllByStoryId(storyId) } returns mockNonImagePlaces
            coEvery { placeDeletePort.deleteAllByStoryId(storyId) } just Runs

            then("rollback 메시지는 저장되지 않는다") {
                placeDeleteService.deleteAllByStoryId(storyId)
                coVerify { placeDeletePort.deleteAllByStoryId(storyId) }
                coVerify(exactly = 0) { imageRollbackOutboxPort.saveImageRollbackMessage(any(), any()) }
            }
        }
    }

})