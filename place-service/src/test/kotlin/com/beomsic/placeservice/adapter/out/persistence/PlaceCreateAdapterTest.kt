package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.placeservice.domain.Category
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
class PlaceCreateAdapterTest: BehaviorSpec ({

    val placeRepository = mockk<PlaceRepository>()
    val placeCreateAdapter = PlaceCreateAdapter(placeRepository)

    afterEach {
        clearAllMocks()
    }

    given("장소 엔티티 생성시") {
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

        val savedEntity = PlaceEntity(
            id = 1L,
            storyId = command.storyId,
            authorId = command.authorId,
            name = command.name,
            description = command.description,
            category = command.category.name,
            imageUrl = command.imageUrl,
            latitude = command.latitude,
            longitude = command.longitude,
            address = command.address,
            visitedDate = command.visitedDate,
            createdAt = LocalDateTime.of(2024, 4, 5, 22, 0, 0, 0),
            updatedAt = LocalDateTime.of(2024, 4, 5, 22, 0, 0, 0)
        )

        `when`("정상 동작시") {

            coEvery { placeRepository.save(any()) } returns savedEntity

            val result = placeCreateAdapter.create(command)


            then("엔티티가 저장된 후 Place 도메인을 반환한다.") {
                result.id shouldBe 1L
                result.name shouldBe command.name
                result.description shouldBe command.description
                coVerify(exactly = 1) { placeRepository.save(any()) }
            }
        }
    }
})