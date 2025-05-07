package com.beomsic.placeservice.application.service

import com.beomsic.placeservice.adapter.out.external.service.ImageWebClient
import com.beomsic.placeservice.application.port.`in`.command.PlaceUpdateCommand
import com.beomsic.placeservice.application.port.out.PlaceFindPort
import com.beomsic.placeservice.application.port.out.PlaceUpdatePort
import com.beomsic.placeservice.domain.Category
import com.beomsic.placeservice.domain.Place
import com.beomsic.placeservice.domain.exception.ForbiddenException
import com.beomsic.placeservice.infra.EventPublisher
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class PlaceUpdateServiceTest: BehaviorSpec ({

    val placeUpdatePort = mockk<PlaceUpdatePort>()
    val placeFindPort = mockk<PlaceFindPort>()
    val imageClient = mockk<ImageWebClient>()
    val eventPublisher = mockk<EventPublisher>(relaxed = true)
    val placeUpdateService = PlaceUpdateService(placeUpdatePort, placeFindPort, imageClient, eventPublisher)

    afterEach {
        clearAllMocks()
    }

    val placeId = 1L
    val authorId = 2L
    val storyId = 2L
    val currentImageUrl = "image/com"

    fun makeMockkPlace(): Place = mockk(relaxed = true) {
        every { this@mockk.id } returns placeId
        every { this@mockk.storyId } returns storyId
        every { this@mockk.authorId } returns authorId
        every { this@mockk.imageUrl } returns currentImageUrl
    }

    val updateCommand = PlaceUpdateCommand(
        placeId = placeId,
        authorId = authorId,
        name = "New Name",
        description = "Updated Description",
        category = Category.ATTRACTION,
        latitude = 2.0,
        longitude = 2.0,
    )

    given("updateContent 호출 시") {
        val wrongAuthorId = 3L
        val existingPlace = makeMockkPlace()

        `when`("작성자 본인이 수정할 경우") {
            coEvery { placeFindPort.findByPlaceId(placeId) } returns existingPlace
            val capturedCommand = slot<PlaceUpdateCommand>()
            coEvery { placeUpdatePort.updateContent(placeId, capture(capturedCommand)) } returns existingPlace

            then("업데이트된 장소가 반환되고 인자 값이 올바르다") {
                val result = placeUpdateService.updateContent(updateCommand)

                result shouldBe existingPlace
                capturedCommand.captured.name shouldBe "New Name"
                capturedCommand.captured.description shouldBe "Updated Description"
                capturedCommand.captured.authorId shouldBe authorId
            }
        }

        `when`("작성자가 아닌 경우") {
            val invalidCommand = updateCommand.copy(authorId = wrongAuthorId)

            coEvery { placeFindPort.findByPlaceId(placeId) } returns existingPlace

            then("ForbiddenException 이 발생한다") {
                shouldThrow<ForbiddenException> {
                    placeUpdateService.updateContent(invalidCommand)
                }.message shouldBe ForbiddenException().message
            }
        }
    }

    given("updateImage 호출 시") {
        val mockFilePart = mockk<FilePart>()
        val newImageUrl = "new-image-url"
        val wrongAuthorId = 3L
        val existingPlace = makeMockkPlace()

        `when`("작성자 본인이 이미지를 수정하는 경우") {
            coEvery { placeFindPort.findByPlaceId(placeId) } returns makeMockkPlace()
            coEvery { imageClient.uploadImage(mockFilePart) } returns newImageUrl
            val imageUrlSlot = slot<String>()
            coEvery { placeUpdatePort.updateImage(placeId, capture(imageUrlSlot)) } returns existingPlace
            every { eventPublisher.publishImageRollbackEvent(any()) } just Runs

            then("이미지가 업데이트되고 이벤트가 발행된다") {
                placeUpdateService.updateImage(placeId, authorId, mockFilePart)

                imageUrlSlot.captured shouldBe newImageUrl
                verify { eventPublisher.publishImageRollbackEvent(
                    match { it.imageUrl == currentImageUrl }
                ) }
            }
        }

        `when`("작성자가 아닌 경우") {
            coEvery { placeFindPort.findByPlaceId(placeId) } returns existingPlace

            then("ForbiddenException이 발생한다") {
                shouldThrow<ForbiddenException> {
                    placeUpdateService.updateImage(placeId, wrongAuthorId, mockFilePart)
                }
            }
        }

        `when`("기존 이미지가 없는 경우") {
            val placeWithoutImage = mockk<Place>(relaxed = true) {
                every { this@mockk.id } returns placeId
                every { this@mockk.storyId } returns storyId
                every { this@mockk.authorId } returns authorId
                every { this@mockk.imageUrl } returns null
            }

            coEvery { placeFindPort.findByPlaceId(placeId) } returns placeWithoutImage
            coEvery { imageClient.uploadImage(mockFilePart) } returns newImageUrl
            coEvery { placeUpdatePort.updateImage(placeId, newImageUrl) } returns existingPlace

            then("이미지 롤백 이벤트는 발행되지 않는다") {
                placeUpdateService.updateImage(placeId, authorId, mockFilePart)

                verify(exactly = 0) { eventPublisher.publishImageRollbackEvent(any()) }
            }
        }
    }
})