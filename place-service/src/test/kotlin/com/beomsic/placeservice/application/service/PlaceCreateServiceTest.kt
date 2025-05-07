package com.beomsic.placeservice.application.service

import com.beomsic.placeservice.adapter.out.external.service.ImageWebClient
import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.placeservice.application.port.out.PlaceCreatePort
import com.beomsic.placeservice.domain.Category
import com.beomsic.placeservice.domain.Place
import com.beomsic.placeservice.infra.EventPublisher
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@ActiveProfiles("test")
class PlaceCreateServiceTest: BehaviorSpec({

    val placeCreatePort = mockk<PlaceCreatePort>()
    val imageWebClient = mockk<ImageWebClient>()
    val eventPublisher = mockk<EventPublisher>()

    val placeCreateService = PlaceCreateService(placeCreatePort, imageWebClient, eventPublisher)

    afterEach {
        clearAllMocks()
    }

    given("장소 생성 요청시 ") {
        val storyId = 2L
        val authorId = 1L

        val command = PlaceCreateCommand(
            storyId = storyId,
            authorId = authorId,
            name = "new Place",
            description = "place record",
            category = Category.RESTAURANT,
            latitude = 1.0,
            longitude = 2.0,
            address = "new place address",
            visitedDate = LocalDate.of(2025, 5, 4)
        )

        val image = mockk<FilePart>()

        `when`("이미지 없이 생성 요청을 했을 때") {
            then("이미지 업로드 로직을 실행하지 않고 장소 정보가 저장된다.") {
                val expectedPlace = mockk<Place>()
                coEvery { placeCreatePort.create(any()) } returns expectedPlace

                val result = placeCreateService.execute(command, null)

                result shouldBe expectedPlace
                coVerify(exactly = 0) { imageWebClient.uploadImage(any()) }
                coVerify(exactly = 1) { placeCreatePort.create(any()) }
                coVerify(exactly = 0) { eventPublisher.publishImageRollbackEvent(any()) }
            }
        }

        `when`("이미지가 포함된 요청을 했을 때") {
            then("이미지를 업로드하고 장소 정보가 저장된다.") {
                val uploadedUrl = "uploaded.com/image.png"
                val expectedPlace = mockk<Place>()

                coEvery { imageWebClient.uploadImage(any()) } returns uploadedUrl
                coEvery { placeCreatePort.create(any()) } returns expectedPlace

                val result = placeCreateService.execute(command, image)

                result shouldBe expectedPlace
                command.imageUrl shouldBe uploadedUrl

                coVerify(exactly = 1) { imageWebClient.uploadImage(image) }
                coVerify(exactly = 1) { placeCreatePort.create(any()) }
                coVerify(exactly = 0) { eventPublisher.publishImageRollbackEvent(any()) }
            }
        }

        `when`("이미지 업로드 중 예외가 발생했을 때") {
            then("장소 생성이 되지 않으며 롤백 이벤트도 발생하지 않는다.") {
                coEvery { imageWebClient.uploadImage(image) } throws RuntimeException("Upload failed")

                shouldThrow<RuntimeException> {
                    placeCreateService.execute(command, image)
                }

                coVerify(exactly = 1) { imageWebClient.uploadImage(image) }
                coVerify(exactly = 0) { placeCreatePort.create(any()) }
                coVerify(exactly = 0) { eventPublisher.publishImageRollbackEvent(any()) }
            }
        }

        `when`("장소 정보 저장 중 예외가 발생했을 때") {
            then("이미지 롤백 이벤트가 발생한다.") {
                val uploadedUrl = "uploaded.com/image.png"
                coEvery { imageWebClient.uploadImage(any()) } returns uploadedUrl
                coEvery { placeCreatePort.create(any()) } throws RuntimeException()

                shouldThrow<RuntimeException> {
                    placeCreateService.execute(command, image)
                }

                coVerify(exactly = 1) { imageWebClient.uploadImage(image) }
                coVerify(exactly = 1) { placeCreatePort.create(any()) }
                coVerify(exactly = 1) {
                    eventPublisher.publishImageRollbackEvent(match {
                        it.imageUrl == uploadedUrl
                    })
                }
            }
        }

        `when`("이미지 없이 장소 정보 저장 중 예외가 발생했을 때") {
            then("이미지 롤백 이벤트가 발생하지 않는다.") {

                coEvery { placeCreatePort.create(any()) } throws RuntimeException()

                shouldThrow<RuntimeException> {
                    placeCreateService.execute(command, null)
                }

                coVerify(exactly = 0) { imageWebClient.uploadImage(any()) }
                coVerify(exactly = 1) { placeCreatePort.create(any()) }
                coVerify(exactly = 0) { eventPublisher.publishImageRollbackEvent(any()) }
            }
        }
    }

})