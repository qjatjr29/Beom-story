package com.beomsic.placeservice.application.service

import com.beomsic.common.infra.kafka.event.ImageRollbackEvent
import com.beomsic.placeservice.adapter.out.external.service.ImageWebClient
import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.placeservice.application.port.`in`.usecase.PlaceCreateUseCase
import com.beomsic.placeservice.application.port.out.PlaceCreatePort
import com.beomsic.placeservice.domain.Place
import com.beomsic.placeservice.domain.exception.ServerException
import com.beomsic.placeservice.infra.EventPublisher
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlaceCreateService(
    private val placeCreatePort: PlaceCreatePort,
    private val imageWebClient: ImageWebClient,
    private val eventPublisher: EventPublisher
) : PlaceCreateUseCase {

    @Transactional
    override suspend fun execute(command: PlaceCreateCommand, image: FilePart?): Place {
        var uploadedImageUrl: String? = null
        try {
            // 1. 이미지 비동기 업로드
            if (image != null) {
                uploadedImageUrl = imageWebClient.uploadImage(image = image)
                command.imageUrl = uploadedImageUrl
            }
            // 2. DB 트랜잭션 내에서 장소 엔티티 생성
            return placeCreatePort.create(command)
        } catch (ex: Exception) {
            if (uploadedImageUrl != null) {
                eventPublisher.publishImageRollbackEvent(ImageRollbackEvent(uploadedImageUrl))
            }
            throw ServerException(ex.message ?: "Server Exception", ex)
        }
    }
}