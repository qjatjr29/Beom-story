package com.beomsic.placeservice.application.service

import com.beomsic.common.infra.kafka.event.ImageRollbackEvent
import com.beomsic.placeservice.adapter.out.external.service.ImageWebClient
import com.beomsic.placeservice.application.port.`in`.command.PlaceUpdateCommand
import com.beomsic.placeservice.application.port.`in`.usecase.PlaceUpdateUseCase
import com.beomsic.placeservice.application.port.out.PlaceFindPort
import com.beomsic.placeservice.application.port.out.PlaceUpdatePort
import com.beomsic.placeservice.domain.Place
import com.beomsic.placeservice.domain.exception.UnauthorizedPlaceAccessException
import com.beomsic.placeservice.infra.EventPublisher
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlaceUpdateService(
    private val placeUpdatePort: PlaceUpdatePort,
    private val placeFindPort: PlaceFindPort,
    private val imageWebClient: ImageWebClient,
    private val eventPublisher: EventPublisher
): PlaceUpdateUseCase {

    @Transactional
    override suspend fun updateContent(command: PlaceUpdateCommand): Place {
        val existingPlace = placeFindPort.findByPlaceId(command.placeId)

        if (existingPlace.authorId != command.authorId) {
            throw UnauthorizedPlaceAccessException()
        }
        return placeUpdatePort.updateContent(placeId = command.placeId, command = command)
    }


    // Todo: 도메인 로직이 commit 된다면 원래 이미지는 삭제해주어야 하기때문에 이벤트 생성
    // Transaction phase 가 AFTER_COMMIT, AFTER_ROLLBACK 인 경우 해당 이벤트 발행
    @Transactional
    override suspend fun updateImage(placeId: Long, authorId: Long, image: FilePart) {
        val existingPlace = placeFindPort.findByPlaceId(placeId)

        if (existingPlace.authorId != authorId) {
            throw UnauthorizedPlaceAccessException()
        }

        val oldImageUrl: String? = existingPlace.imageUrl
        val newImageUrl = imageWebClient.uploadImage(image)

        placeUpdatePort.updateImage(placeId, newImageUrl)

        oldImageUrl?.let {
            eventPublisher.publishImageRollbackEvent(ImageRollbackEvent(oldImageUrl))
        }
    }

}