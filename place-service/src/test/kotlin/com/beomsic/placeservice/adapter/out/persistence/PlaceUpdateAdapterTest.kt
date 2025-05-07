package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.application.port.`in`.command.PlaceUpdateCommand
import com.beomsic.placeservice.domain.Category
import com.beomsic.placeservice.domain.exception.PlaceNotFoundException
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
class PlaceUpdateAdapterTest: BehaviorSpec({

    val placeRepository = mockk<PlaceRepository>()
    val placeUpdateAdapter = PlaceUpdateAdapter(placeRepository)

    val placeId = 1L
    val storyId = 2L
    val authorId = 2L

    afterEach { clearAllMocks() }

    given("장소의 내용을 업데이트할 때") {


        val updateCommand = PlaceUpdateCommand(
            placeId = placeId,
            authorId = authorId,
            name = "업데이트된 장소",
            description = "새로운 설명",
            category = Category.CAFE,
            latitude = 23.0,
            longitude = 7.0
        )

        val placeEntity = PlaceEntity(
            id = placeId,
            storyId = storyId,
            authorId = authorId,
            name = "원래 장소",
            description = "원래 설명",
            category = "RESTAURANT",
            latitude = 37.5,
            longitude = 127.0,
            address = "서울시 강남구",
            imageUrl = "http://example.com/image.jpg",
            visitedDate = LocalDate.of(2025, 5, 7),
            createdAt = LocalDateTime.of(2025, 5, 7, 0, 0),
            updatedAt = LocalDateTime.of(2025, 5, 7, 0, 0),
        )

        val updatedEntity = placeEntity.copy(
            name = updateCommand.name,
            description = updateCommand.description,
            category = updateCommand.category.name,
            latitude = updateCommand.latitude,
            longitude = updateCommand.longitude,
        )

        `when`("장소가 존재하고 업데이트가 성공하면") {
            coEvery { placeRepository.findByIdOrNull(placeId) } returns placeEntity
            coEvery { placeRepository.save(any()) } returns updatedEntity
//            every { updatedEntity.toDomain() } returns expectedDomain

            then("업데이트된 장소 정보를 반환한다") {
                val result = placeUpdateAdapter.updateContent(placeId, updateCommand)

                result.name shouldBe updateCommand.name
                result.description shouldBe updateCommand.description
                result.category shouldBe updateCommand.category
                result.latitude shouldBe updateCommand.latitude
                result.longitude shouldBe updateCommand.longitude
                coVerify(exactly = 1) { placeRepository.findByIdOrNull(placeId) }
                coVerify(exactly = 1) { placeRepository.save(any()) }
            }
        }

        `when`("장소가 존재하지 않으면") {
            coEvery { placeRepository.findById(placeId) } returns null

            then("PlaceNotFoundException이 발생한다") {
                shouldThrow<PlaceNotFoundException> {
                    placeUpdateAdapter.updateContent(placeId, updateCommand)
                }.message shouldBe PlaceNotFoundException().message

                coVerify(exactly = 1) { placeRepository.findByIdOrNull(placeId) }
                coVerify(exactly = 0) { placeRepository.save(any()) }
            }
        }

        `when`("업데이트 중 예외가 발생하면") {
            coEvery { placeRepository.findByIdOrNull(placeId) } returns placeEntity
            coEvery { placeRepository.save(any()) } throws RuntimeException()

            then("예외가 전파되어야 한다") {
                val thrownException = shouldThrow<RuntimeException> {
                    placeUpdateAdapter.updateContent(placeId, updateCommand)
                }

                coVerify(exactly = 1) { placeRepository.findByIdOrNull(placeId) }
                coVerify(exactly = 1) { placeRepository.save(any()) }
            }
        }
    }

    given("PlaceUpdateAdapter의 updateImage 메소드가 호출되면") {

        val newImageUrl = "http://example.com/new-image.jpg"

        val placeEntity = PlaceEntity(
            id = placeId,
            storyId = storyId,
            authorId = authorId,
            name = "원래 장소",
            description = "원래 설명",
            category = "RESTAURANT",
            latitude = 37.5,
            longitude = 127.0,
            address = "서울시 강남구",
            imageUrl = "http://example.com/image.jpg",
            visitedDate = LocalDate.of(2025, 5, 7),
            createdAt = LocalDateTime.of(2025, 5, 7, 0, 0),
            updatedAt = LocalDateTime.of(2025, 5, 7, 0, 0),
        )

        val updatedEntity = placeEntity.copy(
            imageUrl = newImageUrl,
        )

        `when`("장소가 존재하고 이미지 업데이트가 성공하면") {
            coEvery { placeRepository.findByIdOrNull(placeId) } returns placeEntity
            coEvery { placeRepository.save(any()) } returns updatedEntity

            then("업데이트된 장소 정보를 반환한다") {
                val result = placeUpdateAdapter.updateImage(placeId, newImageUrl)

                result.imageUrl shouldBe newImageUrl
                coVerify(exactly = 1) { placeRepository.findByIdOrNull(placeId) }
                coVerify(exactly = 1) { placeRepository.save(any()) }
            }
        }

        `when`("장소가 존재하지 않으면") {
            coEvery { placeRepository.findById(placeId) } returns null

            then("PlaceNotFoundException이 발생한다") {
                shouldThrow<PlaceNotFoundException> {
                    placeUpdateAdapter.updateImage(placeId, newImageUrl)
                }.message shouldBe PlaceNotFoundException().message

                coVerify(exactly = 1) { placeRepository.findByIdOrNull(placeId) }
                coVerify(exactly = 0) { placeRepository.save(any()) }
            }
        }

        `when`("이미지 업데이트 중 예외가 발생하면") {
            coEvery { placeRepository.findByIdOrNull(placeId) } returns placeEntity
            coEvery { placeRepository.save(any()) } throws  RuntimeException()

            then("예외가 전파되어야 한다") {
                shouldThrow<RuntimeException> {
                    placeUpdateAdapter.updateImage(placeId, newImageUrl)
                }

                coVerify(exactly = 1) { placeRepository.findByIdOrNull(placeId) }
                coVerify(exactly = 1) { placeRepository.save(any()) }
            }
        }
    }
})